package com.coditory.klog.publish

import com.coditory.klog.LogEvent
import com.coditory.klog.text.TextLogEventSerializer
import com.coditory.klog.text.json.JsonLogEventSerializer
import com.coditory.klog.text.plain.ConsoleColors
import com.coditory.klog.text.plain.PlainTextLogEventSerializer

class SystemOutPublisher(
    private val formatter: TextLogEventSerializer,
) : AsyncLogPublisher, BatchLogPublisher {
    override suspend fun publishAsync(event: LogEvent) {
        // performance tests reveal that there is no use in switching to IO dispatcher
        publishBlocking(event)
    }

    override suspend fun publishBatchAsync(events: List<LogEvent>) {
        events.forEach { publishBlocking(it) }
    }

    override fun publishBlocking(event: LogEvent) {
        val log = serialize(event)
        println(log)
    }

    override suspend fun publishSuspending(event: LogEvent) {
        // performance tests reveal that there is no use in switching to IO dispatcher
        publishBlocking(event)
    }

    private fun serialize(event: LogEvent): String {
        val sb = StringBuilder()
        formatter.format(event, sb)
        return sb.toString()
    }

    companion object {
        fun plainText(
            development: Boolean = false,
            ansi: Boolean = ConsoleColors.ANSI_CONSOLE,
        ): SystemOutPublisher {
            val formatter =
                if (development) {
                    PlainTextLogEventSerializer.development(ansi = ansi)
                } else {
                    PlainTextLogEventSerializer.production()
                }
            return SystemOutPublisher(formatter)
        }

        fun json(): SystemOutPublisher {
            val formatter = JsonLogEventSerializer()
            return SystemOutPublisher(formatter)
        }
    }
}
