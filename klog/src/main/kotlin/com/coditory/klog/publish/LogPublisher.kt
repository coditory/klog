package com.coditory.klog.publish

import com.coditory.klog.LogEvent
import com.coditory.klog.LogPublisherListener
import com.coditory.klog.config.KlogErrLogger

interface LogPublisher : FlushablePublisher, BlockingPublisher {
    fun publish(event: LogEvent)
}

interface AsyncLogPublisher : FlushablePublisher, BlockingPublisher {
    suspend fun publishAsync(event: LogEvent)

    suspend fun publishBatchAsync(events: List<LogEvent>)
}

interface FlushablePublisher {
    suspend fun flush() {}

    suspend fun stopAndFlush() {}
}

interface BlockingPublisher {
    fun publishBlocking(event: LogEvent)

    suspend fun publishSuspending(event: LogEvent) {
        publishBlocking(event)
    }
}

internal class InstrumentedAsyncLogPublisher(
    private val listener: LogPublisherListener,
    private val publisher: AsyncLogPublisher,
    private val klogErrLogger: KlogErrLogger,
) : AsyncLogPublisher {
    override suspend fun publishAsync(event: LogEvent) {
        listener.onPublishStarted(event)
        try {
            publisher.publishAsync(event)
            listener.onPublishEnded(event)
        } catch (e: Throwable) {
            klogErrLogger.logDropped(e)
            listener.onPublishDropped(event, e)
        }
    }

    override suspend fun publishBatchAsync(events: List<LogEvent>) {
        listener.onPublishStarted(events)
        try {
            publisher.publishBatchAsync(events)
            listener.onPublishEnded(events)
        } catch (e: Throwable) {
            klogErrLogger.logDropped(e)
            listener.onPublishDropped(events, e)
        }
    }

    override fun publishBlocking(event: LogEvent) {
        listener.onPublishStarted(event)
        try {
            publisher.publishBlocking(event)
            listener.onPublishEnded(event)
        } catch (e: Throwable) {
            klogErrLogger.logDropped(e)
            listener.onPublishDropped(event, e)
        }
    }
}

internal class InstrumentedBlockingPublisher(
    private val listener: LogPublisherListener,
    private val publisher: BlockingPublisher,
    private val klogErrLogger: KlogErrLogger,
) : BlockingPublisher {
    override suspend fun publishSuspending(event: LogEvent) {
        listener.onPublishStarted(event)
        try {
            publisher.publishSuspending(event)
            listener.onPublishEnded(event)
        } catch (e: Throwable) {
            klogErrLogger.logDropped(e)
            listener.onPublishDropped(event, e)
        }
    }

    override fun publishBlocking(event: LogEvent) {
        listener.onPublishStarted(event)
        try {
            publisher.publishBlocking(event)
            listener.onPublishEnded(event)
        } catch (e: Throwable) {
            klogErrLogger.logDropped(e)
            listener.onPublishDropped(event, e)
        }
    }
}
