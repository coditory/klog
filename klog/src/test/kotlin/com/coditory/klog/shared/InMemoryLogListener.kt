package com.coditory.klog.shared

import com.coditory.klog.LogEvent
import com.coditory.klog.LogListener
import com.coditory.klog.LogPublisherListener
import com.coditory.klog.LogStreamListener

class InMemoryLogListener : LogListener, LogStreamListener, LogPublisherListener {
    private val received = mutableListOf<Event>()

    @Synchronized
    fun received(): List<Event> {
        return received
    }

    @Synchronized
    fun receivedSimplified(): List<String> {
        return received.map {
            if (it.single) {
                "${it.type}: ${it.logs.first().message}"
            } else {
                "${it.type}: ${it.logs.map { log -> log.message }}"
            }
        }
    }

    @Synchronized
    fun clear() {
        received.clear()
    }

    @Synchronized
    override fun onLogStarted(event: LogEvent) {
        received.add(
            Event(
                type = EventType.LogStarted,
                single = true,
                logs = listOf(event),
            ),
        )
    }

    @Synchronized
    override fun onLogEnded(event: LogEvent) {
        received.add(
            Event(
                type = EventType.LogEnded,
                single = true,
                logs = listOf(event),
            ),
        )
    }

    @Synchronized
    override fun onLogSkipped(event: LogEvent) {
        received.add(
            Event(
                type = EventType.LogSkipped,
                single = true,
                logs = listOf(event),
            ),
        )
    }

    @Synchronized
    override fun onStreamStarted(event: LogEvent) {
        received.add(
            Event(
                type = EventType.StreamStarted,
                single = true,
                logs = listOf(event),
            ),
        )
    }

    @Synchronized
    override fun onStreamEnded(event: LogEvent) {
        received.add(
            Event(
                type = EventType.StreamEnded,
                single = true,
                logs = listOf(event),
            ),
        )
    }

    @Synchronized
    override fun onStreamDropped(event: LogEvent, e: Throwable?) {
        received.add(
            Event(
                type = EventType.StreamDropped,
                single = true,
                logs = listOf(event),
                e = e,
            ),
        )
    }

    @Synchronized
    override fun onStreamDropped(events: List<LogEvent>, e: Throwable?) {
        received.add(
            Event(
                type = EventType.StreamDropped,
                single = false,
                logs = events,
                e = e,
            ),
        )
    }

    @Synchronized
    override fun onPublishStarted(event: LogEvent) {
        received.add(
            Event(
                type = EventType.PublishStarted,
                single = true,
                logs = listOf(event),
            ),
        )
    }

    @Synchronized
    override fun onPublishStarted(events: List<LogEvent>) {
        received.add(
            Event(
                type = EventType.PublishStarted,
                single = false,
                logs = events,
            ),
        )
    }

    @Synchronized
    override fun onPublishDropped(event: LogEvent, e: Throwable?) {
        received.add(
            Event(
                type = EventType.PublishDropped,
                single = true,
                logs = listOf(event),
                e = e,
            ),
        )
    }

    @Synchronized
    override fun onPublishDropped(events: List<LogEvent>, e: Throwable?) {
        received.add(
            Event(
                type = EventType.PublishDropped,
                single = false,
                logs = events,
                e = e,
            ),
        )
    }

    @Synchronized
    override fun onPublishEnded(event: LogEvent) {
        received.add(
            Event(
                type = EventType.PublishEnded,
                single = true,
                logs = listOf(event),
            ),
        )
    }

    @Synchronized
    override fun onPublishEnded(events: List<LogEvent>) {
        received.add(
            Event(
                type = EventType.PublishEnded,
                single = false,
                logs = events,
            ),
        )
    }

    data class Event(
        val type: EventType,
        val single: Boolean,
        val logs: List<LogEvent>,
        val e: Throwable? = null,
    )

    enum class EventType {
        LogStarted,
        LogEnded,
        LogSkipped,
        StreamStarted,
        StreamEnded,
        StreamDropped,
        PublishStarted,
        PublishDropped,
        PublishEnded,
    }
}
