package com.coditory.klog.text.shared

import kotlin.math.min

internal class SizedAppendable(
    private val appendable: Appendable,
    private val maxLength: Int = Int.MAX_VALUE,
    private val maxLengthMarker: String = MAX_LENGTH_MARKER,
) : Appendable {
    private var length: Int = 0
    private var lazyAppend: CharSequence? = null
    private var lazyAppendChar: Char? = null

    fun length(): Int {
        return length
    }

    fun appendLazy(csq: CharSequence?): Appendable {
        lazyAppend = csq
        lazyAppendChar = null
        return this
    }

    fun appendLazy(c: Char): Appendable {
        lazyAppend = null
        lazyAppendChar = c
        return this
    }

    override fun append(csq: CharSequence?): Appendable {
        lazyAppend()
        if (length >= maxLength) return this
        if (csq != null) {
            val csqLength = min(csq.length, maxLength - length)
            length += csqLength
            appendable.append(csq, 0, csqLength)
            appendMaxLengthMarker()
        }
        return this
    }

    override fun append(
        csq: CharSequence?,
        start: Int,
        end: Int,
    ): Appendable {
        lazyAppend()
        if (length >= maxLength) return this
        if (csq != null && start < end) {
            val csqLength = min(end - start, maxLength - length)
            length += csqLength
            appendable.append(csq, start, start + csqLength)
            appendMaxLengthMarker()
        }
        return this
    }

    override fun append(c: Char): Appendable {
        lazyAppend()
        if (length >= maxLength) return this
        length++
        appendable.append(c)
        appendMaxLengthMarker()
        return this
    }

    private fun appendMaxLengthMarker() {
        if (length >= maxLength) {
            appendable.append(maxLengthMarker)
        }
    }

    private fun lazyAppend() {
        if (length >= maxLength) {
            return
        }
        val lazy = lazyAppend
        if (lazy != null) {
            lazyAppend = null
            append(lazy)
            return
        }
        val lazyChar = lazyAppendChar
        if (lazyChar != null) {
            lazyAppendChar = null
            append(lazyChar)
        }
    }

    companion object {
        const val LONG_TEXT_LENGTH: Int = 10 * 1024
        const val MAX_LENGTH_MARKER: String = " [trimmed]"
    }
}
