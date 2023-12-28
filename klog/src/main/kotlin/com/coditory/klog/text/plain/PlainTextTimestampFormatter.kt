package com.coditory.klog.text.plain

import com.coditory.klog.text.shared.DateTimeFormatters.ISO_LOCAL_DATE_TIME_MILLIS
import com.coditory.klog.text.shared.DateTimeFormatters.ISO_LOCAL_TIME_MILLIS
import com.coditory.klog.text.shared.DateTimeFormatters.ISO_OFFSET_DATE_TIME_MILLIS
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun interface PlainTextTimestampFormatter {
    fun format(
        ts: ZonedDateTime,
        appendable: Appendable,
    )

    companion object {
        fun fromLocalTime(style: TextStyle = TextStyle.empty()): PlainTextTimestampFormatter {
            return from(ISO_LOCAL_TIME_MILLIS, style)
        }

        fun fromLocalDateTime(style: TextStyle = TextStyle.empty()): PlainTextTimestampFormatter {
            return from(ISO_LOCAL_DATE_TIME_MILLIS, style)
        }

        fun fromZonedDateTime(style: TextStyle = TextStyle.empty()): PlainTextTimestampFormatter {
            return from(ISO_OFFSET_DATE_TIME_MILLIS, style)
        }

        fun from(
            formatter: DateTimeFormatter,
            style: TextStyle = TextStyle.empty(),
        ): PlainTextTimestampFormatter {
            return PlainTextTimestampFormatter { ts, appendable ->
                appendable.append(style.prefix)
                formatter.formatTo(ts, appendable)
                appendable.append(style.postfix)
            }
        }
    }
}
