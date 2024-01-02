package com.coditory.klog.text.json

import com.coditory.klog.text.plain.PlainTextStringFormatter
import com.coditory.klog.text.shared.SizedAppendable
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
            maxLengthMarker: String = "[trimmed]",
        ): JsonStringFormatter {
            return JsonStringFormatter { text, appendable ->
                appendable.append('"')
                val wrapped =
                    if (escape) {
                        JsonEscapedAppendable(
                            appendable = appendable,
                            maxLength = maxLength,
                            maxLengthMarker = maxLengthMarker,
                        )
                    } else if (maxLength < Int.MAX_VALUE) {
                        SizedAppendable(
                            appendable = appendable,
                            maxLength = maxLength,
                            maxLengthMarker = maxLengthMarker,
                        )
                    } else {
                        appendable
                    }
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
