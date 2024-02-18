package com.coditory.klog

import com.coditory.klog.config.LogFilter
import com.coditory.klog.config.LogPrioritizer
import com.coditory.klog.publish.LogPublisher

internal class LogStream(
    private val filter: LogFilter,
    private val publishers: List<LogPublisher>,
    private val stopOnMatch: Boolean,
    private val prioritizer: LogPrioritizer,
    private val listener: LogStreamListener,
) {
    suspend fun stopAndFlush() {
        publishers.forEach { it.stopAndFlush() }
    }

    suspend fun flush() {
        publishers.forEach { it.flush() }
    }

    fun emit(
        event: LogEvent,
        async: Boolean = true,
    ): Boolean {
        if (filter.matches(event.level, event.logger)) {
            listener.onStreamStarted(event)
            val eventWithPriority = setupPriority(event)
            for (publisher in publishers) {
                if (async) {
                    publisher.publish(eventWithPriority)
                } else {
                    publisher.publishBlocking(eventWithPriority)
                }
            }
            listener.onStreamEnded(event)
            return stopOnMatch
        }
        return false
    }

    suspend fun emitSuspending(event: LogEvent): Boolean {
        if (filter.matches(event.level, event.logger)) {
            listener.onStreamStarted(event)
            val eventWithPriority = setupPriority(event)
            for (publisher in publishers) {
                publisher.publishSuspending(eventWithPriority)
            }
            listener.onStreamEnded(event)
            return stopOnMatch
        }
        return false
    }

    fun stopOnMatch(): Boolean {
        return stopOnMatch
    }

    fun matches(
        level: Level,
        loggerName: String,
    ): Boolean {
        return filter.matches(level, loggerName)
    }

    private fun setupPriority(event: LogEvent): LogEvent {
        val priority = prioritizer.priority(event)
        return if (event.priority == priority) {
            event
        } else {
            event.copy(priority = priority)
        }
    }
}
