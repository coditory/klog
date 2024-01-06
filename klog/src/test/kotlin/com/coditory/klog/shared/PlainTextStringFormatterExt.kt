package com.coditory.klog.shared

import com.coditory.klog.text.plain.PlainTextStringFormatter

fun PlainTextStringFormatter.formatToString(text: String): String {
    val sb = StringBuilder()
    this.format(text, sb)
    return sb.toString()
}
