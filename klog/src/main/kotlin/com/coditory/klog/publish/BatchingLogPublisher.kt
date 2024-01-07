package com.coditory.klog.publish

import com.coditory.klog.LogEvent
import com.coditory.klog.config.KlogErrLogger
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

internal class BatchingLogPublisher(
    private val publisher: AsyncLogPublisher,
    private val batchSize: Int = Defaults.batchSize,
    private val maxBatchStaleness: Duration = Defaults.maxBatchStaleness,
    private val klogErrLogger: KlogErrLogger = KlogErrLogger.STDERR,
    private val listener: LogPublisherListener = LogPublisherListener.NOOP,
) : AsyncLogPublisher {
    private val stopMutex = Mutex()
    private var stopped = false
    private val batchMutex = Mutex()
    private var batch: List<LogEvent> = mutableListOf()

    @OptIn(DelicateCoroutinesApi::class)
    private val tickerJob =
        GlobalScope.launch(CoroutineName(BatchingLogPublisher::class.java.simpleName)) {
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
                    this@BatchingLogPublisher.batch = mutableListOf()
                    current
                } else {
                    listOf()
                }
            }
        if (current.isNotEmpty()) {
            try {
                publisher.publishBatchAsync(current)
            } catch (e: Throwable) {
                klogErrLogger.log { "${this::class.simpleName}: Could not publish log on timer. Cause: " + e.stackTraceToString() }
                listener.dropped(current, e)
            }
        }
    }

    override suspend fun publishAsync(event: LogEvent) {
        listener.received(event)
        val current =
            batchMutex.withLock {
                if (stopped) {
                    throw IllegalStateException("BatchingAsyncLogPublisher is already stopped")
                }
                batch.addLast(event)
                if (batch.size >= batchSize) {
                    val current = batch
                    this@BatchingLogPublisher.batch = mutableListOf()
                    current
                } else {
                    listOf()
                }
            }
        if (current.isNotEmpty()) {
            try {
                publisher.publishBatchAsync(current)
                listener.published(current)
            } catch (e: Throwable) {
                klogErrLogger.log { "${this::class.simpleName}: Could not publish batched logs. Cause: " + e.stackTraceToString() }
                listener.dropped(current, e)
            }
        }
    }

    override suspend fun publishBatchAsync(events: List<LogEvent>) {
        events.forEach { publishAsync(it) }
    }

    override fun publishBlocking(event: LogEvent) =
        runBlocking {
            publisher.publishBlocking(event)
        }

    override suspend fun publishSuspending(event: LogEvent) {
        publisher.publishSuspending(event)
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
