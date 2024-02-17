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
        listener.received(event)
        try {
            publisher.publishBlocking(event)
            listener.published(event)
        } catch (e: Throwable) {
            klogErrLogger.logDropped(e)
            listener.dropped(event, e)
        }
    }

    override suspend fun publishSuspending(event: LogEvent) {
        listener.received(event)
        try {
            publisher.publishSuspending(event)
            listener.published(event)
        } catch (e: Throwable) {
            klogErrLogger.logDropped(e)
            listener.dropped(event, e)
        }
    }

    override suspend fun publishAsync(event: LogEvent) {
        listener.received(event)
        try {
            publisher.publishBatchAsync(listOf(event))
            listener.published(event)
        } catch (e: Throwable) {
            klogErrLogger.logDropped(e)
            listener.dropped(event, e)
        }
    }

    override suspend fun publishBatchAsync(events: List<LogEvent>) {
        listener.received(events)
        try {
            mutex.withLock {
                publisher.publishBatchAsync(events)
                listener.published(events)
            }
        } catch (e: Throwable) {
            klogErrLogger.logDropped(e)
            listener.dropped(events, e)
        }
    }
}
