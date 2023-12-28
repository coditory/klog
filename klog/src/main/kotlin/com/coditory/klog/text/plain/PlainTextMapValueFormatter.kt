package com.coditory.klog.text.plain

fun interface PlainTextMapValueFormatter {
    fun format(
        key: String,
        value: Any?,
        appendable: Appendable,
    )

    companion object {
        fun formatUsingToString(): PlainTextMapValueFormatter {
            return PlainTextMapValueFormatter { _, value, appendable ->
                if (value == null) {
                    appendable.append("null")
                } else {
                    val text = value.toString()
                    appendable.append(text)
                }
            }
        }

        fun fromTextFormatter(formatter: PlainTextStringFormatter): PlainTextMapValueFormatter {
            return PlainTextMapValueFormatter { _, value, appendable ->
                if (value == null) {
                    appendable.append("null")
                } else {
                    val text = value.toString()
                    formatter.format(text, appendable)
                }
            }
        }
    }
}
