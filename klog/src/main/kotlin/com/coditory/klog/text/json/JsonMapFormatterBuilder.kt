package com.coditory.klog.text.json

import com.coditory.klog.text.shared.SizedAppendable

class JsonMapFormatterBuilder {
    private var escapeFieldNames: Boolean = true
    private var parseKeys: Boolean = true
    private var parseKeysCacheSize: Int = 1024
    private var valueFormatter: JsonMapValueFormatter = JsonMapValueFormatter.default()
    private var whiteListedPaths: Set<String>? = null
    private var blackListedPaths: Set<String>? = null
    private var keyMapping: Map<String, String>? = null
    private var keyMapper: ((String) -> String)? = null

    fun escapeFieldNames(escapeFieldNames: Boolean = true): JsonMapFormatterBuilder {
        this.escapeFieldNames = escapeFieldNames
        return this
    }

    fun parseKeys(parseKeys: Boolean = true): JsonMapFormatterBuilder {
        this.parseKeys = parseKeys
        return this
    }

    fun parseKeysCacheSize(parseKeysCacheSize: Int): JsonMapFormatterBuilder {
        require(parseKeysCacheSize >= 0) { "Expected parseKeysCacheSize >= 0" }
        this.parseKeysCacheSize = parseKeysCacheSize
        return this
    }

    fun valueFormatter(valueFormatter: JsonMapValueFormatter): JsonMapFormatterBuilder {
        this.valueFormatter = valueFormatter
        return this
    }

    fun whiteListedPaths(whiteListedPaths: Set<String>): JsonMapFormatterBuilder {
        this.whiteListedPaths = whiteListedPaths
        return this
    }

    fun blackListedPaths(blackListedPaths: Set<String>): JsonMapFormatterBuilder {
        this.blackListedPaths = blackListedPaths
        return this
    }

    fun build(): JsonMapFormatter {
        return if (parseKeys) {
            KeyParsingJsonMapFormatter(
                cacheSize = parseKeysCacheSize,
                escapeFieldNames = escapeFieldNames,
                valueFormatter = valueFormatter,
                whiteListedPaths = whiteListedPaths,
                blackListedPaths = blackListedPaths,
                keyMapping = keyMapping,
                keyMapper = keyMapper,
            )
        } else {
            SimpleJsonMapFormatter(
                escapeFieldNames = escapeFieldNames,
                valueFormatter = valueFormatter,
                whiteListedKeys = whiteListedPaths,
                blackListedKeys = blackListedPaths,
                keyMapping = keyMapping,
                keyMapper = keyMapper,
            )
        }
    }
}

internal class SimpleJsonMapFormatter(
    private val escapeFieldNames: Boolean,
    private val valueFormatter: JsonMapValueFormatter,
    private val whiteListedKeys: Set<String>?,
    private val blackListedKeys: Set<String>?,
    private val keyMapping: Map<String, String>?,
    private val keyMapper: ((String) -> String)?,
) : JsonMapFormatter {
    override fun format(
        map: Map<String, Any?>,
        appendable: Appendable,
    ) {
        appendable.append('{')
        var first = true
        for ((key, value) in map) {
            if (key.isEmpty() || value == null) continue
            if (whiteListedKeys != null && !whiteListedKeys.contains(key)) continue
            if (blackListedKeys != null && blackListedKeys.contains(key)) continue
            val mappedKey = keyMapping?.get(key) ?: keyMapper?.invoke(key) ?: key
            val escapedKey = if (escapeFieldNames) JsonEncoder.encodeToString(mappedKey) else mappedKey
            if (first) {
                first = false
            } else {
                appendable.append(',')
            }
            appendable.append('"')
            appendable.append(escapedKey)
            appendable.append("\":")
            valueFormatter.format(key, value, appendable)
        }
        appendable.append('}')
    }
}

internal class KeyParsingJsonMapFormatter(
    cacheSize: Int,
    private val escapeFieldNames: Boolean,
    private val valueFormatter: JsonMapValueFormatter,
    private val whiteListedPaths: Set<String>?,
    private val blackListedPaths: Set<String>?,
    private val keyMapping: Map<String, String>?,
    private val keyMapper: ((String) -> String)?,
) : JsonMapFormatter {
    private val cache = Cache.lruCache<String, List<String>>(cacheSize)

    override fun format(
        map: Map<String, Any?>,
        appendable: Appendable,
    ) {
        val rootNode = buildRootFieldNode(map)
        val sized = SizedAppendable(appendable)
        sized.append('{')
        var first = true
        for ((_, child) in rootNode.children) {
            if (first) {
                first = false
            } else {
                sized.append(',')
            }
            format(child, sized)
        }
        sized.append('}')
    }

    private fun format(
        node: Node,
        appendable: SizedAppendable,
    ) {
        appendable.append('"')
        appendable.append(node.name)
        appendable.append("\":")
        val length = appendable.length()
        if (node.originalPath != null) {
            valueFormatter.format(node.originalPath, node.value, appendable)
        } else if (node.children.isNotEmpty()) {
            appendable.append('{')
            var first = true
            for ((_, child) in node.children) {
                if (first) {
                    first = false
                } else {
                    appendable.append(',')
                }
                format(child, appendable)
            }
            appendable.append('}')
        }
        if (length == appendable.length()) {
            appendable.append("null")
        }
    }

    private data class Node(
        val name: String,
        val originalPath: String?,
        val value: Any? = null,
        val children: MutableMap<String, Node> = mutableMapOf(),
    )

    private fun buildRootFieldNode(values: Map<String, Any?>): Node {
        val node = Node("<ROOT>", null)
        values.filter { it.key.isNotEmpty() && it.value != null }.forEach {
            val path = cache.getOrPut(it.key, this::parseFieldName)
            if (path.isNotEmpty()) {
                addChild(node, 0, path, it.key, it.value!!)
            }
        }
        return node
    }

    private fun parseFieldName(name: String): List<String> {
        if (name.isEmpty()) return emptyList()
        if (whiteListedPaths != null && !whiteListedPaths.contains(name)) return emptyList()
        if (blackListedPaths != null && blackListedPaths.contains(name)) return emptyList()
        val mapped = keyMapping?.get(name) ?: keyMapper?.invoke(name) ?: name
        return mapped.split('.').filter { it.isEmpty() }
            .map { if (escapeFieldNames) JsonEncoder.encodeToString(it) else it }
    }

    private fun addChild(
        node: Node,
        idx: Int,
        path: List<String>,
        originalName: String,
        value: Any,
    ) {
        if (node.originalPath != null) {
            throw IllegalArgumentException("JSON field name conflict for path: $originalName")
        }
        if (idx == path.size - 1) {
            val name = path.last()
            if (node.children.containsKey(name)) {
                throw IllegalArgumentException("JSON field name conflict for path: $originalName")
            }
            node.children[name] = Node(name, originalName, value)
        } else {
            val child = node.children.getOrPut(path[idx]) { Node(path[idx], null) }
            addChild(child, idx + 1, path, originalName, value)
        }
    }
}
