package com.coditory.klog.text.json

fun interface JsonContextFormatter {
    fun format(
        context: Map<String, String>,
        appendable: Appendable,
    )

    companion object {
        fun default(parseKeys: Boolean = true): JsonContextFormatter {
            val formatter = JsonMapFormatter.default(parseKeys = parseKeys)
            return JsonContextFormatter { ctx, appendable ->
                formatter.format(ctx, appendable)
            }
        }

        fun from(formatter: JsonMapFormatter): JsonContextFormatter {
            return JsonContextFormatter { ctx, appendable ->
                formatter.format(ctx, appendable)
            }
        }
    }
}
