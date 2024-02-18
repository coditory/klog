package com.coditory.klog.publish

import com.coditory.klog.LogEvent
import com.coditory.klog.LogListener
import com.coditory.klog.LogPublisherDescriptor
import com.coditory.klog.config.KlogErrLogger

internal class BlockingLogSink(
    private val publisher: BlockingPublisher,
    private val descriptor: LogPublisherDescriptor,
    private val listener: LogListener,
    private val klogErrLogger: KlogErrLogger,
) : LogPublisher {
    override suspend fun flush() {
        // deliberately empty
    }

    override suspend fun stopAndFlush() {
        // deliberately empty
    }

    override fun publishBlocking(event: LogEvent) {
        listener.onPublishStarted(descriptor, event)
        try {
            publisher.publishBlocking(event)
            listener.onPublishEnded(descriptor, event)
        } catch (e: Throwable) {
            klogErrLogger.logDropped(e)
            listener.onPublishDropped(descriptor, event, e)
        }
    }

    override suspend fun publishSuspending(event: LogEvent) {
        listener.onPublishStarted(descriptor, event)
        try {
            publisher.publishSuspending(event)
            listener.onPublishEnded(descriptor, event)
        } catch (e: Throwable) {
            klogErrLogger.logDropped(e)
            listener.onPublishDropped(descriptor, event, e)
        }
    }

    override fun publish(event: LogEvent) {
        publishBlocking(event)
    }
}
