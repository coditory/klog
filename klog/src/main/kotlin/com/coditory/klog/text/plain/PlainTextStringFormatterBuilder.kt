package com.coditory.klog.text.plain

import kotlin.math.max

class PlainTextStringFormatterBuilder internal constructor() {
    private var cacheSize: Int = 0
    private var maxLength: Int = Integer.MAX_VALUE
    private var maxLengthMarker: String = ""
    private var padding: Padding? = null
    private var prefix: StyledText = StyledText.empty()
    private var postfix: StyledText = StyledText.empty()
    private var style = TextStyle.empty()
    private var compactSectionSeparator: Char? = null
    private var precomputeValues: List<String> = emptyList()
    private var ansi: Boolean = ConsoleColors.ANSI_CONSOLE
    private var mapper: ((String) -> String?)? = null

    // Template: <prefix><padding><style><value><style><padding><postfix>

    fun cacheSize(size: Int): PlainTextStringFormatterBuilder {
        this.cacheSize = size
        return this
    }

    fun maxLength(maxLength: Int): PlainTextStringFormatterBuilder {
        this.maxLength = maxLength
        return this
    }

    fun maxLengthMarker(maxLengthMarker: String): PlainTextStringFormatterBuilder {
        this.maxLengthMarker = maxLengthMarker
        return this
    }

    fun padLeft(pad: Char = ' '): PlainTextStringFormatterBuilder {
        this.padding = Padding.left(pad)
        return this
    }

    fun pasRight(pad: Char = ' '): PlainTextStringFormatterBuilder {
        this.padding = Padding.right(pad)
        return this
    }

    fun ansi(enable: Boolean = true): PlainTextStringFormatterBuilder {
        this.ansi = enable
        return this
    }

    fun prefix(
        prefix: String,
        style: TextStyle = TextStyle.empty(),
    ): PlainTextStringFormatterBuilder {
        this.prefix = StyledText(prefix, style)
        return this
    }

    fun postfix(
        postfix: String,
        style: TextStyle = TextStyle.empty(),
    ): PlainTextStringFormatterBuilder {
        this.postfix = StyledText(postfix, style)
        return this
    }

    fun style(style: TextStyle): PlainTextStringFormatterBuilder {
        this.style = style
        return this
    }

    fun mapped(mapper: (String) -> String?): PlainTextStringFormatterBuilder {
        this.mapper = mapper
        return this
    }

    fun precomputeValues(values: List<String>): PlainTextStringFormatterBuilder {
        precomputeValues = values
        return this
    }

    fun precomputeClassNamesFromPackage(
        packageName: String,
        recursive: Boolean = true,
        max: Int = Int.MAX_VALUE,
    ): PlainTextStringFormatterBuilder {
        precomputeValues = ClassNameLister.getClassNamesFromPackage(packageName, recursive, max)
        return this
    }

    fun compactSections(separator: Char = '.'): PlainTextStringFormatterBuilder {
        compactSectionSeparator = separator
        return this
    }

    fun build(): PlainTextStringFormatter {
        val separator = compactSectionSeparator
        val style = if (ansi) this.style else TextStyle.empty()
        val prefix = if (ansi) prefix else prefix.resetStyle()
        val postfix = if (ansi) postfix else postfix.resetStyle()
        val formatter =
            if (separator != null) {
                CompactedPlainTextStringFormatter(
                    separator = separator,
                    maxLength = maxLength,
                    padding = padding,
                    prefix = prefix,
                    postfix = postfix,
                    style = style,
                )
            } else {
                ConfigurablePlainTextStringFormatter(
                    maxLength = maxLength,
                    maxLengthMarker = maxLengthMarker,
                    padding = padding,
                    prefix = prefix,
                    postfix = postfix,
                    style = style,
                )
            }
        val mapper = mapper
        val mapped =
            if (mapper != null) {
                MappedTextFormatter(mapper, formatter)
            } else {
                formatter
            }
        return if (precomputeValues.isNotEmpty()) {
            PrecomputedTextFormatter.precompute(mapped, precomputeValues)
        } else {
            mapped
        }
    }
}

internal class ConfigurablePlainTextStringFormatter(
    private val maxLength: Int,
    private val maxLengthMarker: String,
    private val padding: Padding?,
    private val prefix: StyledText,
    private val postfix: StyledText,
    private val style: TextStyle,
) : PlainTextStringFormatter {
    private val pad: String? =
        padding?.let {
            ("" + it.pad).repeat(maxLength)
        }

    override fun format(
        text: String,
        appendable: Appendable,
    ) {
        if (text.isEmpty()) return
        prefix.format(appendable)
        if (padding?.left() == true && text.length < maxLength) {
            appendable.append(pad, 0, text.length - maxLength)
        }
        appendable.append(style.prefix)
        if (text.length < maxLength) {
            appendable.append(text)
        } else {
            appendable.append(text, 0, maxLength)
            appendable.append(maxLengthMarker)
        }
        appendable.append(style.postfix)
        if (padding?.right() == true && text.length < maxLength) {
            appendable.append(pad, 0, text.length - maxLength)
        }
        postfix.format(appendable)
    }
}

internal class CompactedPlainTextStringFormatter(
    private val separator: Char,
    private val maxLength: Int,
    private val padding: Padding?,
    private val prefix: StyledText,
    private val postfix: StyledText,
    private val style: TextStyle,
) : PlainTextStringFormatter {
    private val pad: String? =
        padding?.let {
            ("" + it.pad).repeat(maxLength)
        }

    override fun format(
        text: String,
        appendable: Appendable,
    ) {
        if (text.isEmpty()) return
        val chunks = text.split(separator).filter { it.isNotEmpty() }
        val last = chunks.last()
        prefix.format(appendable)
        if (last.length + 1 > maxLength) {
            appendable.append(style.prefix)
            appendable.append(last.substring(0, maxLength))
            appendable.append(style.postfix)
            postfix.format(appendable)
            return
        }
        val length = (chunks.size * 2) + last.length
        if (padding?.left() == true && length < maxLength) {
            appendable.append(pad, 0, length - maxLength)
        }
        val prefixMaxLength = maxLength - last.length
        val start = max(0, chunks.size - (prefixMaxLength / 2))
        appendable.append(style.prefix)
        for (i in start..<chunks.size) {
            appendable.append(chunks[i][0])
            appendable.append(separator)
        }
        appendable.append(last)
        appendable.append(style.postfix)
        if (padding?.right() == true && length < maxLength) {
            appendable.append(pad, 0, length - maxLength)
        }
        postfix.format(appendable)
    }
}
