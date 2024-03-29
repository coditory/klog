package com.coditory.klog.config

import com.coditory.klog.Level
import com.coditory.klog.LogStreamListener
import com.coditory.klog.publish.AsyncLogPublisher
import com.coditory.klog.publish.BlockingPublisher

data class LogStreamConfig(
    val filter: LogFilter = LogFilter.filterByMinLevel(Level.INFO),
    val publishers: List<LogPublisherConfig> = mutableListOf(),
    val stopOnMatch: Boolean = true,
    val prioritizer: LogPrioritizer = LogPrioritizer.prioritizeByMinLevel(Level.FATAL),
    val listener: LogStreamListener = LogStreamListener.NOOP,
) {
    companion object {
        fun builder(): LogStreamConfigBuilder {
            return LogStreamConfigBuilder()
        }
    }
}

@ScopedKlogConfig
class LogStreamConfigBuilder {
    private var filter: LogFilter = LogFilter.filterByMinLevel(Level.INFO)
    private var stopOnMatch: Boolean = true
    private var prioritizer: LogPrioritizer = LogPrioritizer.prioritizeByMinLevel(Level.FATAL)
    private var publishers: MutableList<LogPublisherConfig> = mutableListOf()
    private var listener: LogStreamListener = LogStreamListener.NOOP

    fun listener(listener: LogStreamListener): LogStreamConfigBuilder {
        this.listener = listener
        return this
    }

    fun filter(filter: LogFilter): LogStreamConfigBuilder {
        this.filter = filter
        return this
    }

    fun filter(
        levelRange: LevelRange,
        nameMatcher: LoggerNameMatcher,
    ): LogStreamConfigBuilder {
        this.filter = LogFilter(levelRange, nameMatcher)
        return this
    }

    fun stopOnMatch(stopOnMatch: Boolean): LogStreamConfigBuilder {
        this.stopOnMatch = stopOnMatch
        return this
    }

    fun prioritizer(prioritizer: LogPrioritizer): LogStreamConfigBuilder {
        this.prioritizer = prioritizer
        return this
    }

    fun publisher(config: LogPublisherConfig): LogStreamConfigBuilder {
        this.publishers.add(config)
        return this
    }

    fun asyncPublisher(
        publisher: AsyncLogPublisher,
        configure: AsyncLogPublisherConfigBuilder.() -> Unit = {},
    ): LogStreamConfigBuilder {
        val builder = AsyncLogPublisherConfigBuilder(publisher)
        configure(builder)
        this.publishers.add(builder.build())
        return this
    }

    fun blockingPublisher(
        publisher: BlockingPublisher,
        configure: BlockingLogPublisherConfigBuilder.() -> Unit = {},
    ): LogStreamConfigBuilder {
        val builder = BlockingLogPublisherConfigBuilder(publisher)
        configure(builder)
        this.publishers.add(builder.build())
        return this
    }

    fun build(): LogStreamConfig {
        return LogStreamConfig(
            filter = filter,
            publishers = publishers,
            stopOnMatch = stopOnMatch,
            prioritizer = prioritizer,
            listener = listener,
        )
    }
}
