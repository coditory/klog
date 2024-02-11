package com.coditory.klog

import com.coditory.klog.config.AsyncLogPublisherConfig
import com.coditory.klog.config.BlockingLogPublisherConfig
import com.coditory.klog.config.KlogConfig
import com.coditory.klog.config.KlogErrLogger
import com.coditory.klog.config.LogPublisherConfig
import com.coditory.klog.config.LogStreamConfig
import com.coditory.klog.publish.BatchingLogPublisher
import com.coditory.klog.publish.BlockingLogSink
import com.coditory.klog.publish.BufferedLogSink
import com.coditory.klog.publish.LogPublisher
import com.coditory.klog.publish.LogPublisherListener
import com.coditory.klog.publish.SerialLogPublisher
import java.time.Clock

internal data class KlogContext(
    val streams: List<LogStream>,
    val clock: Clock,
    val listener: LogListener,
    val klogErrLogger: KlogErrLogger,
    val synchronizeLoggers: Boolean,
) {
    companion object {
        fun build(config: KlogConfig): KlogContext {
            return KlogContext(
                streams = config.streams.mapIndexed { idx, stream ->
                    buildStream(idx, stream, config)
                },
                clock = config.clock,
                listener = config.listener,
                klogErrLogger = config.klogErrLogger,
                synchronizeLoggers = config.synchronizeLoggers,
            )
        }

        private fun buildStream(
            streamIdx: Int,
            config: LogStreamConfig,
            klogConfig: KlogConfig,
        ): LogStream {
            val streamDescriptor = LogStreamDescriptor(streamIdx, config.filter)
            val publishers =
                config.publishers.mapIndexed { idx, publisher ->
                    val descriptor = LogPublisherDescriptor(
                        stream = streamDescriptor,
                        publisherIdx = idx,
                        publisherType = publisher.javaClass,
                    )
                    buildPublisher(descriptor, publisher, klogConfig)
                }
            return LogStream(
                descriptor = streamDescriptor,
                filter = config.filter,
                publishers = publishers,
                stopOnMatch = config.stopOnMatch,
                prioritizer = config.prioritizer,
                listener = klogConfig.listener,
            )
        }

        private fun buildPublisher(
            descriptor: LogPublisherDescriptor,
            config: LogPublisherConfig,
            klogConfig: KlogConfig,
        ): LogPublisher {
            return when (config) {
                is BlockingLogPublisherConfig -> buildBlockingLogPublisher(descriptor, config, klogConfig)
                is AsyncLogPublisherConfig -> buildAsyncLogPublisher(descriptor, config, klogConfig)
            }
        }

        private fun buildBlockingLogPublisher(
            descriptor: LogPublisherDescriptor,
            config: BlockingLogPublisherConfig,
            klogConfig: KlogConfig,
        ): LogPublisher {
            return BlockingLogSink(
                publisher = config.publisher,
                descriptor = descriptor,
                listener = klogConfig.listener,
                klogErrLogger = klogConfig.klogErrLogger,
            )
        }

        private fun buildAsyncLogPublisher(
            descriptor: LogPublisherDescriptor,
            config: AsyncLogPublisherConfig,
            klogConfig: KlogConfig,
        ): LogPublisher {
            val serialAsyncLogPublisher =
                if (!config.serialize) {
                    config.publisher
                } else {
                    SerialLogPublisher(
                        publisher = config.publisher,
                        listener = LogPublisherListener.terminal(descriptor, klogConfig.listener),
                        klogErrLogger = klogConfig.klogErrLogger,
                    )
                }
            val batchingAsyncPublisher =
                if (config.batchSize > 0) {
                    BatchingLogPublisher(
                        publisher = serialAsyncLogPublisher,
                        batchSize = config.batchSize,
                        maxBatchStaleness = config.maxBatchStaleness,
                        listener = LogPublisherListener.middle(descriptor, klogConfig.listener),
                        klogErrLogger = klogConfig.klogErrLogger,
                    )
                } else {
                    serialAsyncLogPublisher
                }
            return BufferedLogSink(
                publisher = batchingAsyncPublisher,
                standardLogBufferCapacity = config.standardLogBufferCapacity,
                prioritizedLogBufferCapacity = config.prioritizedLogBufferCapacity,
                listener = LogPublisherListener.entry(descriptor, klogConfig.listener),
                klogErrLogger = klogConfig.klogErrLogger,
            )
        }
    }
}
