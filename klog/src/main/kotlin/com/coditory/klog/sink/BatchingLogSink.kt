package com.coditory.klog.sink

import com.coditory.klog.LogEvent
import com.coditory.klog.LogStreamListener
import com.coditory.klog.config.KlogErrLogger
import com.coditory.klog.publish.AsyncLogPublisher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

internal class BatchingLogSink(
    private val publisher: AsyncLogPublisher,
    private val batchSize: Int = Defaults.batchSize,
    private val maxBatchStaleness: Duration = Defaults.maxBatchStaleness,
    private val klogErrLogger: KlogErrLogger = KlogErrLogger.STDERR,
    private val listener: LogStreamListener = LogStreamListener.NOOP,
) : AsyncLogPublisher {
    private val stopMutex = Mutex()
    private var stopped = false
    private val batchMutex = Mutex()
    private var batch: List<LogEvent> = mutableListOf()

    @OptIn(DelicateCoroutinesApi::class)
    private val tickerJob =
        GlobalScope.launch(CoroutineName(BatchingLogSink::class.java.simpleName)) {
            while (isActive) {
                delay(maxBatchStaleness)
                emit()
            }
        }

    private suspend fun emit() {
        val current =
            batchMutex.withLock {
                if (batch.isNotEmpty()) {
                    val current = batch
                    this@BatchingLogSink.batch = mutableListOf()
                    current
                } else {
                    listOf()
                }
            }
        if (current.isNotEmpty()) {
            try {
                publisher.publishBatchAsync(current)
            } catch (e: Throwable) {
                klogErrLogger.logDropped(e)
                listener.onStreamDropped(current, e)
            }
        }
    }

    override suspend fun publishAsync(event: LogEvent) {
        val current =
            batchMutex.withLock {
                if (stopped) {
                    throw IllegalStateException("BatchingAsyncLogPublisher is already stopped")
                }
                batch.addLast(event)
                if (batch.size >= batchSize) {
                    val current = batch
                    this@BatchingLogSink.batch = mutableListOf()
                    current
                } else {
                    listOf()
                }
            }
        if (current.isNotEmpty()) {
            try {
                publisher.publishBatchAsync(current)
            } catch (e: Throwable) {
                klogErrLogger.logDropped(e)
                listener.onStreamDropped(current, e)
            }
        }
    }

    override suspend fun publishBatchAsync(events: List<LogEvent>) {
        events.forEach { publishAsync(it) }
    }

    override fun publishBlocking(event: LogEvent) {
        try {
            runBlocking {
                publisher.publishBlocking(event)
            }
        } catch (e: Throwable) {
            klogErrLogger.logDropped(e)
            listener.onStreamDropped(event, e)
        }
    }

    override suspend fun publishSuspending(event: LogEvent) {
        try {
            publisher.publishSuspending(event)
        } catch (e: Throwable) {
            klogErrLogger.logDropped(e)
            listener.onStreamDropped(event, e)
        }
    }

    override suspend fun flush() {
        emit()
    }

    override suspend fun stopAndFlush() {
        stopMutex.withLock {
            if (stopped) return
            tickerJob.cancelAndJoin()
            emit()
            publisher.stopAndFlush()
            stopped = true
        }
    }

    object Defaults {
        val batchSize: Int = 25
        val maxBatchStaleness: Duration = 1.seconds
    }
}
