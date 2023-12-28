package com.coditory.klog.slf4j

import com.coditory.klog.KlogLogger
import com.coditory.klog.Level
import org.slf4j.Marker
import org.slf4j.helpers.LegacyAbstractLogger
import org.slf4j.helpers.MessageFormatter

class Slf4jLoggerWrapper(
    private val logger: KlogLogger,
) : LegacyAbstractLogger() {
    override fun getName(): String = logger.name()

    override fun isTraceEnabled(): Boolean = logger.isTraceEnabled()

    override fun trace(msg: String?) = logger.trace { msg ?: "" }

    override fun trace(
        format: String?,
        arg: Any?,
    ) = logger.trace {
        MessageFormatter.format(format, arg).message ?: ""
    }

    override fun trace(
        format: String?,
        arg1: Any?,
        arg2: Any?,
    ) = logger.trace { MessageFormatter.format(format, arg1, arg2).message ?: "" }

    override fun trace(
        format: String?,
        vararg arguments: Any?,
    ) = logger.trace { MessageFormatter.format(format, arguments).message ?: "" }

    override fun trace(
        msg: String?,
        t: Throwable?,
    ) = if (t != null) {
        logger.trace(t) { msg ?: "" }
    } else {
        logger.trace { msg ?: "" }
    }

    override fun isDebugEnabled(): Boolean = logger.isDebugEnabled()

    override fun debug(msg: String?) = logger.debug { msg ?: "" }

    override fun debug(
        format: String?,
        arg: Any?,
    ) = logger.debug { MessageFormatter.format(format, arg).message ?: "" }

    override fun debug(
        format: String?,
        arg1: Any?,
        arg2: Any?,
    ) = logger.debug { MessageFormatter.format(format, arg1, arg2).message ?: "" }

    override fun debug(
        format: String?,
        vararg arguments: Any?,
    ) = logger.debug { MessageFormatter.format(format, arguments).message ?: "" }

    override fun debug(
        msg: String?,
        t: Throwable?,
    ) = if (t != null) {
        logger.debug(t) { msg ?: "" }
    } else {
        logger.debug { msg ?: "" }
    }

    override fun isInfoEnabled(): Boolean = logger.isInfoEnabled()

    override fun info(msg: String?) = logger.info { msg ?: "" }

    override fun info(
        format: String?,
        arg: Any?,
    ) = logger.info { MessageFormatter.format(format, arg).message ?: "" }

    override fun info(
        format: String?,
        arg1: Any?,
        arg2: Any?,
    ) = logger.info { MessageFormatter.format(format, arg1, arg2).message ?: "" }

    override fun info(
        format: String?,
        vararg arguments: Any?,
    ) = logger.info { MessageFormatter.format(format, arguments).message ?: "" }

    override fun info(
        msg: String?,
        t: Throwable?,
    ) = if (t != null) {
        logger.info(t) { msg ?: "" }
    } else {
        logger.info { msg ?: "" }
    }

    override fun isWarnEnabled(): Boolean = logger.isWarnEnabled()

    override fun warn(msg: String?) = logger.warn { msg ?: "" }

    override fun warn(
        format: String?,
        arg: Any?,
    ) = logger.warn { MessageFormatter.format(format, arg).message ?: "" }

    override fun warn(
        format: String?,
        arg1: Any?,
        arg2: Any?,
    ) = logger.warn { MessageFormatter.format(format, arg1, arg2).message ?: "" }

    override fun warn(
        format: String?,
        vararg arguments: Any?,
    ) = logger.warn { MessageFormatter.format(format, arguments).message ?: "" }

    override fun warn(
        msg: String?,
        t: Throwable?,
    ) = if (t != null) {
        logger.warn(t) { msg ?: "" }
    } else {
        logger.warn { msg ?: "" }
    }

    override fun isErrorEnabled(): Boolean = logger.isErrorEnabled()

    override fun error(msg: String?) = logger.error { msg ?: "" }

    override fun error(
        format: String?,
        arg: Any?,
    ) = logger.error { MessageFormatter.format(format, arg).message ?: "" }

    override fun error(
        format: String?,
        arg1: Any?,
        arg2: Any?,
    ) = logger.error { MessageFormatter.format(format, arg1, arg2).message ?: "" }

    override fun error(
        format: String?,
        vararg arguments: Any?,
    ) = logger.error { MessageFormatter.format(format, arguments).message ?: "" }

    override fun error(
        msg: String?,
        t: Throwable?,
    ) = if (t != null) {
        logger.error(t) { msg ?: "" }
    } else {
        logger.error { msg ?: "" }
    }

    override fun getFullyQualifiedCallerName(): String? = KlogLogger::class.qualifiedName

    override fun handleNormalizedLoggingCall(
        level: org.slf4j.event.Level?,
        marker: Marker?,
        messagePattern: String?,
        arguments: Array<out Any>?,
        throwable: Throwable?,
    ) {
        logger.log(klogLevel(level), throwable) {
            MessageFormatter.format(messagePattern, arguments).message
        }
    }

    private fun klogLevel(slf4jLevel: org.slf4j.event.Level?): Level =
        when (slf4jLevel) {
            org.slf4j.event.Level.TRACE -> Level.TRACE
            org.slf4j.event.Level.DEBUG -> Level.DEBUG
            org.slf4j.event.Level.INFO -> Level.INFO
            org.slf4j.event.Level.WARN -> Level.WARN
            org.slf4j.event.Level.ERROR -> Level.ERROR
            else -> Level.INFO
        }
}
