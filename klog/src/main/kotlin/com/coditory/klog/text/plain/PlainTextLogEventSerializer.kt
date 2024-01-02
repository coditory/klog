package com.coditory.klog.text.plain

import com.coditory.klog.LogEvent
import com.coditory.klog.LogEventField
import com.coditory.klog.text.TextLogEventSerializer
import com.coditory.klog.text.shared.DateTimeFormatters.ISO_OFFSET_DATE_TIME_NANOS
import com.coditory.klog.text.shared.SizedAppendable
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class PlainTextLogEventSerializer(
    private val fields: List<LogEventField> = LogEventField.all(),
    private val timestampFormatter: PlainTextTimestampFormatter =
        PlainTextTimestampFormatter.from(
            DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.systemDefault()),
        ),
    private val levelFormatter: PlainTextLevelFormatter = PlainTextLevelFormatter.default(),
    private val loggerNameFormatter: PlainTextStringFormatter = PlainTextStringFormatter.default(),
    private val threadFormatter: PlainTextStringFormatter = PlainTextStringFormatter.default(),
    private val messageFormatter: PlainTextMessageFormatter = PlainTextMessageFormatter.messageOnly(),
    private val contextFormatter: PlainTextContextFormatter = PlainTextContextFormatter.default(),
    private val itemsFormatter: PlainTextMapFormatter = PlainTextMapFormatter.default(),
    private val messageSeparator: String = ": ",
    private val exceptionFormatter: PlainTextExceptionFormatter? = PlainTextExceptionFormatter.fullStackTrace(),
    private val mergeContextToItems: Boolean = false,
) : TextLogEventSerializer {
    override fun format(
        event: LogEvent,
        appendable: Appendable,
    ) {
        val sized = SizedAppendable(appendable)
        var length = sized.length()
        for (i in fields.indices) {
            val field = fields[i]
            if (field.skip(event, mergeContextToItems)) continue
            if (length < sized.length()) {
                if (field == LogEventField.MESSAGE) {
                    sized.append(messageSeparator)
                } else {
                    sized.append(' ')
                }
                length = sized.length()
            }
            when (field) {
                LogEventField.TIMESTAMP -> formatTimestamp(event, sized)
                LogEventField.LEVEL -> formatLevel(event, sized)
                LogEventField.LOGGER -> formatLogger(event, sized)
                LogEventField.THREAD -> formatThread(event, sized)
                LogEventField.CONTEXT -> formatContext(event, sized)
                LogEventField.MESSAGE -> formatMessage(event, sized)
                LogEventField.ITEMS -> formatItems(event, sized)
                LogEventField.EXCEPTION -> formatException(event, sized)
            }
        }
    }

    private fun formatTimestamp(
        event: LogEvent,
        appendable: Appendable,
    ) {
        timestampFormatter.format(event.timestamp, appendable)
    }

    private fun formatLevel(
        event: LogEvent,
        appendable: Appendable,
    ) {
        levelFormatter.format(event.level, appendable)
    }

    private fun formatLogger(
        event: LogEvent,
        appendable: Appendable,
    ) {
        loggerNameFormatter.format(event.logger, appendable)
    }

    private fun formatThread(
        event: LogEvent,
        appendable: Appendable,
    ) {
        threadFormatter.format(event.thread, appendable)
    }

    private fun formatContext(
        event: LogEvent,
        appendable: Appendable,
    ) {
        contextFormatter.format(event.context, appendable)
    }

    private fun formatMessage(
        event: LogEvent,
        appendable: Appendable,
    ) {
        messageFormatter.format(event.message, event.throwable, appendable)
    }

    private fun formatItems(
        event: LogEvent,
        appendable: Appendable,
    ) {
        val items = if (mergeContextToItems) event.items + event.context else event.items
        itemsFormatter.format(items, appendable)
    }

    private fun formatException(
        event: LogEvent,
        appendable: Appendable,
    ) {
        if (exceptionFormatter != null && event.throwable != null) {
            exceptionFormatter.format(event.throwable, appendable)
        }
    }

    companion object {
        fun development(ansi: Boolean? = null): PlainTextLogEventSerializer {
            val ansiResolved = ansi ?: ConsoleColors.ANSI_CONSOLE
            val levelFormatter =
                if (ansiResolved) PlainTextLevelFormatter.leftPaddedAndStyled() else PlainTextLevelFormatter.leftPadded()
            val timestampStyle = if (ansiResolved) TextStyle.white() else TextStyle.empty()
            val threadStyle = if (ansiResolved) TextStyle.whiteBold() else TextStyle.empty()
            return PlainTextLogEventSerializer(
                fields = LogEventField.all(),
                timestampFormatter = PlainTextTimestampFormatter.fromLocalTime(timestampStyle),
                levelFormatter = levelFormatter,
                loggerNameFormatter = PlainTextStringFormatter.builder().ansi(ansiResolved).compactSections().build(),
                threadFormatter =
                    PlainTextStringFormatter.builder().ansi(ansiResolved).style(threadStyle).prefix("[")
                        .postfix("]").build(),
            )
        }

        fun production(): PlainTextLogEventSerializer {
            return PlainTextLogEventSerializer(
                fields = LogEventField.all(),
                timestampFormatter = PlainTextTimestampFormatter.from(ISO_OFFSET_DATE_TIME_NANOS),
                levelFormatter = PlainTextLevelFormatter.leftPadded(),
                loggerNameFormatter = PlainTextStringFormatter.builder().compactSections().maxLength(1024).build(),
                threadFormatter = PlainTextStringFormatter.builder().prefix("[").postfix("]").maxLength(1024).build(),
                messageFormatter = PlainTextMessageFormatter.messageOnly(PlainTextStringFormatter.limitted()),
            )
        }
    }
}
