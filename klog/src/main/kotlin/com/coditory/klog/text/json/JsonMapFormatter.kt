package com.coditory.klog.text.json

fun interface JsonMapFormatter {
    fun format(
        map: Map<String, Any?>,
        appendable: Appendable,
    )

    companion object {
        fun default(parseKeys: Boolean = true): JsonMapFormatter {
            return builder().parseKeys(parseKeys).build()
        }

        fun builder(): JsonMapFormatterBuilder {
            return JsonMapFormatterBuilder()
        }
    }
}
