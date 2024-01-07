package com.coditory.klog.publish

import com.coditory.klog.LogEvent
import com.coditory.klog.config.KlogErrLogger
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class SerialBatchLogPublisher(
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
        publisher.publishBatchAsync(listOf(event))
    }

    override suspend fun publishBatchAsync(events: List<LogEvent>) {
        listener.received(events)
        try {
            mutex.withLock {
                publisher.publishBatchAsync(events)
                listener.published(events)
            }
        } catch (e: Throwable) {
            klogErrLogger.log { "${this::class.simpleName}: Could not publish log. Cause: " + e.stackTraceToString() }
            listener.dropped(events, e)
        }
    }
}
