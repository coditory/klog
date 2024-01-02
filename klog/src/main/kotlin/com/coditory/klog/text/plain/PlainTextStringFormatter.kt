package com.coditory.klog.text.plain

import com.coditory.klog.text.shared.SizedAppendable.Companion.LONG_TEXT_LENGTH
import com.coditory.klog.text.shared.SizedAppendable.Companion.MAX_LENGTH_MARKER

fun interface PlainTextStringFormatter {
    fun format(
        text: String,
        appendable: Appendable,
    )

    companion object {
        fun default(): PlainTextStringFormatter {
            return PlainTextStringFormatter { text, appendable ->
                appendable.append(text)
            }
        }

        fun limitted(
            maxLength: Int = LONG_TEXT_LENGTH,
            maxLengthMarker: String = MAX_LENGTH_MARKER,
        ): PlainTextStringFormatter {
            return PlainTextStringFormatter { text, appendable ->
                if (text.length <= maxLength) {
                    appendable.append(text)
                } else {
                    appendable.append(text, 0, maxLength)
                    appendable.append(maxLengthMarker)
                }
            }
        }

        fun from(configure: PlainTextStringFormatterBuilder.() -> Unit): PlainTextStringFormatter {
            val builder = PlainTextStringFormatterBuilder()
            configure(builder)
            return builder.build()
        }

        fun builder(): PlainTextStringFormatterBuilder {
            return PlainTextStringFormatterBuilder()
        }
    }
}
