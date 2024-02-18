package com.coditory.klog

import java.time.Clock
import java.time.ZonedDateTime

internal class EmittingKlogLogger(
    private val name: String,
    private val clock: Clock,
    private val minLogLevel: Level?,
    private val streams: Map<Level, List<LogStream>>,
    private val listener: LogListener,
) {
    fun name(): String = name

    fun isLevelEnabled(level: Level): Boolean = minLogLevel != null && minLogLevel <= level

    fun logEntry(
        level: Level,
        entry: () -> LogEntry,
        async: Boolean,
    ) {
        if (!isLevelEnabled(level)) return
        val resolved = entry()
        val event = createEvent(level, resolved.throwable, resolved.message, resolved.items)
        emitEvent(event, async)
    }

    fun log(
        level: Level,
        throwable: Throwable?,
        message: () -> String,
        async: Boolean,
    ) {
        if (!isLevelEnabled(level)) return
        val event = createEvent(level, throwable, message(), emptyMap())
        emitEvent(event, async)
    }

    fun log(
        level: Level,
        message: () -> String,
        async: Boolean,
    ) {
        if (!isLevelEnabled(level)) return
        val event = createEvent(level, null, message(), emptyMap())
        emitEvent(event, async)
    }

    suspend fun logEntrySuspending(
        level: Level,
        entry: () -> LogEntry,
    ) {
        if (!isLevelEnabled(level)) return
        val resolved = entry()
        val event = createEvent(level, resolved.throwable, resolved.message, resolved.items)
        emitEventSuspending(event)
    }

    suspend fun logSuspending(
        level: Level,
        throwable: Throwable?,
        message: () -> String,
    ) {
        if (!isLevelEnabled(level)) return
        val event = createEvent(level, throwable, message(), emptyMap())
        emitEventSuspending(event)
    }

    suspend fun logSuspending(
        level: Level,
        message: () -> String,
    ) {
        if (!isLevelEnabled(level)) return
        val event = createEvent(level, null, message(), emptyMap())
        emitEventSuspending(event)
    }

    private fun emitEvent(
        event: LogEvent,
        async: Boolean,
    ) {
        listener.onLogStarted(event)
        for (level in Level.higherLevelsOrEqual(event.level)) {
            for (stream in streams.getOrDefault(level, emptyList())) {
                val emitted = stream.emit(event, async)
                if (emitted && stream.stopOnMatch()) {
                    listener.onLogEnded(event)
                    return
                }
            }
        }
        listener.onLogEnded(event)
    }

    private suspend fun emitEventSuspending(event: LogEvent) {
        listener.onLogStarted(event)
        for (level in Level.higherLevelsOrEqual(event.level)) {
            for (stream in streams.getOrDefault(level, emptyList())) {
                val emitted = stream.emitSuspending(event)
                if (emitted && stream.stopOnMatch()) {
                    listener.onLogEnded(event)
                    return
                }
            }
        }
        listener.onLogEnded(event)
    }

    private fun createEvent(
        level: Level,
        throwable: Throwable?,
        message: String,
        items: Map<String, Any>,
    ): LogEvent {
        return LogEvent(
            logger = name,
            timestamp = ZonedDateTime.ofInstant(clock.instant(), clock.zone),
            thread = Thread.currentThread().name,
            level = level,
            message = message,
            throwable = throwable,
            context = LogThreadContext.getCopyOfContextMap(),
            items = items,
        )
    }

    companion object {
        fun create(
            context: KlogContext,
            name: String,
        ): EmittingKlogLogger {
            val streams = mutableMapOf<Level, List<LogStream>>()
            var minLogLevel: Level? = null
            for (stream in context.streams) {
                for (level in Level.levels()) {
                    if (stream.matches(level, name)) {
                        streams.getOrPut(level) { mutableListOf() }.addLast(stream)
                        if (minLogLevel == null || level < minLogLevel) {
                            minLogLevel = level
                        }
                    }
                }
            }
            return EmittingKlogLogger(
                name = name,
                streams = streams,
                minLogLevel = minLogLevel,
                clock = context.clock,
                listener = context.listener,
            )
        }
    }
}
