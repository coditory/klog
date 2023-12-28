package com.coditory.klog.text.json

import com.coditory.klog.text.plain.PlainTextStringFormatter
import kotlin.math.min

fun interface JsonStringFormatter {
    fun format(
        text: String,
        appendable: Appendable,
    )

    companion object {
        fun default(
            escape: Boolean = true,
            maxLength: Int = Int.MAX_VALUE,
        ): JsonStringFormatter {
            return JsonStringFormatter { text, appendable ->
                appendable.append('"')
                val wrapped = if (escape) JsonEscapedAppendable(appendable) else appendable
                wrapped.append(text, 0, min(maxLength, text.length))
                appendable.append('"')
            }
        }

        fun from(
            formatter: PlainTextStringFormatter,
            escape: Boolean = true,
        ): JsonStringFormatter {
            return JsonStringFormatter { text, appendable ->
                appendable.append('"')
                val wrapped = if (escape) JsonEscapedAppendable(appendable) else appendable
                formatter.format(text, wrapped)
                appendable.append('"')
            }
        }
    }
}
