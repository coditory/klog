package com.coditory.klog.sink

import com.coditory.klog.LogEvent
import com.coditory.klog.LogStreamListener
import com.coditory.klog.config.KlogErrLogger
import com.coditory.klog.publish.BlockingPublisher
import com.coditory.klog.publish.LogPublisher

internal class BlockingLogSink(
    private val publisher: BlockingPublisher,
    private val listener: LogStreamListener,
    private val klogErrLogger: KlogErrLogger,
) : LogPublisher {
    override suspend fun flush() {
        // deliberately empty
    }

    override suspend fun stopAndFlush() {
        // deliberately empty
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

    override fun publish(event: LogEvent) {
        publishBlocking(event)
    }
}
