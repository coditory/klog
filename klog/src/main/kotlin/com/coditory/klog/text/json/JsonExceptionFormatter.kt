package com.coditory.klog.text.json

import com.coditory.klog.text.plain.PlainTextExceptionFormatter

fun interface JsonExceptionFormatter {
    fun format(
        throwable: Throwable,
        appendable: Appendable,
    )

    companion object {
        fun from(
            formatter: PlainTextExceptionFormatter,
            escape: Boolean = true,
        ): JsonExceptionFormatter {
            return JsonExceptionFormatter { throwable, appendable ->
                appendable.append('"')
                val wrapped = if (escape) JsonEscapedAppendable(appendable) else appendable
                formatter.format(throwable, wrapped)
                appendable.append('"')
            }
        }
    }
}
