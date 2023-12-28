package com.coditory.klog.text

import com.coditory.klog.LogEvent

interface TextLogEventSerializer {
    fun format(
        event: LogEvent,
        appendable: Appendable,
    )
}
