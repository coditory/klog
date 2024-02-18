package com.coditory.klog

interface LogPublisherListener {
    fun onPublishStarted(event: LogEvent) {}

    fun onPublishStarted(events: List<LogEvent>) {}

    fun onPublishDropped(event: LogEvent, e: Throwable? = null) {}

    fun onPublishDropped(events: List<LogEvent>, e: Throwable? = null) {}

    fun onPublishEnded(event: LogEvent) {}

    fun onPublishEnded(events: List<LogEvent>) {}

    companion object {
        internal val NOOP = object : LogPublisherListener {}
    }
}

internal class CompositeLogPublisherListener private constructor(
    private val first: LogPublisherListener,
    private val second: LogPublisherListener,
) : LogPublisherListener {
    override fun onPublishStarted(event: LogEvent) {
        first.onPublishStarted(event)
        second.onPublishStarted(event)
    }

    override fun onPublishStarted(events: List<LogEvent>) {
        first.onPublishStarted(events)
        second.onPublishStarted(events)
    }

    override fun onPublishDropped(event: LogEvent, e: Throwable?) {
        first.onPublishDropped(event, e)
        second.onPublishDropped(event, e)
    }

    override fun onPublishDropped(events: List<LogEvent>, e: Throwable?) {
        first.onPublishDropped(events, e)
        second.onPublishDropped(events, e)
    }

    override fun onPublishEnded(event: LogEvent) {
        first.onPublishEnded(event)
        second.onPublishEnded(event)
    }

    override fun onPublishEnded(events: List<LogEvent>) {
        first.onPublishEnded(events)
        second.onPublishEnded(events)
    }

    companion object {
        fun create(
            first: LogPublisherListener,
            second: LogPublisherListener,
        ): LogPublisherListener {
            if (first == LogPublisherListener.NOOP) return second
            if (second == LogPublisherListener.NOOP) return first
            return CompositeLogPublisherListener(first, second)
        }

        fun create(
            first: LogPublisherListener,
            second: LogStreamListener,
            third: LogListener,
        ): LogPublisherListener {
            val combined = if (second is LogPublisherListener) {
                create(first, second as LogPublisherListener)
            } else {
                first
            }
            return if (third is LogPublisherListener) {
                create(combined, third as LogPublisherListener)
            } else {
                combined
            }
        }
    }
}
