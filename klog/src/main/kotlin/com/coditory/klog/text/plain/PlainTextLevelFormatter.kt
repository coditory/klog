package com.coditory.klog.text.plain

import com.coditory.klog.Level

fun interface PlainTextLevelFormatter {
    fun format(
        level: Level,
        appendable: Appendable,
    )

    companion object {
        private val DEFAULT_LEVEL_STYLES: Map<Level, TextStyle> =
            mapOf(
                Level.TRACE to TextStyle.whiteBold(),
                Level.DEBUG to TextStyle.blueBold(),
                Level.INFO to TextStyle.greenBold(),
                Level.WARN to TextStyle.yellowBold(),
                Level.ERROR to TextStyle.redBold(),
            )

        fun default(): PlainTextLevelFormatter {
            return PlainTextLevelFormatter { level, appendable -> appendable.append(level.name) }
        }

        fun fromTextFormatter(config: (PlainTextStringFormatterBuilder) -> Unit): PlainTextLevelFormatter {
            val builder = PlainTextStringFormatter.builder()
            config.invoke(builder)
            return fromTextFormatter(builder.build())
        }

        fun fromTextFormatter(textFormatter: PlainTextStringFormatter): PlainTextLevelFormatter {
            return PlainTextLevelFormatter { level, appendable -> textFormatter.format(level.name, appendable) }
        }

        fun leftPaddedAndStyled(
            n: Int = Level.LONGEST_LEVEL_NAME,
            pad: Char = ' ',
            styles: Map<Level, TextStyle> = DEFAULT_LEVEL_STYLES,
        ): PlainTextLevelFormatter {
            val padding = ("" + pad).repeat(n)
            val cache =
                Level.levels().associateWith {
                    val value = padding.substring(0, n - it.name.length) + it.name
                    value
                }
            return PlainTextLevelFormatter { level, appendable ->
                val style = styles[level] ?: TextStyle.empty()
                appendable.append(style.prefix)
                appendable.append(cache[level])
                appendable.append(style.postfix)
            }
        }

        fun rightPaddedAndStyled(
            n: Int = Level.LONGEST_LEVEL_NAME,
            pad: Char = ' ',
            styles: Map<Level, TextStyle> = DEFAULT_LEVEL_STYLES,
        ): PlainTextLevelFormatter {
            val padding = ("" + pad).repeat(n)
            val cache =
                Level.levels().associateWith {
                    val value = it.name + padding.substring(0, n - it.name.length)
                    value
                }
            return PlainTextLevelFormatter { level, appendable ->
                val style = styles[level] ?: TextStyle.empty()
                appendable.append(style.prefix)
                appendable.append(cache[level])
                appendable.append(style.postfix)
            }
        }

        fun leftPadded(
            n: Int = Level.LONGEST_LEVEL_NAME,
            pad: Char = ' ',
        ): PlainTextLevelFormatter {
            val padding = ("" + pad).repeat(n)
            val cache =
                Level.levels().associateWith {
                    val value = padding.substring(0, n - it.name.length) + it.name
                    value
                }
            return PlainTextLevelFormatter { level, appendable -> appendable.append(cache[level]) }
        }

        fun rightPadded(
            n: Int = Level.LONGEST_LEVEL_NAME,
            pad: Char = ' ',
        ): PlainTextLevelFormatter {
            val padding = ("" + pad).repeat(n)
            val cache =
                Level.levels().associateWith {
                    val value = it.name + padding.substring(0, n - it.name.length)
                    value
                }
            return PlainTextLevelFormatter { level, appendable -> appendable.append(cache[level]) }
        }
    }
}
