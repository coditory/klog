package com.coditory.klog.text.plain

fun interface PlainTextMapFormatter {
    fun format(
        map: Map<String, Any>,
        appendable: Appendable,
    )

    companion object {
        fun default(): PlainTextMapFormatter {
            return builder().build()
        }

        fun builder(): PlainTextMapFormatterBuilder {
            return PlainTextMapFormatterBuilder()
        }
    }
}
