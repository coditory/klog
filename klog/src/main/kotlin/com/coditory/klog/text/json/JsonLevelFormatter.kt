package com.coditory.klog.text.json

import com.coditory.klog.Level
import com.coditory.klog.text.plain.PlainTextLevelFormatter

fun interface JsonLevelFormatter {
    fun format(
        level: Level,
        appendable: Appendable,
    )

    companion object {
        fun default(): JsonLevelFormatter {
            return JsonLevelFormatter { level, appendable ->
                appendable.append('"')
                appendable.append(level.name)
                appendable.append('"')
            }
        }

        fun from(
            formatter: PlainTextLevelFormatter,
            escape: Boolean = true,
        ): JsonLevelFormatter {
            return JsonLevelFormatter { level, appendable ->
                appendable.append('"')
                val wrapped = if (escape) JsonEscapedAppendable(appendable) else appendable
                formatter.format(level, wrapped)
                appendable.append('"')
            }
        }

        fun from(formatter: JsonStringFormatter): JsonLevelFormatter {
            return JsonLevelFormatter { level, appendable ->
                formatter.format(level.name, appendable)
            }
        }
    }
}
