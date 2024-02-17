package com.coditory.klog.publish

import com.coditory.klog.LogEvent
import com.coditory.klog.text.TextLogEventSerializer
import com.coditory.klog.text.plain.PlainTextLogEventSerializer
import com.coditory.klog.text.plain.PlainTextTimestampFormatter
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class InMemoryTextPublisher(
    private val serializer: TextLogEventSerializer = PlainTextLogEventSerializer(),
) : BlockingPublisher, AsyncLogPublisher {
    private val logs = mutableListOf<LogEntry>()

    override suspend fun publishAsync(event: LogEvent) {
        publishBlocking(event)
    }

    override suspend fun publishBatchAsync(events: List<LogEvent>) {
        events.forEach { publishBlocking(it) }
    }

    @Synchronized
    override fun publishBlocking(event: LogEvent) {
        val sb = StringBuilder()
        serializer.format(event, sb)
        logs.add(
            LogEntry(
                event = event,
                message = sb.toString(),
            ),
        )
    }

    @Synchronized
    fun getLogs(): List<String> = logs.map { it.message }

    @Synchronized
    fun getLastLog(): String? = logs.lastOrNull()?.message

    @Synchronized
    fun getLogEntries(): List<LogEntry> = logs

    @Synchronized
    fun getLastLogEntry(): LogEntry? = logs.lastOrNull()

    @Synchronized
    fun getLogEvents(): List<LogEvent> = logs.map { it.event }

    @Synchronized
    fun getLastLogEvent(): LogEvent? = logs.lastOrNull()?.event

    @Synchronized
    fun clear() {
        logs.clear()
    }

    data class LogEntry(
        val event: LogEvent,
        val message: String,
    )

    companion object {
        const val TEST_THREAD_NAME = "@THREAD"

        fun testPublisher(): InMemoryTextPublisher {
            return InMemoryTextPublisher(
                PlainTextLogEventSerializer(
                    timestampFormatter = PlainTextTimestampFormatter.from(
                        DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.of("UTC")),
                    ),
                    threadFormatter = { _, appendable -> appendable.append(TEST_THREAD_NAME) },
                ),
            )
        }
    }
}
