package com.coditory.klog.publish

import com.coditory.klog.LogEvent
import com.coditory.klog.config.KlogErrLogger
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class SerialLogPublisher(
    private val publisher: AsyncLogPublisher,
    private val listener: LogPublisherListener = LogPublisherListener.NOOP,
    private val klogErrLogger: KlogErrLogger = KlogErrLogger.STDERR,
) : AsyncLogPublisher {
    private val mutex = Mutex()

    override suspend fun stopAndFlush() {
        publisher.stopAndFlush()
    }

    override fun publishBlocking(event: LogEvent) {
        publisher.publishBlocking(event)
    }

    override suspend fun publishSuspending(event: LogEvent) {
        publisher.publishSuspending(event)
    }

    override suspend fun publishAsync(event: LogEvent) {
        listener.received(event)
        try {
            mutex.withLock {
                publisher.publishAsync(event)
                listener.published(event)
            }
        } catch (e: Throwable) {
            klogErrLogger.log { "${this::class.simpleName}: Could not publish log. Cause: " + e.stackTraceToString() }
            listener.dropped(event, e)
        }
    }

    override suspend fun publishBatchAsync(events: List<LogEvent>) {
        events.forEach { publishAsync(it) }
    }
}
