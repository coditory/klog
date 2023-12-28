package com.coditory.klog.text.json

internal class JsonEscapedAppendable(
    private val appendable: Appendable,
) : Appendable {
    override fun append(csq: CharSequence?): Appendable {
        if (csq != null) {
            JsonEncoder.encode(csq, appendable)
        }
        return this
    }

    override fun append(
        csq: CharSequence?,
        start: Int,
        end: Int,
    ): Appendable {
        if (csq != null) {
            JsonEncoder.encode(csq, appendable, start = start, end = end)
        }
        return this
    }

    override fun append(ch: Char): Appendable {
        JsonEncoder.encode(ch, appendable)
        return this
    }
}
