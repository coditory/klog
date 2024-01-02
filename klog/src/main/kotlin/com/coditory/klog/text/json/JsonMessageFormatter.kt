package com.coditory.klog.text.json

import com.coditory.klog.text.plain.PlainTextMessageFormatter

fun interface JsonMessageFormatter {
    fun format(
        message: String,
        throwable: Throwable?,
        appendable: Appendable,
    )

    companion object {
        fun from(
            messageFormatter: PlainTextMessageFormatter,
            escape: Boolean = true,
        ): JsonMessageFormatter {
            return JsonMessageFormatter { message, throwable, appendable ->
                appendable.append('"')
                val wrapped =
                    if (escape) {
                        JsonEscapedAppendable(appendable)
                    } else {
                        appendable
                    }
                messageFormatter.format(message, throwable, wrapped)
                appendable.append('"')
            }
        }
    }
}
