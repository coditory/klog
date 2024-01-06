package com.coditory.klog.format

import java.lang.Long.signum
import kotlin.math.abs

object QuantityFormat {
    fun formatQuantitySI(bytes: Long): String {
        return formatQuantitySIWithSuffix(bytes, suffix = null)
    }

    fun formatQuantityBin(bytes: Long): String {
        return formatQuantityBinWithSuffix(bytes, suffix = null)
    }

    internal fun formatQuantitySIWithSuffix(
        bytes: Long,
        suffix: Char?,
    ): String {
        if (-1000 < bytes && bytes < 1000) {
            return if (suffix != null) "$bytes $suffix" else "$bytes"
        }
        var significant = bytes
        val x = "kMGTPE"
        var xi = 0
        while (xi < x.length - 1 && (significant <= -999_950 || significant >= 999_950)) {
            significant /= 1000
            xi++
        }
        return if (suffix != null) {
            String.format("%.1f %c$suffix", significant / 1000.0, x[xi])
        } else {
            String.format("%.1f %c", significant / 1000.0, x[xi])
        }
    }

    internal fun formatQuantityBinWithSuffix(
        bytes: Long,
        suffix: Char?,
    ): String {
        val absB =
            if (bytes == Long.MIN_VALUE) {
                Long.MAX_VALUE
            } else {
                abs(bytes.toDouble()).toLong()
            }
        if (absB < 1024) {
            return if (suffix != null) "$bytes $suffix" else "$bytes"
        }
        var value = absB
        val x = "KMGTPE"
        var xi = 0
        var i = 40
        while (xi < x.length - 1 && (i >= 0 && absB > 0xfffccccccccccccL shr i)) {
            value = value shr 10
            xi++
            i -= 10
        }
        value *= signum(bytes).toLong()
        return if (suffix != null) {
            String.format("%.1f %ci$suffix", value / 1024.0, x[xi])
        } else {
            String.format("%.1f %ci", value / 1024.0, x[xi])
        }
    }
}
