package com.coditory.klog

data class LogEntry(
    val priority: LogPriority,
    val message: String,
    val throwable: Throwable? = null,
    val items: Map<String, Any>,
)
