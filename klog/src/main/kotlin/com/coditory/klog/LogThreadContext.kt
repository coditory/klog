package com.coditory.klog

import java.util.ArrayDeque
import java.util.Deque

object LogThreadContext {
    private val threadLocalMap: ThreadLocal<MutableMap<String, String>?> = ThreadLocal()
    private val threadLocalMapOfDeques = ThreadLocalMapOfStacks()

    /**
     * Put a context value (the value parameter) as identified with the
     * `key` parameter into the current thread's context map. Note that
     * contrary to log4j, the value parameter can be null.
     *
     * Keys or values placed in a ThreadLocal should not be of a type/class
     * not included in the JDK. See also https://jira.qos.ch/browse/LOGBACK-450
     *
     * If the current thread does not have a context map it is created as a side
     * effect of this call.
     *
     * Each time a value is added, a new instance of the map is created. This is
     * to be certain that the serialization process will operate on the updated
     * map and not send a reference to the old map, thus not allowing the remote
     * logback component to see the latest changes.
     */
    fun put(
        key: String,
        value: String,
    ) {
        var current = threadLocalMap.get()
        if (current == null) {
            current = HashMap()
            threadLocalMap.set(current)
        }
        current[key] = value
    }

    fun get(key: String): String? {
        return threadLocalMap.get()?.get(key)
    }

    fun remove(key: String) {
        threadLocalMap.get()?.remove(key)
    }

    fun clear() {
        threadLocalMap.set(null)
    }

    fun getCopyOfContextMap(): Map<String, String> {
        return threadLocalMap.get()
            ?.let { HashMap(it) }
            ?: emptyMap()
    }

    fun getKeys(): Set<String> {
        return threadLocalMap.get()?.keys ?: emptySet()
    }

    fun setContextMap(contextMap: Map<String, String>) {
        if (contextMap.isNotEmpty()) {
            threadLocalMap.set(HashMap(contextMap))
        } else {
            threadLocalMap.set(null)
        }
    }

    fun pushByKey(
        key: String,
        value: String,
    ) {
        threadLocalMapOfDeques.pushByKey(key, value)
    }

    fun popByKey(key: String): String? {
        return threadLocalMapOfDeques.popByKey(key)
    }

    fun getCopyOfDequeByKey(key: String): Deque<String>? {
        return threadLocalMapOfDeques.getCopyOfDequeByKey(key)
    }

    fun clearDequeByKey(key: String) {
        threadLocalMapOfDeques.clearDequeByKey(key)
    }
}

private class ThreadLocalMapOfStacks {
    val mapOfStacks: ThreadLocal<MutableMap<String, Deque<String>>> = ThreadLocal()

    fun pushByKey(
        key: String,
        value: String,
    ) {
        var map = mapOfStacks.get()
        if (map == null) {
            map = HashMap()
            mapOfStacks.set(map)
        }
        var deque = map[key]
        if (deque == null) {
            deque = ArrayDeque()
        }
        deque.push(value)
        map[key] = deque
    }

    fun popByKey(key: String): String? {
        val map = mapOfStacks.get() ?: return null
        val deque = map[key] ?: return null
        return deque.pop()
    }

    fun getCopyOfDequeByKey(key: String): Deque<String>? {
        val map = mapOfStacks.get() ?: return null
        val deque = map[key] ?: return null
        return ArrayDeque(deque)
    }

    fun clearDequeByKey(key: String) {
        val map = mapOfStacks.get() ?: return
        val deque = map[key] ?: return
        deque.clear()
    }
}
