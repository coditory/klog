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
    override fun onLogStart(event: LogEvent) {
        received.add("LogStart: ${event.message}")
    }

    @Synchronized
    override fun onLogEnd(event: LogEvent) {
        received.add("LogEnd: ${event.message}")
    }

    @Synchronized
    override fun onStreamStart(
        stream: LogStreamDescriptor,
        event: LogEvent,
    ) {
        received.add("StreamStart: ${event.message}")
    }

    @Synchronized
    override fun onStreamEnd(
        stream: LogStreamDescriptor,
        event: LogEvent,
    ) {
        received.add("StreamEnd: ${event.message}")
    }

    @Synchronized
    override fun onReceived(
        publisher: LogPublisherDescriptor,
        event: LogEvent,
    ) {
        received.add("Received: ${event.message}")
    }

    @Synchronized
    override fun onReceived(
        publisher: LogPublisherDescriptor,
        events: List<LogEvent>,
    ) {
        received.add("Received: ${events.map { it.message }}")
    }

    @Synchronized
    override fun onDropped(
        publisher: LogPublisherDescriptor,
        event: LogEvent,
        e: Throwable?,
    ) {
        received.add("Dropped: ${event.message}")
    }

    @Synchronized
    override fun onDropped(
        publisher: LogPublisherDescriptor,
        events: List<LogEvent>,
        e: Throwable?,
    ) {
        received.add("Dropped: ${events.map { it.message }}")
    }

    @Synchronized
    override fun onPublished(
        publisher: LogPublisherDescriptor,
        event: LogEvent,
    ) {
        received.add("Published: ${event.message}")
    }

    @Synchronized
    override fun onPublished(
        publisher: LogPublisherDescriptor,
        events: List<LogEvent>,
    ) {
        received.add("Published: ${events.map { it.message }}")
    }
}
