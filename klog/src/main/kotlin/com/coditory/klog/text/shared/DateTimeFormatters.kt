package com.coditory.klog.text.shared

import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

internal object DateTimeFormatters {
    val ISO_LOCAL_TIME_MILLIS: DateTimeFormatter =
        DateTimeFormatterBuilder()
            .appendValue(ChronoField.HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
            .optionalStart()
            .appendLiteral(':')
            .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
            .optionalStart()
            .appendFraction(ChronoField.MILLI_OF_DAY, 3, 3, true)
            .toFormatter()
    val ISO_LOCAL_DATE_TIME_MILLIS: DateTimeFormatter =
        DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral('T')
            .append(ISO_LOCAL_TIME_MILLIS)
            .toFormatter()
    val ISO_OFFSET_DATE_TIME_MILLIS: DateTimeFormatter =
        DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(ISO_LOCAL_DATE_TIME_MILLIS)
            .parseLenient()
            .appendOffsetId()
            .parseStrict()
            .toFormatter()

    val ISO_LOCAL_TIME_NANOS: DateTimeFormatter =
        DateTimeFormatterBuilder()
            .appendValue(ChronoField.HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
            .optionalStart()
            .appendLiteral(':')
            .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
            .optionalStart()
            .appendFraction(ChronoField.NANO_OF_SECOND, 6, 6, true)
            .toFormatter()
    val ISO_LOCAL_DATE_TIME_NANOS: DateTimeFormatter =
        DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral('T')
            .append(ISO_LOCAL_TIME_NANOS)
            .toFormatter()
    val ISO_OFFSET_DATE_TIME_NANOS: DateTimeFormatter =
        DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(ISO_LOCAL_DATE_TIME_NANOS)
            .parseLenient()
            .appendOffsetId()
            .parseStrict()
            .toFormatter()
}
