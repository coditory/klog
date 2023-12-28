package com.coditory.klog.text.plain

internal class PrecomputedTextFormatter(
    private val precomputed: Map<String, String>,
    private val formatter: PlainTextStringFormatter,
) : PlainTextStringFormatter {
    override fun format(
        text: String,
        appendable: Appendable,
    ) {
        val value = precomputed[text]
        if (value != null) {
            appendable.append(value)
        } else {
            formatter.format(text, appendable)
        }
    }

    companion object {
        fun precompute(
            formatter: PlainTextStringFormatter,
            values: List<String>,
        ): PlainTextStringFormatter {
            val cache = mutableMapOf<String, String>()
            val sb = StringBuilder()
            for (value in values) {
                formatter.format(value, sb)
                cache[value] = sb.toString()
                sb.clear()
            }
            return PrecomputedTextFormatter(cache, formatter)
        }
    }
}
