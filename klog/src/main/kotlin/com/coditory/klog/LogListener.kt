package com.coditory.klog

import com.coditory.klog.config.LogFilter

interface LogListener {
    fun onLogStart(event: LogEvent) {}

    fun onLogEnd(event: LogEvent) {}

    fun onStreamStart(
        stream: LogStreamDescriptor,
        event: LogEvent,
    ) {
    }

    fun onStreamEnd(
        stream: LogStreamDescriptor,
        event: LogEvent,
    ) {
    }

    fun onReceived(
        publisher: LogPublisherDescriptor,
        event: LogEvent,
    ) {
    }

    fun onReceived(
        publisher: LogPublisherDescriptor,
        events: List<LogEvent>,
    ) {
    }

    fun onDropped(
        publisher: LogPublisherDescriptor,
        event: LogEvent,
        e: Throwable? = null,
    ) {
    }

    fun onDropped(
        publisher: LogPublisherDescriptor,
        events: List<LogEvent>,
        e: Throwable? = null,
    ) {
    }

    fun onPublished(
        publisher: LogPublisherDescriptor,
        event: LogEvent,
    ) {
    }

    fun onPublished(
        publisher: LogPublisherDescriptor,
        events: List<LogEvent>,
    ) {
    }

    companion object {
        internal val NOOP = object : LogListener {}
    }
}

data class LogPublisherDescriptor(
    val stream: LogStreamDescriptor,
    val publisherIdx: Int,
    val publisherType: Class<out Any>,
)

data class LogStreamDescriptor(
    val idx: Int,
    val filter: LogFilter,
)
