package com.coditory.klog.text.json

import com.coditory.klog.text.plain.PlainTextTimestampFormatter
import com.coditory.klog.text.shared.DateTimeFormatters.ISO_OFFSET_DATE_TIME_NANOS
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun interface JsonTimestampFormatter {
    fun format(
        ts: ZonedDateTime,
        appendable: Appendable,
    )

    companion object {
        fun default(): JsonTimestampFormatter {
            return from(ISO_OFFSET_DATE_TIME_NANOS, escape = false)
        }

        fun from(
            formatter: DateTimeFormatter,
            escape: Boolean = true,
        ): JsonTimestampFormatter {
            return JsonTimestampFormatter { instant, appendable ->
                appendable.append('"')
                val wrapped = if (escape) JsonEscapedAppendable(appendable) else appendable
                formatter.formatTo(instant, wrapped)
                appendable.append('"')
            }
        }

        fun from(
            formatter: PlainTextTimestampFormatter,
            escape: Boolean = true,
        ): JsonTimestampFormatter {
            return JsonTimestampFormatter { ts, appendable ->
                appendable.append('"')
                val wrapped = if (escape) JsonEscapedAppendable(appendable) else appendable
                formatter.format(ts, wrapped)
                appendable.append('"')
            }
        }
    }
}
