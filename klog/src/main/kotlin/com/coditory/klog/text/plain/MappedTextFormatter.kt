package com.coditory.klog.text.plain

internal class MappedTextFormatter(
    private val mapper: (String) -> String?,
    private val formatter: PlainTextStringFormatter,
) : PlainTextStringFormatter {
    override fun format(
        text: String,
        appendable: Appendable,
    ) {
        val mapped = mapper.invoke(text) ?: text
        formatter.format(mapped, appendable)
    }
}
