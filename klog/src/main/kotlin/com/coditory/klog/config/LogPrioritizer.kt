package com.coditory.klog.config

import com.coditory.klog.Level
import com.coditory.klog.LogEvent
import com.coditory.klog.LogPriority

fun interface LogPrioritizer {
    fun priority(event: LogEvent): LogPriority

    companion object {
        fun prioritizeByMinLevel(minLevel: Level): LogPrioritizer {
            return LogPrioritizer { event: LogEvent ->
                if (event.level >= minLevel) {
                    LogPriority.PRIORITIZED
                } else {
                    LogPriority.STANDARD
                }
            }
        }
    }
}
