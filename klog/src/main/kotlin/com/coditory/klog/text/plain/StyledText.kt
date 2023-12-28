package com.coditory.klog.text.plain

internal data class StyledText(
    val text: String,
    val style: TextStyle = TextStyle.empty(),
) {
    fun format(appendable: Appendable) {
        if (text.isNotEmpty()) {
            appendable.append(style.prefix)
            appendable.append(text)
            appendable.append(style.postfix)
        }
    }

    fun resetStyle(): StyledText {
        return StyledText(text)
    }

    fun length(): Int {
        return text.length
    }

    companion object {
        fun empty(): StyledText {
            return StyledText("")
        }
    }
}
