package com.coditory.klog.text.plain

fun interface PlainTextMessageFormatter {
    fun format(
        message: String,
        throwable: Throwable?,
        appendable: Appendable,
    )

    companion object {
        fun messageAndException(
            messageFormatter: PlainTextStringFormatter = PlainTextStringFormatter.limitted(),
            exceptionFormatter: PlainTextExceptionFormatter = PlainTextExceptionFormatter.fullStackTrace(),
        ): PlainTextMessageFormatter {
            return PlainTextMessageFormatter { message, throwable, appendable ->
                messageFormatter.format(message, appendable)
                if (throwable != null) {
                    exceptionFormatter.format(throwable, appendable)
                }
            }
        }

        fun messageOnly(messageFormatter: PlainTextStringFormatter = PlainTextStringFormatter.default()): PlainTextMessageFormatter {
            return PlainTextMessageFormatter { message, _, appendable ->
                messageFormatter.format(message, appendable)
            }
        }
    }
}
