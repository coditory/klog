package com.coditory.klog

import com.coditory.klog.config.LogFilter

interface LogListener {
    fun onLogStarted(event: LogEvent) {}

    fun onLogEnded(event: LogEvent) {}

    fun onStreamStarted(
        stream: LogStreamDescriptor,
        event: LogEvent,
    ) {
    }

    fun onStreamEnded(
        stream: LogStreamDescriptor,
        event: LogEvent,
    ) {
    }

    fun onPublishStarted(
        publisher: LogPublisherDescriptor,
        event: LogEvent,
    ) {
    }

    fun onPublishStarted(
        publisher: LogPublisherDescriptor,
        events: List<LogEvent>,
    ) {
    }

    fun onPublishDropped(
        publisher: LogPublisherDescriptor,
        event: LogEvent,
        e: Throwable? = null,
    ) {
    }

    fun onPublishDropped(
        publisher: LogPublisherDescriptor,
        events: List<LogEvent>,
        e: Throwable? = null,
    ) {
    }

    fun onPublishEnded(
        publisher: LogPublisherDescriptor,
        event: LogEvent,
    ) {
    }

    fun onPublishEnded(
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
