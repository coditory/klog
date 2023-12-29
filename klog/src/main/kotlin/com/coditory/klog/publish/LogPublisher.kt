package com.coditory.klog.publish

import com.coditory.klog.LogEvent

interface LogPublisher : FlushablePublisher, BlockingPublisher {
    fun publish(event: LogEvent)
}

interface AsyncLogPublisher : FlushablePublisher, BlockingPublisher {
    suspend fun publishAsync(event: LogEvent)
}

interface BatchLogPublisher : FlushablePublisher, BlockingPublisher {
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
