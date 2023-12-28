package com.coditory.klog.text.shared

import kotlin.math.max

internal class SizedAppendable(
    private val appendable: Appendable,
    initialSize: Int = 0,
) : Appendable {
    private var length: Int = initialSize

    fun length(): Int {
        return length
    }

    override fun append(csq: CharSequence?): Appendable {
        if (csq != null) {
            length += csq.length
            appendable.append(csq)
        }
        return this
    }

    override fun append(
        csq: CharSequence?,
        start: Int,
        end: Int,
    ): Appendable {
        if (csq != null) {
            length += max(0, end - start - 1)
            appendable.append(csq, start, end)
        }
        return this
    }

    override fun append(c: Char): Appendable {
        length++
        appendable.append(c)
        return this
    }
}
