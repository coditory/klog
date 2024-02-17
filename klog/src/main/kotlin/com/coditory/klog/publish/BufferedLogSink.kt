package com.coditory.klog.publish

import com.coditory.klog.LogEvent
import com.coditory.klog.LogPriority
import com.coditory.klog.config.KlogErrLogger
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@OptIn(DelicateCoroutinesApi::class)
internal class BufferedLogSink(
    private val publisher: AsyncLogPublisher,
    private val standardLogBufferCapacity: Int = Defaults.standardLogBufferCapacity,
    private val prioritizedLogBufferCapacity: Int = Defaults.prioritizedLogBufferCapacity,
    private val listener: LogPublisherListener = LogPublisherListener.NOOP,
    private val klogErrLogger: KlogErrLogger = KlogErrLogger.STDERR,
) : LogPublisher {
    private val stdLogsChannel = Channel<LogEvent>(standardLogBufferCapacity)
    private val prioritizedLogsChannel = Channel<LogEvent>(prioritizedLogBufferCapacity)
    private val closeChannel = Channel<Unit>(0)
    private val stopMutex = Mutex()

    private val job =
        GlobalScope.launch(CoroutineName(BufferedLogSink::class.java.simpleName)) {
            while (isActive) {
                select {
                    prioritizedLogsChannel.onReceiveCatching { result -> result.getOrNull()?.let { emit(it) } }
                    stdLogsChannel.onReceiveCatching { result -> result.getOrNull()?.let { emit(it) } }
                }
                closeChannel.trySend(Unit)
            }
        }

    private suspend fun emit(event: LogEvent) {
        try {
            publisher.publishAsync(event)
            listener.published(event)
        } catch (e: Throwable) {
            klogErrLogger.logDropped(e)
            listener.dropped(event, e)
        }
    }

    override fun publish(event: LogEvent) {
        listener.received(event)
        val result =
            if (event.priority == LogPriority.PRIORITIZED) {
                stdLogsChannel.trySend(event)
            } else {
                prioritizedLogsChannel.trySend(event)
            }
        if (!result.isSuccess) {
            listener.dropped(event, result.exceptionOrNull())
        }
    }

    override fun publishBlocking(event: LogEvent) {
        listener.received(event)
        try {
            publisher.publishBlocking(event)
            listener.published(event)
        } catch (e: Throwable) {
            klogErrLogger.logDropped(e)
            listener.dropped(event, e)
        }
    }

    override suspend fun publishSuspending(event: LogEvent) {
        listener.received(event)
        try {
            publisher.publishSuspending(event)
            listener.published(event)
        } catch (e: Throwable) {
            klogErrLogger.logDropped(e)
            listener.dropped(event, e)
        }
    }

    override suspend fun flush() {
        for (log in receiveAvailable(prioritizedLogsChannel, prioritizedLogBufferCapacity)) {
            publisher.publishAsync(log)
        }
        for (log in receiveAvailable(stdLogsChannel, standardLogBufferCapacity)) {
            publisher.publishAsync(log)
        }
        publisher.flush()
    }

    private fun receiveAvailable(
        channel: ReceiveChannel<LogEvent>,
        max: Int,
    ): List<LogEvent> {
        if (max <= 0) return emptyList()
        val batch = mutableListOf<LogEvent>()
        var next = channel.tryReceive()
        var nextValue = next.getOrNull()
        while (batch.size < max && next.isSuccess && nextValue != null) {
            batch.add(nextValue)
            if (batch.size < max) {
                next = channel.tryReceive()
                nextValue = next.getOrNull()
            }
        }
        return batch
    }

    override suspend fun stopAndFlush() {
        stopMutex.withLock {
            if (!job.isActive) return
            stdLogsChannel.close()
            prioritizedLogsChannel.close()
            while (!stdLogsChannel.isClosedForReceive || !prioritizedLogsChannel.isClosedForReceive) {
                closeChannel.receive()
            }
            job.cancelAndJoin()
            publisher.stopAndFlush()
        }
    }

    object Defaults {
        val standardLogBufferCapacity: Int = 1024
        val prioritizedLogBufferCapacity: Int = 1024
    }
}
