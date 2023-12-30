package com.coditory.klog.text.json

import com.coditory.klog.LogEvent
import com.coditory.klog.LogEventField
import com.coditory.klog.text.TextLogEventSerializer
import com.coditory.klog.text.shared.SizedAppendable

class JsonLogEventSerializer(
    private val whitelist: Set<LogEventField> = LogEventField.all().toSet(),
    private val mergeContextToItems: Boolean = false,
    private val fieldNameMapper: ((LogEventField) -> String)? = null,
    private val fieldNames: Map<LogEventField, String> = emptyMap(),
    private val parseFieldNames: Boolean = true,
    private val escapeFieldNames: Boolean = true,
    private val timestampFormatter: JsonTimestampFormatter = JsonTimestampFormatter.default(),
    private val levelFormatter: JsonLevelFormatter = JsonLevelFormatter.default(),
    private val loggerNameFormatter: JsonStringFormatter = JsonStringFormatter.default(escape = false),
    private val threadFormatter: JsonStringFormatter = JsonStringFormatter.default(escape = false),
    private val messageFormatter: JsonStringFormatter = JsonStringFormatter.default(maxLength = 10 * 1024),
    private val contextFormatter: JsonContextFormatter = JsonContextFormatter.default(),
    private val itemsFormatter: JsonMapFormatter = JsonMapFormatter.default(),
) : TextLogEventSerializer {
    private val fieldPaths =
        LogEventField.all()
            .filter { whitelist.contains(it) }
            .associateWith { field ->
                val name = fieldNames[field] ?: fieldNameMapper?.invoke(field) ?: field.name.lowercase()
                val path =
                    if (parseFieldNames) {
                        name.split('.').filter { it.isNotEmpty() }
                    } else {
                        listOf(name)
                    }
                val escaped =
                    if (escapeFieldNames) {
                        path.map { JsonEncoder.encodeToString(it) }
                    } else {
                        path
                    }
                escaped
            }
    private val rootNode = buildRootFieldNode(fieldPaths)

    override fun format(
        event: LogEvent,
        appendable: Appendable,
    ) {
        val sizedAppendable = SizedAppendable(appendable)
        sizedAppendable.append('{')
        var length = sizedAppendable.length()
        for ((_, node) in rootNode.children) {
            if (isEmpty(event, node)) continue
            if (length < sizedAppendable.length()) {
                sizedAppendable.append(',')
                length = sizedAppendable.length()
            }
            format(event, node, sizedAppendable)
        }
        sizedAppendable.append('}')
    }

    private fun isEmpty(
        event: LogEvent,
        node: Node,
    ): Boolean {
        return if (node.value != null) {
            node.value.skip(event, mergeContextToItems)
        } else {
            node.children.isEmpty() || node.children.all { isEmpty(event, it.value) }
        }
    }

    private fun format(
        event: LogEvent,
        node: Node,
        appendable: SizedAppendable,
    ) {
        appendable.append('"')
        appendable.append(node.name)
        appendable.append("\":")
        val length = appendable.length()
        if (node.value != null) {
            format(event, node.value, appendable)
        } else if (node.children.isNotEmpty()) {
            appendable.append('{')
            var first = true
            for ((_, child) in node.children) {
                if (first) {
                    first = false
                } else {
                    appendable.append(',')
                }
                format(event, child, appendable)
            }
            appendable.append('}')
        }
        if (length == appendable.length()) {
            appendable.append("null")
        }
    }

    private fun format(
        event: LogEvent,
        field: LogEventField,
        appendable: Appendable,
    ) {
        when (field) {
            LogEventField.TIMESTAMP -> formatTimestamp(event, appendable)
            LogEventField.LEVEL -> formatLevel(event, appendable)
            LogEventField.LOGGER -> formatLogger(event, appendable)
            LogEventField.THREAD -> formatThread(event, appendable)
            LogEventField.CONTEXT -> formatContext(event, appendable)
            LogEventField.MESSAGE -> formatMessage(event, appendable)
            LogEventField.ITEMS -> formatItems(event, appendable)
        }
    }

    private fun formatTimestamp(
        event: LogEvent,
        appendable: Appendable,
    ) {
        timestampFormatter.format(event.timestamp, appendable)
    }

    private fun formatLevel(
        event: LogEvent,
        appendable: Appendable,
    ) {
        levelFormatter.format(event.level, appendable)
    }

    private fun formatLogger(
        event: LogEvent,
        appendable: Appendable,
    ) {
        loggerNameFormatter.format(event.logger, appendable)
    }

    private fun formatThread(
        event: LogEvent,
        appendable: Appendable,
    ) {
        threadFormatter.format(event.thread, appendable)
    }

    private fun formatContext(
        event: LogEvent,
        appendable: Appendable,
    ) {
        contextFormatter.format(event.context, appendable)
    }

    private fun formatMessage(
        event: LogEvent,
        appendable: Appendable,
    ) {
        messageFormatter.format(event.message, appendable)
    }

    private fun formatItems(
        event: LogEvent,
        appendable: Appendable,
    ) {
        val items = if (mergeContextToItems) event.items + event.context else event.items
        itemsFormatter.format(items, appendable)
    }

    private data class Node(
        val name: String,
        val value: LogEventField? = null,
        val children: MutableMap<String, Node> = mutableMapOf(),
    )

    private fun buildRootFieldNode(fieldNames: Map<LogEventField, List<String>>): Node {
        val node = Node("<ROOT>")
        fieldNames.forEach { (field, path) ->
            addChild(node, 0, path, field)
        }
        return node
    }

    private fun addChild(
        node: Node,
        idx: Int,
        path: List<String>,
        value: LogEventField,
    ) {
        if (node.value != null) {
            throw IllegalArgumentException("JSON field name conflict for path: " + path.joinToString("."))
        }
        if (idx == path.size - 1) {
            val name = path.last()
            if (node.children.containsKey(name)) {
                throw IllegalArgumentException("JSON field name conflict for path: " + path.joinToString("."))
            }
            node.children[name] = Node(name, value)
        } else {
            val child = node.children.getOrPut(path[idx]) { Node(path[idx]) }
            addChild(child, idx + 1, path, value)
        }
    }

    companion object {
        fun default(): JsonLogEventSerializer {
            return JsonLogEventSerializer()
        }
    }
}
