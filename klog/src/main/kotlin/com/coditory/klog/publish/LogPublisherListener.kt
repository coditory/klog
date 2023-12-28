package com.coditory.klog.publish

import com.coditory.klog.LogEvent
import com.coditory.klog.LogListener
import com.coditory.klog.LogPublisherDescriptor

internal interface LogPublisherListener {
    fun received(event: LogEvent) {}

    fun received(events: List<LogEvent>) {}

    fun dropped(
        event: LogEvent,
        e: Throwable? = null,
    ) {}

    fun dropped(
        events: List<LogEvent>,
        e: Throwable? = null,
    ) {}

    fun published(event: LogEvent) {}

    fun published(events: List<LogEvent>) {}

    companion object {
        val NOOP = object : LogPublisherListener {}

        fun terminal(
            publisher: LogPublisherDescriptor,
            listener: LogListener,
        ): LogPublisherListener {
            return object : LogPublisherListener {
                override fun published(event: LogEvent) {
                    listener.onPublished(publisher, event)
                }

                override fun published(events: List<LogEvent>) {
                    listener.onPublished(publisher, events)
                }

                override fun dropped(
                    event: LogEvent,
                    e: Throwable?,
                ) {
                    listener.onDropped(publisher, event, e)
                }

                override fun dropped(
                    events: List<LogEvent>,
                    e: Throwable?,
                ) {
                    listener.onDropped(publisher, events, e)
                }
            }
        }

        fun middle(
            publisher: LogPublisherDescriptor,
            listener: LogListener,
        ): LogPublisherListener {
            return object : LogPublisherListener {
                override fun dropped(
                    event: LogEvent,
                    e: Throwable?,
                ) {
                    listener.onDropped(publisher, event, e)
                }

                override fun dropped(
                    events: List<LogEvent>,
                    e: Throwable?,
                ) {
                    listener.onDropped(publisher, events, e)
                }
            }
        }

        fun entry(
            publisher: LogPublisherDescriptor,
            listener: LogListener,
        ): LogPublisherListener {
            return object : LogPublisherListener {
                override fun received(event: LogEvent) {
                    listener.onReceived(publisher, event)
                }

                override fun received(events: List<LogEvent>) {
                    listener.onReceived(publisher, events)
                }

                override fun dropped(
                    event: LogEvent,
                    e: Throwable?,
                ) {
                    listener.onDropped(publisher, event, e)
                }

                override fun dropped(
                    events: List<LogEvent>,
                    e: Throwable?,
                ) {
                    listener.onDropped(publisher, events, e)
                }
            }
        }
    }
}
