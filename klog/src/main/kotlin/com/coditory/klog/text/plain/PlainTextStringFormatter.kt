package com.coditory.klog.text.plain

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

        fun builder(): PlainTextStringFormatterBuilder {
            return PlainTextStringFormatterBuilder()
        }
    }
}
