package com.coditory.klog.text.plain

fun interface PlainTextContextFormatter {
    fun format(
        context: Map<String, String>,
        appendable: Appendable,
    )

    companion object {
        fun default(): PlainTextContextFormatter {
            return from {}
        }

        fun from(config: (PlainTextMapFormatterBuilder) -> Unit): PlainTextContextFormatter {
            val builder = PlainTextMapFormatter.builder()
            config.invoke(builder)
            val formatter = builder.build()
            return from(formatter)
        }

        fun from(formatter: PlainTextMapFormatter): PlainTextContextFormatter {
            return PlainTextContextFormatter { ctx, appendable -> formatter.format(ctx, appendable) }
        }
    }
}
