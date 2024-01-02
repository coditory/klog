package com.coditory.klog

enum class LogEventField {
    TIMESTAMP,
    LEVEL,
    THREAD,
    CONTEXT,
    LOGGER,
    MESSAGE,
    ITEMS,
    EXCEPTION,
    ;

    internal fun skip(
        event: LogEvent,
        mergeContextToItems: Boolean,
    ): Boolean {
        if (this == CONTEXT && event.context.isEmpty()) return true
        if (this == ITEMS) {
            return if (mergeContextToItems) {
                event.items.isEmpty() && event.context.isEmpty()
            } else {
                event.items.isEmpty()
            }
        }
        return false
    }

    companion object {
        fun all(): List<LogEventField> {
            return listOf(TIMESTAMP, LEVEL, THREAD, CONTEXT, LOGGER, MESSAGE, ITEMS, EXCEPTION)
        }
    }
}
