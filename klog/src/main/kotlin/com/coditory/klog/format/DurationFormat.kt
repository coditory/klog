package com.coditory.klog.format

import java.time.Duration
import kotlin.math.abs
import kotlin.math.round

object DurationFormat {
    fun format(duration: Duration): String {
        val ms = duration.toMillis()
        return if (duration.isZero || ms != 0L) {
            formatMillis(ms)
        } else {
            "${duration.toNanos()}ns"
        }
    }

    fun formatMillis(ms: Long): String {
        if (ms == 0L) {
            return "0.00s"
        }
        val seconds = ms / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        return if (abs(days) > 0) {
            val v = round(hours * 100 / 24.0) / 100.0
            "%.2fd".format(v)
        } else if (abs(hours) > 0) {
            val v = round(minutes * 100 / 60.0) / 100.0
            "%.2fh".format(v)
        } else if (abs(minutes) > 0) {
            val v = round(seconds * 100 / 60.0) / 100.0
            "%.2fm".format(v)
        } else if (abs(seconds) > 0) {
            val v = round(ms * 100 / 1000.0) / 100.0
            "%.2fs".format(v)
        } else {
            "${ms}ms"
        }
    }

    fun formatSignificant(duration: Duration): String {
        val ms = duration.toMillis()
        return if (duration.isZero || ms != 0L) {
            formatMillisSignificant(ms)
        } else {
            "${duration.toNanos()}ns"
        }
    }

    fun formatMillisSignificant(ms: Long): String {
        if (ms == 0L) {
            return "0s"
        }
        val seconds = ms / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        return if (abs(days) > 0) {
            "${days}d"
        } else if (abs(hours) > 0) {
            "${hours}h"
        } else if (abs(minutes) > 0) {
            "${minutes}m"
        } else if (abs(seconds) > 0) {
            "${seconds}s"
        } else {
            "${ms}ms"
        }
    }
}
