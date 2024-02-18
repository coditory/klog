package com.coditory.klog

interface LogListener : LogStreamListener, LogPublisherListener {
    fun onLogStarted(event: LogEvent) {}

    fun onLogSkipped(event: LogEvent) {}

    fun onLogEnded(event: LogEvent) {}

    companion object {
        internal val NOOP = object : LogListener {}
    }
}
