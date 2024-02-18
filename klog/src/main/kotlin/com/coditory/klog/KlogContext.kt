package com.coditory.klog

import com.coditory.klog.config.AsyncLogPublisherConfig
import com.coditory.klog.config.BlockingLogPublisherConfig
import com.coditory.klog.config.KlogConfig
import com.coditory.klog.config.KlogErrLogger
import com.coditory.klog.config.LogPublisherConfig
import com.coditory.klog.config.LogStreamConfig
import com.coditory.klog.publish.InstrumentedAsyncLogPublisher
import com.coditory.klog.publish.InstrumentedBlockingPublisher
import com.coditory.klog.publish.LogPublisher
import com.coditory.klog.sink.BatchingLogSink
import com.coditory.klog.sink.BlockingLogSink
import com.coditory.klog.sink.BufferedLogSink
import com.coditory.klog.sink.SerialLogSink
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
                streams = config.streams.map { stream ->
                    buildStream(stream, config)
                },
                clock = config.clock,
                listener = config.listener,
                klogErrLogger = config.klogErrLogger,
                synchronizeLoggers = config.synchronizeLoggers,
            )
        }

        private fun buildStream(
            config: LogStreamConfig,
            klogConfig: KlogConfig,
        ): LogStream {
            val publishers =
                config.publishers.map { publisher ->
                    buildPublisher(publisher, config, klogConfig)
                }

            return LogStream(
                filter = config.filter,
                publishers = publishers,
                stopOnMatch = config.stopOnMatch,
                prioritizer = config.prioritizer,
                listener = CompositeLogStreamListener.create(config.listener, klogConfig.listener),
            )
        }

        private fun buildPublisher(
            config: LogPublisherConfig,
            streamConfig: LogStreamConfig,
            klogConfig: KlogConfig,
        ): LogPublisher {
            return when (config) {
                is BlockingLogPublisherConfig -> buildBlockingLogPublisher(config, streamConfig, klogConfig)
                is AsyncLogPublisherConfig -> buildAsyncLogPublisher(config, streamConfig, klogConfig)
            }
        }

        private fun buildBlockingLogPublisher(
            config: BlockingLogPublisherConfig,
            streamConfig: LogStreamConfig,
            klogConfig: KlogConfig,
        ): LogPublisher {
            val publisher = InstrumentedBlockingPublisher(
                listener = CompositeLogPublisherListener.create(
                    config.listener,
                    streamConfig.listener,
                    klogConfig.listener,
                ),
                publisher = config.publisher,
                klogErrLogger = klogConfig.klogErrLogger,
            )
            return BlockingLogSink(
                publisher = publisher,
                listener = CompositeLogStreamListener.create(streamConfig.listener, klogConfig.listener),
                klogErrLogger = klogConfig.klogErrLogger,
            )
        }

        private fun buildAsyncLogPublisher(
            config: AsyncLogPublisherConfig,
            streamConfig: LogStreamConfig,
            klogConfig: KlogConfig,
        ): LogPublisher {
            val publisher = InstrumentedAsyncLogPublisher(
                listener = CompositeLogPublisherListener.create(
                    config.listener,
                    streamConfig.listener,
                    klogConfig.listener,
                ),
                publisher = config.publisher,
                klogErrLogger = klogConfig.klogErrLogger,
            )
            val serialAsyncLogPublisher =
                if (!config.serialize) {
                    publisher
                } else {
                    SerialLogSink(
                        publisher = publisher,
                        listener = CompositeLogStreamListener.create(streamConfig.listener, klogConfig.listener),
                        klogErrLogger = klogConfig.klogErrLogger,
                    )
                }
            val batchingAsyncPublisher =
                if (config.batchSize > 0) {
                    BatchingLogSink(
                        publisher = serialAsyncLogPublisher,
                        batchSize = config.batchSize,
                        maxBatchStaleness = config.maxBatchStaleness,
                        listener = CompositeLogStreamListener.create(streamConfig.listener, klogConfig.listener),
                        klogErrLogger = klogConfig.klogErrLogger,
                    )
                } else {
                    serialAsyncLogPublisher
                }
            return BufferedLogSink(
                publisher = batchingAsyncPublisher,
                standardLogBufferCapacity = config.standardLogBufferCapacity,
                prioritizedLogBufferCapacity = config.prioritizedLogBufferCapacity,
                listener = CompositeLogStreamListener.create(streamConfig.listener, klogConfig.listener),
                klogErrLogger = klogConfig.klogErrLogger,
            )
        }
    }
}
