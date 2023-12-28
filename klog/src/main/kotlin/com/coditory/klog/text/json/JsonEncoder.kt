package com.coditory.klog.text.json

internal object JsonEncoder {
    private val ESCAPE_STRINGS: Array<String?> =
        arrayOfNulls<String>(93).apply {
            for (c in 0..0x1f) {
                val c1 = toHexChar(c shr 12)
                val c2 = toHexChar(c shr 8)
                val c3 = toHexChar(c shr 4)
                val c4 = toHexChar(c)
                this[c] = "\\u$c1$c2$c3$c4"
            }
            this['"'.code] = "\\\""
            this['\\'.code] = "\\\\"
            this['\t'.code] = "\\t"
            this['\b'.code] = "\\b"
            this['\n'.code] = "\\n"
            this['\r'.code] = "\\r"
            this[0x0c] = "\\f"
        }

    private fun toHexChar(i: Int): Char {
        val d = i and 0xf
        return if (d < 10) {
            (d + '0'.code).toChar()
        } else {
            (d - 10 + 'a'.code).toChar()
        }
    }

    fun encodeToString(csq: CharSequence): String {
        val sb = StringBuilder()
        encode(csq, sb)
        return sb.toString()
    }

    fun encode(
        csq: CharSequence,
        appendable: Appendable,
        start: Int = 0,
        end: Int = csq.length,
        quote: Boolean = false,
    ) {
        if (quote) appendable.append('"')
        if (csq.length < end) {
            throw ArrayIndexOutOfBoundsException("Expected end < sequence.length")
        }
        if (start > end) {
            throw ArrayIndexOutOfBoundsException("Expected start <= end")
        }
        var lastPos = 0
        for (i in start..<end) {
            val c = csq[i].code
            if (c < ESCAPE_STRINGS.size && ESCAPE_STRINGS[c] != null) {
                appendable.append(csq, lastPos, i) // flush prev
                lastPos = i + 1
                appendable.append(ESCAPE_STRINGS[c])
            }
        }
        if (lastPos != 0) {
            appendable.append(csq, lastPos, csq.length)
        } else {
            appendable.append(csq)
        }
        if (quote) appendable.append('"')
    }

    fun encode(
        char: Char,
        appendable: Appendable,
    ) {
        val c = char.code
        if (c < ESCAPE_STRINGS.size && ESCAPE_STRINGS[c] != null) {
            appendable.append(ESCAPE_STRINGS[c])
        } else {
            appendable.append(char)
        }
    }
}
