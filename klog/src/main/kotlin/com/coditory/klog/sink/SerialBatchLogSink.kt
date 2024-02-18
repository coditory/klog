package com.coditory.klog.sink

import com.coditory.klog.LogEvent
import com.coditory.klog.LogStreamListener
import com.coditory.klog.config.KlogErrLogger
import com.coditory.klog.publish.AsyncLogPublisher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class SerialBatchLogSink(
    private val publisher: AsyncLogPublisher,
    private val listener: LogStreamListener = LogStreamListener.NOOP,
    private val klogErrLogger: KlogErrLogger = KlogErrLogger.STDERR,
) : AsyncLogPublisher {
    private val mutex = Mutex()

    override suspend fun stopAndFlush() {
        publisher.stopAndFlush()
    }

    override fun publishBlocking(event: LogEvent) {
        try {
            publisher.publishBlocking(event)
        } catch (e: Throwable) {
            klogErrLogger.logDropped(e)
            listener.onStreamDropped(event, e)
        }
    }

    override suspend fun publishSuspending(event: LogEvent) {
        try {
            publisher.publishSuspending(event)
        } catch (e: Throwable) {
            klogErrLogger.logDropped(e)
            listener.onStreamDropped(event, e)
        }
    }

    override suspend fun publishAsync(event: LogEvent) {
        try {
            publisher.publishBatchAsync(listOf(event))
        } catch (e: Throwable) {
            klogErrLogger.logDropped(e)
            listener.onStreamDropped(event, e)
        }
    }

    override suspend fun publishBatchAsync(events: List<LogEvent>) {
        try {
            mutex.withLock {
                publisher.publishBatchAsync(events)
            }
        } catch (e: Throwable) {
            klogErrLogger.logDropped(e)
            listener.onStreamDropped(events, e)
        }
    }
}
