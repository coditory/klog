package com.coditory.klog.jpl

import com.coditory.klog.KlogLogger
import com.coditory.klog.Level
import java.text.MessageFormat
import java.util.MissingResourceException
import java.util.ResourceBundle

class SystemLogger(
    private val logger: KlogLogger,
) : System.Logger {
    override fun getName() = logger.name()

    override fun isLoggable(level: System.Logger.Level): Boolean {
        return when (level) {
            System.Logger.Level.ALL -> true
            System.Logger.Level.OFF -> false
            else -> {
                val klogLevel = translateLevel(level)
                return if (klogLevel == null) {
                    println("Unsupported klog level: $level")
                    logger.isLevelEnabled(Level.TRACE)
                } else {
                    logger.isLevelEnabled(klogLevel)
                }
            }
        }
    }

    private fun translateLevel(level: System.Logger.Level): Level? {
        return when (level) {
            System.Logger.Level.ALL -> null
            System.Logger.Level.TRACE -> Level.TRACE
            System.Logger.Level.DEBUG -> Level.DEBUG
            System.Logger.Level.INFO -> Level.INFO
            System.Logger.Level.WARNING -> Level.WARN
            System.Logger.Level.ERROR -> Level.ERROR
            System.Logger.Level.OFF -> null
        }
    }

    override fun log(
        level: System.Logger.Level,
        bundle: ResourceBundle?,
        msg: String?,
        thrown: Throwable?,
    ) {
        log(level, bundle, msg, thrown, null as Array<Any?>?)
    }

    override fun log(
        level: System.Logger.Level,
        bundle: ResourceBundle?,
        format: String?,
        params: Array<out Any?>?,
    ) {
        log(level, bundle, format, null, params)
    }

    /**
     * Single point of processing taking all possible parameters.
     */
    private fun log(
        level: System.Logger.Level,
        bundle: ResourceBundle?,
        msg: String?,
        thrown: Throwable?,
        params: Array<out Any?>?,
    ) {
        if (level == System.Logger.Level.OFF) {
            return
        }
        if (level == System.Logger.Level.ALL) {
            performLog(Level.TRACE, bundle, msg, thrown, params)
            return
        }
        val klogLevel = translateLevel(level)
        if (klogLevel == null) {
            println("Unsupported klog level: $level")
            performLog(Level.TRACE, bundle, msg, thrown, params)
            return
        }
        performLog(klogLevel, bundle, msg, thrown, params)
    }

    private fun performLog(
        level: Level,
        bundle: ResourceBundle?,
        msg: String?,
        thrown: Throwable?,
        params: Array<out Any?>?,
    ) {
        if (!logger.isLevelEnabled(level)) return
        logger.log(level, thrown) {
            message(bundle, msg, params)
        }
    }

    private fun message(bundle: ResourceBundle?, msg: String?, params: Array<out Any?>?): String {
        var message = resourceStringOrMessage(bundle, msg)
        if (params != null && params.isNotEmpty()) {
            message = MessageFormat.format(message, *params)
        }
        return message
    }

    private fun resourceStringOrMessage(bundle: ResourceBundle?, msg: String?): String {
        if (bundle == null || msg == null) {
            return msg!!
        }
        // ResourceBundle::getString throws:
        //
        // * NullPointerException for null keys
        // * ClassCastException if the message is no string
        // * MissingResourceException if there is no message for the key
        return try {
            bundle.getString(msg)
        } catch (_: MissingResourceException) {
            msg
        } catch (_: ClassCastException) {
            bundle.getObject(msg).toString()
        }
    }
}
