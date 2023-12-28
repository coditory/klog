package com.coditory.klog.config

import com.coditory.klog.publish.AsyncLogPublisher
import com.coditory.klog.publish.BatchLogPublisher
import com.coditory.klog.publish.BatchingLogPublisher
import com.coditory.klog.publish.BufferedLogPublisher
import kotlin.time.Duration

sealed interface LogPublisherConfig

data class BatchLogPublisherConfig(
    val publisher: BatchLogPublisher,
    // batching
    val batchSize: Int = BatchingLogPublisher.Defaults.batchSize,
    val maxBatchStaleness: Duration = BatchingLogPublisher.Defaults.maxBatchStaleness,
    // buffering
    val standardLogBufferCapacity: Int = BufferedLogPublisher.Defaults.standardLogBufferCapacity,
    val prioritizedLogBufferCapacity: Int = BufferedLogPublisher.Defaults.prioritizedLogBufferCapacity,
    // serializing
    val serialize: Boolean = false,
) : LogPublisherConfig

@ScopedKlogConfig
class BatchLogPublisherConfigBuilder {
    private var publisher: BatchLogPublisher? = null

    // batching
    private var batchSize: Int = BatchingLogPublisher.Defaults.batchSize
    private var maxBatchStaleness: Duration = BatchingLogPublisher.Defaults.maxBatchStaleness

    // buffering
    private var standardLogBufferCapacity: Int = BufferedLogPublisher.Defaults.standardLogBufferCapacity
    private var prioritizedLogBufferCapacity: Int = BufferedLogPublisher.Defaults.prioritizedLogBufferCapacity

    // serializing
    private var serialize: Boolean = false

    fun publisher(publisher: BatchLogPublisher): BatchLogPublisherConfigBuilder {
        this.publisher = publisher
        return this
    }

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
            publisher = publisher ?: throw NullPointerException("Expected publisher"),
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
    var standardLogBufferCapacity: Int = BufferedLogPublisher.Defaults.standardLogBufferCapacity,
    var prioritizedLogBufferCapacity: Int = BufferedLogPublisher.Defaults.prioritizedLogBufferCapacity,
    // serializing
    var serialize: Boolean = true,
) : LogPublisherConfig

@ScopedKlogConfig
class AsyncLogPublisherConfigBuilder {
    private var publisher: AsyncLogPublisher? = null

    // buffering
    private var standardLogBufferCapacity: Int = BufferedLogPublisher.Defaults.standardLogBufferCapacity
    private var prioritizedLogBufferCapacity: Int = BufferedLogPublisher.Defaults.prioritizedLogBufferCapacity

    // serializing
    private var serialize: Boolean = true

    fun publisher(publisher: AsyncLogPublisher): AsyncLogPublisherConfigBuilder {
        this.publisher = publisher
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

    internal fun build(): AsyncLogPublisherConfig {
        return AsyncLogPublisherConfig(
            publisher = publisher ?: throw NullPointerException("Expected publisher"),
            standardLogBufferCapacity = standardLogBufferCapacity,
            prioritizedLogBufferCapacity = prioritizedLogBufferCapacity,
            serialize = serialize,
        )
    }
}
