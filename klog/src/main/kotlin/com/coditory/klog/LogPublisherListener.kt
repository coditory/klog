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
            return if (isNoop(first)) second else CompositeLogPublisherListener(first, second)
        }

        fun create(
            first: LogPublisherListener,
            second: LogPublisherListener,
            third: LogPublisherListener,
        ): LogPublisherListener {
            return create(create(first, second), third)
        }

        private fun isNoop(listener: LogPublisherListener): Boolean {
            return listener == LogPublisherListener.NOOP ||
                listener == LogStreamListener.NOOP ||
                listener == LogListener.NOOP
        }
    }
}
