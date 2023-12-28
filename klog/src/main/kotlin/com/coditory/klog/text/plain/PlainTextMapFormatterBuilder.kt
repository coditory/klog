package com.coditory.klog.text.plain

import kotlin.math.min

class PlainTextMapFormatterBuilder internal constructor() {
    private var prefix: StyledText = StyledText("[")
    private var postfix: StyledText = StyledText("]")
    private var keyValueSeparator: StyledText = StyledText("=")
    private var entrySeparator: StyledText = StyledText(" ")
    private var whiteListedKeys: Set<String>? = null
    private var blackListedKeys: Set<String>? = null
    private var orderedKeys: List<String>? = null
    private var keyMapping: Map<String, String>? = null
    private var keyMapper: ((String) -> String)? = null
    private var valueMapper: PlainTextMapValueFormatter? = null
    private var maxLength: Int = Int.MAX_VALUE
    private var maxKeyLength: Int = Int.MAX_VALUE
    private var maxValueLength: Int = Int.MAX_VALUE
    private var padding: Padding? = null
    private var keyStyle: TextStyle = TextStyle.empty()
    private var valueStyle: TextStyle = TextStyle.empty()

    fun prefix(
        prefix: String,
        style: TextStyle = TextStyle.empty(),
    ): PlainTextMapFormatterBuilder {
        this.prefix = StyledText(prefix, style)
        return this
    }

    fun postfix(
        postfix: String,
        style: TextStyle = TextStyle.empty(),
    ): PlainTextMapFormatterBuilder {
        this.postfix = StyledText(postfix, style)
        return this
    }

    fun keyValueSeparator(
        keyValueSeparator: String,
        style: TextStyle = TextStyle.empty(),
    ): PlainTextMapFormatterBuilder {
        this.keyValueSeparator = StyledText(keyValueSeparator, style)
        return this
    }

    fun entrySeparator(
        entrySeparator: String,
        style: TextStyle = TextStyle.empty(),
    ): PlainTextMapFormatterBuilder {
        this.entrySeparator = StyledText(entrySeparator, style)
        return this
    }

    fun whiteListedKeys(keys: Set<String>): PlainTextMapFormatterBuilder {
        this.whiteListedKeys = keys
        return this
    }

    fun blackListedKeys(keys: Set<String>): PlainTextMapFormatterBuilder {
        this.blackListedKeys = keys
        return this
    }

    fun orderedKeys(keys: List<String>): PlainTextMapFormatterBuilder {
        this.orderedKeys = keys
        return this
    }

    fun orderedWhiteListedKeys(keys: List<String>): PlainTextMapFormatterBuilder {
        this.whiteListedKeys = keys.toSet()
        this.orderedKeys = keys
        return this
    }

    fun keyMapping(
        keyMapping: Map<String, String>,
        skipRest: Boolean = false,
    ): PlainTextMapFormatterBuilder {
        if (skipRest) {
            this.whiteListedKeys = keyMapping.keys
        }
        this.keyMapping = keyMapping
        this.orderedKeys = keyMapping.asSequence().map { it.key }.toList()
        return this
    }

    fun keyMapper(keyMapper: (String) -> String): PlainTextMapFormatterBuilder {
        this.keyMapper = keyMapper
        return this
    }

    fun valueMapper(valueMapper: PlainTextMapValueFormatter): PlainTextMapFormatterBuilder {
        this.valueMapper = valueMapper
        return this
    }

    fun maxKeyLength(maxKeyLength: Int): PlainTextMapFormatterBuilder {
        this.maxKeyLength = maxKeyLength
        return this
    }

    fun maxValueLength(maxValueLength: Int): PlainTextMapFormatterBuilder {
        this.maxValueLength = maxValueLength
        return this
    }

    fun maxLength(maxLength: Int): PlainTextMapFormatterBuilder {
        this.maxLength = maxLength
        return this
    }

    fun padLeft(pad: Char = ' '): PlainTextMapFormatterBuilder {
        this.padding = Padding.left(pad)
        return this
    }

    fun padRight(pad: Char = ' '): PlainTextMapFormatterBuilder {
        this.padding = Padding.right(pad)
        return this
    }

    fun keyStyle(keyStyle: TextStyle): PlainTextMapFormatterBuilder {
        this.keyStyle = keyStyle
        return this
    }

    fun valueStyle(valueStyle: TextStyle): PlainTextMapFormatterBuilder {
        this.valueStyle = valueStyle
        return this
    }

