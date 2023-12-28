package com.coditory.klog.publish

import com.coditory.klog.LogEvent
import com.coditory.klog.LogListener
import com.coditory.klog.LogPublisherDescriptor
import com.coditory.klog.config.KlogErrLogger

interface LogPublisher : FlushablePublisher, BlockingPublisher {
    fun publish(event: LogEvent)

    companion object {
        internal fun withListener(
            publisher: LogPublisher,
            descriptor: LogPublisherDescriptor,
            listener: LogListener,
            klogErrLogger: KlogErrLogger,
        ) = SyncLogPublisherWithListener(publisher, descriptor, listener, klogErrLogger)
    }
}

internal class SyncLogPublisherWithListener(
    private val publisher: LogPublisher,
    private val descriptor: LogPublisherDescriptor,
    private val listener: LogListener,
    private val klogErrLogger: KlogErrLogger,
) : LogPublisher {
    override suspend fun flush() {
        publisher.flush()
    }

    override suspend fun stopAndFlush() {
        publisher.stopAndFlush()
    }

    override fun publishBlocking(event: LogEvent) {
        listener.onReceived(descriptor, event)
        try {
            publisher.publishBlocking(event)
            listener.onPublished(descriptor, event)
        } catch (e: Throwable) {
            klogErrLogger.log { "${publisher::class.simpleName}: Could not publish log. Cause: " + e.stackTraceToString() }
            listener.onDropped(descriptor, event, e)
        }
    }

    override suspend fun publishSuspending(event: LogEvent) {
        listener.onReceived(descriptor, event)
        try {
            publisher.publishSuspending(event)
            listener.onPublished(descriptor, event)
        } catch (e: Throwable) {
            klogErrLogger.log { "${publisher::class.simpleName}: Could not publish log. Cause: " + e.stackTraceToString() }
            listener.onDropped(descriptor, event, e)
        }
    }

    override fun publish(event: LogEvent) {
        listener.onReceived(descriptor, event)
        try {
            publisher.publish(event)
            listener.onPublished(descriptor, event)
        } catch (e: Throwable) {
            klogErrLogger.log { "${publisher::class.simpleName}: Could not publish log. Cause: " + e.stackTraceToString() }
            listener.onDropped(descriptor, event, e)
        }
    }
}
