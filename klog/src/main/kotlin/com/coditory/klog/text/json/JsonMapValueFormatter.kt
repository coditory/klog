package com.coditory.klog.text.json

import com.coditory.klog.text.plain.PlainTextMapValueFormatter

fun interface JsonMapValueFormatter {
    fun format(
        key: String,
        value: Any?,
        appendable: Appendable,
    )

    companion object {
        fun default(escape: Boolean = true): JsonMapValueFormatter {
            return JsonMapValueFormatter { _, value, appendable ->
                if (value == null) {
                    appendable.append("null")
                } else {
                    val text = value.toString()
                    appendable.append('"')
                    val wrapped = if (escape) JsonEscapedAppendable(appendable) else appendable
                    wrapped.append(text)
                    appendable.append('"')
                }
            }
        }

        fun from(formatter: JsonStringFormatter): JsonMapValueFormatter {
            return JsonMapValueFormatter { _, value, appendable ->
                if (value == null) {
                    appendable.append("null")
                } else {
                    val text = value.toString()
                    formatter.format(text, appendable)
                }
            }
        }

        fun from(
            formatter: PlainTextMapValueFormatter,
            escape: Boolean = true,
        ): JsonMapValueFormatter {
            return JsonMapValueFormatter { key, value, appendable ->
                if (value == null) {
                    appendable.append("null")
                } else {
                    val wrapped = if (escape) JsonEscapedAppendable(appendable) else appendable
                    formatter.format(key, value, wrapped)
                }
            }
        }
    }
}
