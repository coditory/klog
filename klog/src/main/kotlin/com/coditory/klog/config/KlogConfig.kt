package com.coditory.klog.config

import com.coditory.klog.LogListener
import java.time.Clock
import java.time.ZoneId

fun klogConfig(init: KlogConfigBuilder.() -> Unit): KlogConfig {
    val config = KlogConfigBuilder()
    config.init()
    return config.build()
}

data class KlogConfig(
    val streams: List<LogStreamConfig> = listOf(),
    val clock: Clock = Clock.systemDefaultZone(),
    val listener: LogListener = LogListener.NOOP,
    val klogErrLogger: KlogErrLogger = KlogErrLogger.STDERR,
    val synchronizeLoggers: Boolean = false,
) {
    companion object {
        fun builder(): KlogConfigBuilder {
            return KlogConfigBuilder()
        }
    }
}

@ScopedKlogConfig
class KlogConfigBuilder {
    private val streams: MutableList<LogStreamConfig> = mutableListOf()
    private var clock: Clock = Clock.systemDefaultZone()
    private var zoneId: ZoneId = clock.zone
    private var listener: LogListener = LogListener.NOOP
    private var klogErrLogger: KlogErrLogger = KlogErrLogger.STDERR
    private var synchronizeLoggers: Boolean = false

    fun clock(clock: Clock): KlogConfigBuilder {
        this.clock = clock
        return this
    }

    fun zoneId(zoneId: ZoneId): KlogConfigBuilder {
        this.zoneId = zoneId
        return this
    }

    fun listener(listener: LogListener): KlogConfigBuilder {
        this.listener = listener
        return this
    }

    fun klogErrLogger(klogErrLogger: KlogErrLogger): KlogConfigBuilder {
        this.klogErrLogger = klogErrLogger
        return this
    }

    fun synchronizeLoggers(synchronizeLoggers: Boolean): KlogConfigBuilder {
        this.synchronizeLoggers = synchronizeLoggers
        return this
    }

    fun stream(configure: LogStreamConfigBuilder.() -> Unit): KlogConfigBuilder {
        val builder = LogStreamConfigBuilder()
        configure(builder)
        return stream(builder.build())
    }

    fun stream(config: LogStreamConfig): KlogConfigBuilder {
        streams.addLast(config)
        return this
    }

    fun build(): KlogConfig {
        return KlogConfig(
            streams = streams,
            clock = clock,
            listener = listener,
            synchronizeLoggers = synchronizeLoggers,
            klogErrLogger = klogErrLogger,
        )
    }
}
