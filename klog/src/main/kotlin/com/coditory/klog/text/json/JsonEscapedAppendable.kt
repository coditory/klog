package com.coditory.klog.text.json

import com.coditory.klog.text.shared.SizedAppendable.Companion.MAX_LENGTH_MARKER
import kotlin.math.min

internal class JsonEscapedAppendable(
    private val appendable: Appendable,
    private val maxLength: Int = Int.MAX_VALUE,
    private val maxLengthMarker: String = MAX_LENGTH_MARKER,
) : Appendable {
    private var length = 0

    override fun append(csq: CharSequence?): Appendable {
        if (length >= maxLength) return this
        if (csq != null) {
            val csqLength = min(csq.length, maxLength - length)
            length += csqLength
            JsonEncoder.encode(csq, appendable, start = 0, end = csqLength)
            appendMaxLengthMarker()
        }
        return this
    }

    override fun append(
        csq: CharSequence?,
        start: Int,
        end: Int,
    ): Appendable {
        if (length >= maxLength) return this
        if (csq != null && start < end) {
            val csqLength = min(end - start, maxLength - length)
            length += csqLength
            JsonEncoder.encode(csq, appendable, start = start, end = start + csqLength)
            appendMaxLengthMarker()
        }
        return this
    }

    override fun append(ch: Char): Appendable {
        if (length >= maxLength) return this
        length++
        JsonEncoder.encode(ch, appendable)
        appendMaxLengthMarker()
        return this
    }

    private fun appendMaxLengthMarker() {
        if (length >= maxLength) {
            JsonEncoder.encode(maxLengthMarker, appendable)
        }
    }
}
