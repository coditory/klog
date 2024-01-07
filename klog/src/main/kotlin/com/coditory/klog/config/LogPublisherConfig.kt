package com.coditory.klog.config

import com.coditory.klog.publish.AsyncLogPublisher
import com.coditory.klog.publish.BatchingLogPublisher
import com.coditory.klog.publish.BlockingPublisher
import com.coditory.klog.publish.BufferedLogSink
import kotlin.time.Duration

sealed interface LogPublisherConfig

data class BlockingLogPublisherConfig(
    val publisher: BlockingPublisher,
) : LogPublisherConfig

data class AsyncLogPublisherConfig(
    val publisher: AsyncLogPublisher,
    // batching
    val batchSize: Int = BatchingLogPublisher.Defaults.batchSize,
    val maxBatchStaleness: Duration = BatchingLogPublisher.Defaults.maxBatchStaleness,
    // buffering
    var standardLogBufferCapacity: Int = BufferedLogSink.Defaults.standardLogBufferCapacity,
    var prioritizedLogBufferCapacity: Int = BufferedLogSink.Defaults.prioritizedLogBufferCapacity,
    // serializing
    var serialize: Boolean = true,
) : LogPublisherConfig

@ScopedKlogConfig
class AsyncLogPublisherConfigBuilder(
    private val publisher: AsyncLogPublisher,
) {
    // batching
    private var batchSize: Int = BatchingLogPublisher.Defaults.batchSize
    private var maxBatchStaleness: Duration = BatchingLogPublisher.Defaults.maxBatchStaleness

    // buffering
    private var standardLogBufferCapacity: Int = BufferedLogSink.Defaults.standardLogBufferCapacity
    private var prioritizedLogBufferCapacity: Int = BufferedLogSink.Defaults.prioritizedLogBufferCapacity

    // serializing
    private var serialize: Boolean = false

    fun batchSize(batchSize: Int): AsyncLogPublisherConfigBuilder {
        this.batchSize = batchSize
        return this
    }

    fun maxBatchStaleness(maxBatchStaleness: Duration): AsyncLogPublisherConfigBuilder {
        this.maxBatchStaleness = maxBatchStaleness
        return this
    }

    fun standardLogBufferCapacity(standardLogBufferCapacity: Int): AsyncLogPublisherConfigBuilder {
        this.standardLogBufferCapacity = standardLogBufferCapacity
        return this
    }

    fun prioritizedLogBufferCapacity(prioritizedLogBufferCapacity: Int): AsyncLogPublisherConfigBuilder {
        this.prioritizedLogBufferCapacity = prioritizedLogBufferCapacity
        return this
    }

    fun serialize(serialize: Boolean): AsyncLogPublisherConfigBuilder {
        this.serialize = serialize
        return this
    }

    fun build(): AsyncLogPublisherConfig {
        return AsyncLogPublisherConfig(
            publisher = publisher,
            batchSize = batchSize,
            maxBatchStaleness = maxBatchStaleness,
            standardLogBufferCapacity = standardLogBufferCapacity,
            prioritizedLogBufferCapacity = prioritizedLogBufferCapacity,
            serialize = serialize,
        )
    }
}
