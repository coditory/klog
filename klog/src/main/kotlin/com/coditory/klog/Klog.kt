package com.coditory.klog

import com.coditory.klog.config.KlogConfig
import com.coditory.klog.config.KlogConfigBuilder
import com.coditory.klog.config.klogConfig
import com.coditory.klog.publish.SystemOutPublisher
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

fun klog(init: KlogConfigBuilder.() -> Unit): Klog {
    val config = KlogConfigBuilder()
    config.init()
    return Klog(config.build())
}

class Klog(config: KlogConfig) {
    private val loggers = ConcurrentHashMap<String, KlogLoggerHolder>()
    private var context: KlogContext = KlogContext.build(config)

    @Synchronized
    private fun reconfigure(config: KlogConfig) {
        this.context = KlogContext.build(config)
        for ((name, holder) in loggers) {
            holder.replace(EmittingKlogLogger.create(context, name))
        }
    }

    @Synchronized
    fun stopAndFlush() {
        runBlocking {
            context.streams.forEach { it.stopAndFlush() }
        }
    }

    @Synchronized
    fun flush() {
        runBlocking {
            context.streams.forEach { it.flush() }
        }
    }

    fun loggerFor(name: String): KlogLogger {
        return loggers.getOrPut(name) {
            val logger = EmittingKlogLogger.create(context, name)
            KlogLoggerHolder.create(context.synchronizeLoggers, logger)
        }
    }

    fun logger(name: String): KlogLogger = loggerFor(name)

    fun logger(ownerClass: KClass<*>): KlogLogger = loggerFor(
        classNameOf(ownerClass) ?: throw IllegalArgumentException("Could not extract logger name from class name"),
    )

    inline fun <reified T> logger(): KlogLogger = logger(T::class)

    private fun classNameOf(ownerClass: KClass<*>): String? {
        return if (ownerClass.isCompanion) {
            ownerClass.java.enclosingClass.name
        } else {
            ownerClass.java.name
        }
    }

    companion object {
        private val GLOBAL_INSTANCE =
            Klog(
                klogConfig {
                    stream {
                        asyncPublisher(SystemOutPublisher.plainText(development = true))
                    }
                },
            )

        fun configure(config: KlogConfig) {
            GLOBAL_INSTANCE.reconfigure(config)
        }

        fun configure(init: KlogConfigBuilder.() -> Unit) {
            val config = KlogConfigBuilder()
            config.init()
            GLOBAL_INSTANCE.reconfigure(config.build())
        }

        fun flush() {
            GLOBAL_INSTANCE.flush()
        }

        fun stopAndFlush() {
            GLOBAL_INSTANCE.stopAndFlush()
        }

        fun logger(name: String): KlogLogger = GLOBAL_INSTANCE.logger(name)

        fun logger(ownerClass: KClass<*>): KlogLogger = GLOBAL_INSTANCE.logger(ownerClass)

        inline fun <reified T> logger(): KlogLogger = logger(T::class)
    }
}
