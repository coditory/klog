package com.coditory.klog.config

import com.coditory.klog.publish.AsyncLogPublisher
import com.coditory.klog.publish.BatchLogPublisher
import com.coditory.klog.publish.BatchingLogPublisher
import com.coditory.klog.publish.BlockingPublisher
import com.coditory.klog.publish.BufferedLogSink
import kotlin.time.Duration

sealed interface LogPublisherConfig

data class BlockingLogPublisherConfig(
    val publisher: BlockingPublisher,
) : LogPublisherConfig

data class BatchLogPublisherConfig(
    val publisher: BatchLogPublisher,
    // batching
    val batchSize: Int = BatchingLogPublisher.Defaults.batchSize,
    val maxBatchStaleness: Duration = BatchingLogPublisher.Defaults.maxBatchStaleness,
    // buffering
    val standardLogBufferCapacity: Int = BufferedLogSink.Defaults.standardLogBufferCapacity,
    val prioritizedLogBufferCapacity: Int = BufferedLogSink.Defaults.prioritizedLogBufferCapacity,
    // serializing
    val serialize: Boolean = false,
) : LogPublisherConfig

@ScopedKlogConfig
class BatchLogPublisherConfigBuilder(
    private val publisher: BatchLogPublisher
) {
    // batching
    private var batchSize: Int = BatchingLogPublisher.Defaults.batchSize
    private var maxBatchStaleness: Duration = BatchingLogPublisher.Defaults.maxBatchStaleness

    // buffering
    private var standardLogBufferCapacity: Int = BufferedLogSink.Defaults.standardLogBufferCapacity
    private var prioritizedLogBufferCapacity: Int = BufferedLogSink.Defaults.prioritizedLogBufferCapacity

    // serializing
    private var serialize: Boolean = false

    fun batchSize(batchSize: Int): BatchLogPublisherConfigBuilder {
        this.batchSize = batchSize
        return this
    }

    fun maxBatchStaleness(maxBatchStaleness: Duration): BatchLogPublisherConfigBuilder {
        this.maxBatchStaleness = maxBatchStaleness
        return this
    }

    fun standardLogBufferCapacity(standardLogBufferCapacity: Int): BatchLogPublisherConfigBuilder {
        this.standardLogBufferCapacity = standardLogBufferCapacity
        return this
    }

    fun prioritizedLogBufferCapacity(prioritizedLogBufferCapacity: Int): BatchLogPublisherConfigBuilder {
        this.prioritizedLogBufferCapacity = prioritizedLogBufferCapacity
        return this
    }

    fun serialize(serialize: Boolean): BatchLogPublisherConfigBuilder {
        this.serialize = serialize
        return this
    }

    fun build(): BatchLogPublisherConfig {
        return BatchLogPublisherConfig(
            publisher = publisher,
            batchSize = batchSize,
            maxBatchStaleness = maxBatchStaleness,
            standardLogBufferCapacity = standardLogBufferCapacity,
            prioritizedLogBufferCapacity = prioritizedLogBufferCapacity,
            serialize = serialize,
        )
    }
}

data class AsyncLogPublisherConfig(
    val publisher: AsyncLogPublisher,
    // buffering
    var standardLogBufferCapacity: Int = BufferedLogSink.Defaults.standardLogBufferCapacity,
    var prioritizedLogBufferCapacity: Int = BufferedLogSink.Defaults.prioritizedLogBufferCapacity,
    // serializing
    var serialize: Boolean = true,
) : LogPublisherConfig

@ScopedKlogConfig
class AsyncLogPublisherConfigBuilder(
    private val publisher: AsyncLogPublisher
) {
    // buffering
    private var standardLogBufferCapacity: Int = BufferedLogSink.Defaults.standardLogBufferCapacity
    private var prioritizedLogBufferCapacity: Int = BufferedLogSink.Defaults.prioritizedLogBufferCapacity

    // serializing
    private var serialize: Boolean = true

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

    internal fun build(): AsyncLogPublisherConfig {
        return AsyncLogPublisherConfig(
            publisher = publisher,
            standardLogBufferCapacity = standardLogBufferCapacity,
            prioritizedLogBufferCapacity = prioritizedLogBufferCapacity,
            serialize = serialize,
        )
    }
}
