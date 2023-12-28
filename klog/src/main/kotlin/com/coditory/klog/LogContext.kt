package com.coditory.klog

import kotlinx.coroutines.ThreadContextElement
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

class LogContext(
    vararg items: Pair<String, String>,
) : ThreadContextElement<Map<String, String>?>, AbstractCoroutineContextElement(Key) {
    companion object Key : CoroutineContext.Key<LogContext>

    private val contextMap: Map<String, String> = LogThreadContext.getCopyOfContextMap() + items

    override fun updateThreadContext(context: CoroutineContext): Map<String, String> {
        val oldState = LogThreadContext.getCopyOfContextMap()
        setCurrent(contextMap)
        return oldState
    }

    override fun restoreThreadContext(
        context: CoroutineContext,
        oldState: Map<String, String>?,
    ) {
        setCurrent(oldState)
    }

    private fun setCurrent(contextMap: Map<String, String>?) {
        if (contextMap.isNullOrEmpty()) {
            LogThreadContext.clear()
        } else {
            LogThreadContext.setContextMap(contextMap)
        }
    }
}
