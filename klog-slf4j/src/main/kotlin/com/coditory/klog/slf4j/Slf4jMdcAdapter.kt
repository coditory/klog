package com.coditory.klog.slf4j

import com.coditory.klog.LogThreadContext
import org.slf4j.spi.MDCAdapter
import java.util.Deque

class Slf4jMdcAdapter : MDCAdapter {
    override fun put(
        key: String?,
        value: String?,
    ) {
        if (key == null || value == null) return
        LogThreadContext.put(key, value)
    }

    override fun get(key: String?): String? {
        if (key == null) return null
        return LogThreadContext.get(key)
    }

    override fun remove(key: String?) {
        if (key == null) return
        LogThreadContext.remove(key)
    }

    override fun clear() {
        LogThreadContext.clear()
    }

    override fun getCopyOfContextMap(): MutableMap<String, String> {
        return LogThreadContext.getCopyOfContextMap().toMutableMap()
    }

    override fun setContextMap(contextMap: MutableMap<String, String>?) {
        if (contextMap != null) {
            LogThreadContext.setContextMap(contextMap)
        } else {
            LogThreadContext.clear()
        }
    }

    override fun pushByKey(
        key: String?,
        value: String?,
    ) {
        if (key == null || value == null) return
        LogThreadContext.pushByKey(key, value)
    }

    override fun popByKey(key: String?): String? {
        if (key == null) return null
        return LogThreadContext.popByKey(key)
    }

    override fun getCopyOfDequeByKey(key: String?): Deque<String>? {
        if (key == null) return null
        return LogThreadContext.getCopyOfDequeByKey(key)
    }

    override fun clearDequeByKey(key: String?) {
        if (key == null) return
        LogThreadContext.clearDequeByKey(key)
    }
}
