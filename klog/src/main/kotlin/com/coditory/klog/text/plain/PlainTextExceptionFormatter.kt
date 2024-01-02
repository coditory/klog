package com.coditory.klog.text.plain

import com.coditory.klog.text.shared.SizedAppendable.Companion.LONG_TEXT_LENGTH
import com.coditory.klog.text.shared.SizedAppendable.Companion.MAX_LENGTH_MARKER
import java.io.PrintWriter
import java.io.StringWriter

fun interface PlainTextExceptionFormatter {
    fun format(
        throwable: Throwable,
        appendable: Appendable,
    )

    companion object {
        fun fullStackTrace(
            newLine: Boolean = true,
            maxLength: Int = LONG_TEXT_LENGTH,
            maxLengthMarker: String = MAX_LENGTH_MARKER,
        ): PlainTextExceptionFormatter {
            return PlainTextExceptionFormatter { e, appendable ->
                val sw = StringWriter()
                val pw = PrintWriter(sw)
                e.printStackTrace(pw)
                val stack = sw.toString()
                if (newLine) {
                    appendable.append('\n')
                }
                if (stack.length <= maxLength) {
                    appendable.append(stack)
                } else {
                    val trimmed = stack.substring(0, maxLength - maxLengthMarker.length)
                    appendable.append(trimmed)
                    appendable.append(maxLengthMarker)
                }
            }
        }

        fun messageOnly(
            newLine: Boolean = false,
            maxLength: Int = LONG_TEXT_LENGTH,
            maxLengthMarker: String = MAX_LENGTH_MARKER,
        ): PlainTextExceptionFormatter {
            return PlainTextExceptionFormatter { e, appendable ->
                val msg = e.message
                if (!msg.isNullOrEmpty()) {
                    if (newLine) {
                        appendable.append('\n')
                    }
                    if (msg.length <= maxLength) {
                        appendable.append(msg)
                    } else {
                        appendable.append(msg, 0, maxLength)
                        appendable.append(maxLengthMarker)
                    }
                }
            }
        }
    }
}
