package com.coditory.klog

import java.time.ZonedDateTime

data class LogEvent(
    val priority: LogPriority = LogPriority.STANDARD,
    // migrate to kotlin ZonedDateTime when available https://github.com/Kotlin/kotlinx-datetime/issues/163
    val timestamp: ZonedDateTime,
    val logger: String,
    val level: Level,
    val thread: String,
    val message: String,
    val throwable: Throwable? = null,
    val context: Map<String, String> = emptyMap(),
    val items: Map<String, Any> = emptyMap(),
)