    fun build(): PlainTextMapFormatter {
        return ConfigurablePlainTextMapFormatter(
            prefix = prefix,
            postfix = postfix,
            keyValueSeparator = keyValueSeparator,
            entrySeparator = entrySeparator,
            whiteListedKeys = whiteListedKeys,
            blackListedKeys = blackListedKeys,
            orderedKeys = orderedKeys,
            keyMapping = keyMapping,
            keyMapper = keyMapper,
            valueFormatter = valueMapper,
            maxLength = maxLength,
            maxKeyLength = maxKeyLength,
            maxValueLength = maxValueLength,
            padding = padding,
            keyStyle = keyStyle,
            valueStyle = valueStyle,
        )
    }
}

internal class ConfigurablePlainTextMapFormatter(
    private val prefix: StyledText,
    private val postfix: StyledText,
    private val keyValueSeparator: StyledText,
    private val entrySeparator: StyledText,
    private val whiteListedKeys: Set<String>?,
    private val blackListedKeys: Set<String>?,
    private val orderedKeys: List<String>?,
    private val keyMapping: Map<String, String>?,
    private val keyMapper: ((String) -> String)?,
    private val valueFormatter: PlainTextMapValueFormatter?,
    private val maxLength: Int,
    private val maxKeyLength: Int,
    private val maxValueLength: Int,
    private val padding: Padding?,
    private val keyStyle: TextStyle,
    private val valueStyle: TextStyle,
) : PlainTextMapFormatter {
    private val keyOrder =
        orderedKeys
            ?.associate { it to orderedKeys.indexOf(it) }
            ?: emptyMap()
    private val pad =
        if (padding != null && maxLength < Int.MAX_VALUE) {
            ("" + padding.pad).repeat(maxLength)
        } else {
            ""
        }

    override fun format(
        map: Map<String, Any>,
        appendable: Appendable,
    ) {
        val mapped =
            map.keys.asSequence()
                .filter { whiteListedKeys == null || whiteListedKeys.contains(it) }
                .filter { blackListedKeys == null || !blackListedKeys.contains(it) }
                .filter { map[it] != null }
                .map { key ->
                    val mappedKey =
                        keyMapping?.get(key)
                            ?: keyMapper?.invoke(key)
                            ?: key
                    val value = map[key]
                    val mappedValue =
                        valueFormatter?.let {
                            val sb = StringBuilder()
                            it.format(key, value, sb)
                            sb.toString()
                        } ?: value?.toString() ?: ""
                    mappedKey to mappedValue
                }
                .sortedBy { keyOrder[it.first] ?: Int.MAX_VALUE }
                .toMap()
        if (mapped.isNotEmpty()) {
            prefix.format(appendable)
        }
        val expectedLength = expectedLength(mapped)
        if (padding?.left() == true && expectedLength < maxLength) {
            appendable.append(pad, 0, maxLength - expectedLength)
        }
        var lengthLeft = maxLength
        var first = true
        for ((key, value) in mapped) {
            if (first) {
                first = false
            } else {
                val entrySeparatorLength = min(lengthLeft, entrySeparator.length())
                lengthLeft -= entrySeparatorLength
                appendable.append(entrySeparator.style.prefix)
                appendable.append(entrySeparator.text, 0, entrySeparatorLength)
                appendable.append(entrySeparator.style.postfix)
            }
            // key
            val keyLength = min(lengthLeft, key.length)
            lengthLeft -= keyLength
            appendable.append(keyStyle.prefix)
            appendable.append(key, 0, keyLength)
            appendable.append(keyStyle.postfix)
            // separator
            val keyValueSeparatorLength = min(lengthLeft, keyValueSeparator.length())
            lengthLeft -= keyValueSeparatorLength
            appendable.append(keyValueSeparator.style.prefix)
            appendable.append(keyValueSeparator.text, 0, keyValueSeparatorLength)
            appendable.append(keyValueSeparator.style.postfix)
            // value
            val valueSeparatorLength = min(lengthLeft, value.length)
            lengthLeft -= keyValueSeparatorLength
            appendable.append(valueStyle.prefix)
            appendable.append(value, 0, valueSeparatorLength)
            appendable.append(valueStyle.postfix)
            if (lengthLeft <= entrySeparator.length()) {
                break
            }
        }
        if (padding?.right() == true && expectedLength < maxLength) {
            appendable.append(pad, 0, maxLength - expectedLength)
        }
        if (mapped.isNotEmpty()) {
            postfix.format(appendable)
        }
    }

    private fun expectedLength(map: Map<String, String>): Int {
        if (map.isEmpty()) return 0
        val entriesLength =
            map
                .map {
                    min(it.key.length, maxKeyLength) +
                        min(
                            it.value.length,
                            maxValueLength,
                        ) + keyValueSeparator.length()
                }
                .sum()
        return prefix.length() + postfix.length() + entriesLength + (map.size - 1) * entrySeparator.length()
    }
}
