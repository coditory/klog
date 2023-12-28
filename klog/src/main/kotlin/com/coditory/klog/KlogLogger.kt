package com.coditory.klog

interface KlogLogger : KlogLoggerBase {
    fun blocking(): BlockingKlogLogger

    fun suspending(): SuspendingKlogLogger
}

interface BlockingKlogLogger : KlogLoggerBase

interface KlogLoggerBase {
    fun name(): String

    fun isLevelEnabled(level: Level): Boolean

    fun isTraceEnabled(): Boolean = isLevelEnabled(Level.TRACE)

    fun isDebugEnabled(): Boolean = isLevelEnabled(Level.DEBUG)

    fun isInfoEnabled(): Boolean = isLevelEnabled(Level.INFO)

    fun isWarnEnabled(): Boolean = isLevelEnabled(Level.WARN)

    fun isErrorEnabled(): Boolean = isLevelEnabled(Level.ERROR)

    fun isFatalEnabled(): Boolean = isLevelEnabled(Level.FATAL)

    fun logEntry(
        level: Level,
        entry: () -> LogEntry,
    )

    fun log(
        level: Level,
        message: () -> String,
    )

    fun log(
        level: Level,
        throwable: Throwable?,
        message: () -> String,
    )

    fun traceEntry(entry: () -> LogEntry) = logEntry(Level.TRACE, entry)

    fun trace(message: () -> String) = log(Level.TRACE, message)

    fun trace(
        throwable: Throwable?,
        message: () -> String,
    ) = log(Level.TRACE, throwable, message)

    fun debugEntry(entry: () -> LogEntry) = logEntry(Level.DEBUG, entry)

    fun debug(message: () -> String) = log(Level.DEBUG, message)

    fun debug(
        throwable: Throwable?,
        message: () -> String,
    ) = log(Level.DEBUG, throwable, message)

    fun infoEntry(entry: () -> LogEntry) = logEntry(Level.INFO, entry)

    fun info(message: () -> String) = log(Level.INFO, message)

    fun info(
        throwable: Throwable?,
        message: () -> String,
    ) = log(Level.INFO, throwable, message)

    fun warnEntry(entry: () -> LogEntry) = logEntry(Level.WARN, entry)

    fun warn(message: () -> String) = log(Level.WARN, message)

    fun warn(
        throwable: Throwable?,
        message: () -> String,
    ) = log(Level.WARN, throwable, message)

    fun errorEntry(entry: () -> LogEntry) = logEntry(Level.ERROR, entry)

    fun error(message: () -> String) = log(Level.ERROR, message)

    fun error(
        throwable: Throwable?,
        message: () -> String,
    ) = log(Level.ERROR, throwable, message)

    fun fatalEntry(entry: () -> LogEntry) = logEntry(Level.FATAL, entry)

    fun fatal(message: () -> String) = log(Level.FATAL, message)

    fun fatal(
        throwable: Throwable?,
        message: () -> String,
    ) = log(Level.FATAL, throwable, message)
}

interface SuspendingKlogLogger {
    fun name(): String

    fun isLevelEnabled(level: Level): Boolean

    fun isTraceEnabled(): Boolean = isLevelEnabled(Level.TRACE)

    fun isDebugEnabled(): Boolean = isLevelEnabled(Level.DEBUG)

    fun isInfoEnabled(): Boolean = isLevelEnabled(Level.INFO)

    fun isWarnEnabled(): Boolean = isLevelEnabled(Level.WARN)

    fun isErrorEnabled(): Boolean = isLevelEnabled(Level.ERROR)

    fun isFatalEnabled(): Boolean = isLevelEnabled(Level.FATAL)

    suspend fun logEntry(
        level: Level,
        entry: () -> LogEntry,
    )

    suspend fun log(
        level: Level,
        message: () -> String,
    )

    suspend fun log(
        level: Level,
        throwable: Throwable?,
        message: () -> String,
    )

    suspend fun traceEntry(entry: () -> LogEntry) = logEntry(Level.TRACE, entry)

    suspend fun trace(message: () -> String) = log(Level.TRACE, message)

    suspend fun trace(
        throwable: Throwable?,
        message: () -> String,
    ) = log(Level.TRACE, throwable, message)

    suspend fun debugEntry(entry: () -> LogEntry) = logEntry(Level.DEBUG, entry)

    suspend fun debug(message: () -> String) = log(Level.DEBUG, message)

    suspend fun debug(
        throwable: Throwable?,
        message: () -> String,
    ) = log(Level.DEBUG, throwable, message)

    suspend fun infoEntry(entry: () -> LogEntry) = logEntry(Level.INFO, entry)

    suspend fun info(message: () -> String) = log(Level.INFO, message)

    suspend fun info(
        throwable: Throwable?,
        message: () -> String,
    ) = log(Level.INFO, throwable, message)

    suspend fun warnEntry(entry: () -> LogEntry) = logEntry(Level.WARN, entry)

    suspend fun warn(message: () -> String) = log(Level.WARN, message)

    suspend fun warn(
        throwable: Throwable?,
        message: () -> String,
    ) = log(Level.WARN, throwable, message)

    suspend fun errorEntry(entry: () -> LogEntry) = logEntry(Level.ERROR, entry)

    suspend fun error(message: () -> String) = log(Level.ERROR, message)

    suspend fun error(
        throwable: Throwable?,
        message: () -> String,
    ) = log(Level.ERROR, throwable, message)

    suspend fun fatalEntry(entry: () -> LogEntry) = logEntry(Level.FATAL, entry)

    suspend fun fatal(message: () -> String) = log(Level.FATAL, message)

    suspend fun fatal(
        throwable: Throwable?,
        message: () -> String,
    ) = log(Level.FATAL, throwable, message)
}
