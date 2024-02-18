package com.coditory.klog.shared

import com.coditory.klog.LogEvent
import com.coditory.klog.LogListener
import com.coditory.klog.LogPublisherDescriptor
import com.coditory.klog.LogStreamDescriptor

class InMemoryLogListener : LogListener {
    private val received = mutableListOf<String>()

    @Synchronized
    fun received(): List<String> {
        return received
    }

    @Synchronized
    fun clear() {
        received.clear()
    }

    @Synchronized
    override fun onLogStarted(event: LogEvent) {
        received.add("LogStart: ${event.message}")
    }

    @Synchronized
    override fun onLogEnded(event: LogEvent) {
        received.add("LogEnd: ${event.message}")
    }

    @Synchronized
    override fun onStreamStarted(
        stream: LogStreamDescriptor,
        event: LogEvent,
    ) {
        received.add("StreamStart: ${event.message}")
    }

    @Synchronized
    override fun onStreamEnded(
        stream: LogStreamDescriptor,
        event: LogEvent,
    ) {
        received.add("StreamEnd: ${event.message}")
    }

    @Synchronized
    override fun onPublishStarted(
        publisher: LogPublisherDescriptor,
        event: LogEvent,
    ) {
        received.add("Received: ${event.message}")
    }

    @Synchronized
    override fun onPublishStarted(
        publisher: LogPublisherDescriptor,
        events: List<LogEvent>,
    ) {
        received.add("Received: ${events.map { it.message }}")
    }

    @Synchronized
    override fun onPublishDropped(
        publisher: LogPublisherDescriptor,
        event: LogEvent,
        e: Throwable?,
    ) {
        received.add("Dropped: ${event.message}")
    }

    @Synchronized
    override fun onPublishDropped(
        publisher: LogPublisherDescriptor,
        events: List<LogEvent>,
        e: Throwable?,
    ) {
        received.add("Dropped: ${events.map { it.message }}")
    }

    @Synchronized
    override fun onPublishEnded(
        publisher: LogPublisherDescriptor,
        event: LogEvent,
    ) {
        received.add("Published: ${event.message}")
    }

    @Synchronized
    override fun onPublishEnded(
        publisher: LogPublisherDescriptor,
        events: List<LogEvent>,
    ) {
        received.add("Published: ${events.map { it.message }}")
    }
}
