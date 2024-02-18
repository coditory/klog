package com.coditory.klog

interface LogStreamListener : LogPublisherListener {
    fun onStreamStarted(event: LogEvent) {}

    fun onStreamEnded(event: LogEvent) {}

    fun onStreamDropped(event: LogEvent, e: Throwable? = null) {}

    fun onStreamDropped(events: List<LogEvent>, e: Throwable? = null) {}

    companion object {
        internal val NOOP = object : LogStreamListener {}
    }
}

internal class CompositeLogStreamListener private constructor(
    private val first: LogStreamListener,
    private val second: LogStreamListener,
) : LogStreamListener {
    override fun onStreamStarted(event: LogEvent) {
        first.onStreamStarted(event)
        second.onStreamStarted(event)
    }

    override fun onStreamEnded(event: LogEvent) {
        first.onStreamEnded(event)
        second.onStreamEnded(event)
    }

    override fun onStreamDropped(event: LogEvent, e: Throwable?) {
        first.onStreamDropped(event, e)
        second.onStreamDropped(event, e)
    }

    override fun onStreamDropped(events: List<LogEvent>, e: Throwable?) {
        first.onStreamDropped(events, e)
        second.onStreamDropped(events, e)
    }

    companion object {
        fun create(
            first: LogStreamListener,
            second: LogStreamListener,
        ): LogStreamListener {
            if (first == LogStreamListener.NOOP || first == LogListener.NOOP) return second
            if (second == LogStreamListener.NOOP || second == LogListener.NOOP) return first
            return CompositeLogStreamListener(first, second)
        }
    }
}
