package com.coditory.klog

internal interface KlogLoggerHolder : KlogLogger {
    fun replace(logger: EmittingKlogLogger)

    fun getLogger(): EmittingKlogLogger

    companion object {
        fun create(
            volatile: Boolean,
            logger: EmittingKlogLogger,
        ): KlogLoggerHolder {
            return if (volatile) {
                VolatileKlogLoggerHolder(logger)
            } else {
                NonVolatileKlogLoggerHolder(logger)
            }
        }
    }
}

private class NonVolatileKlogLoggerHolder(
    private var logger: EmittingKlogLogger,
) : KlogLoggerHolder {
    override fun getLogger(): EmittingKlogLogger = this.logger

    override fun blocking(): BlockingKlogLogger = BlockingKlogLoggerHolder(this)

    override fun suspending(): SuspendingKlogLogger = SuspendingKlogLoggerHolder(this)

    override fun replace(logger: EmittingKlogLogger) {
        this.logger = logger
    }

    override fun name(): String = logger.name()

    override fun isLevelEnabled(level: Level): Boolean = logger.isLevelEnabled(level)

    override fun log(
        level: Level,
        message: () -> String,
    ) = logger.log(level, message, async = true)

    override fun log(
        level: Level,
        throwable: Throwable?,
        message: () -> String,
    ) = logger.log(level, throwable, message, async = true)

    override fun logEntry(
        level: Level,
        entry: () -> LogEntry,
    ) = logger.logEntry(level, entry, async = true)
}

private class VolatileKlogLoggerHolder(
    @Volatile private var logger: EmittingKlogLogger,
) : KlogLoggerHolder {
    override fun blocking(): BlockingKlogLogger = BlockingKlogLoggerHolder(this)

    override fun suspending(): SuspendingKlogLogger = SuspendingKlogLoggerHolder(this)

    override fun getLogger(): EmittingKlogLogger = this.logger

    override fun replace(logger: EmittingKlogLogger) {
        this.logger = logger
    }

    override fun name(): String = logger.name()

    override fun isLevelEnabled(level: Level): Boolean = logger.isLevelEnabled(level)

    override fun log(
        level: Level,
        message: () -> String,
    ) = logger.log(level, message, async = true)

    override fun log(
        level: Level,
        throwable: Throwable?,
        message: () -> String,
    ) = logger.log(level, throwable, message, async = true)

    override fun logEntry(
        level: Level,
        entry: () -> LogEntry,
    ) = logger.logEntry(level, entry, async = true)
}

private class BlockingKlogLoggerHolder(
    private var holder: KlogLoggerHolder,
) : BlockingKlogLogger {
    override fun name(): String = holder.name()

    override fun isLevelEnabled(level: Level): Boolean = holder.isLevelEnabled(level)

    override fun log(
        level: Level,
        message: () -> String,
    ) = holder.getLogger().log(level, message, async = false)

    override fun log(
        level: Level,
        throwable: Throwable?,
        message: () -> String,
    ) = holder.getLogger().log(level, throwable, message, async = false)

    override fun logEntry(
        level: Level,
        entry: () -> LogEntry,
    ) = holder.getLogger().logEntry(level, entry, async = false)
}

private class SuspendingKlogLoggerHolder(
    private var holder: KlogLoggerHolder,
) : SuspendingKlogLogger {
    override fun name(): String = holder.name()

    override fun isLevelEnabled(level: Level): Boolean = holder.isLevelEnabled(level)

    override suspend fun log(
        level: Level,
        message: () -> String,
    ) = holder.getLogger().logSuspending(level, message)

    override suspend fun log(
        level: Level,
        throwable: Throwable?,
        message: () -> String,
    ) = holder.getLogger().logSuspending(level, throwable, message)

    override suspend fun logEntry(
        level: Level,
        entry: () -> LogEntry,
    ) = holder.getLogger().logEntrySuspending(level, entry)
}
