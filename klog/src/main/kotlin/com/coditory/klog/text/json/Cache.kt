package com.coditory.klog.text.json

internal interface Cache<KEY, VALUE> {
    fun put(
        key: KEY,
        value: VALUE,
    )

    fun remove(key: KEY)

    fun size(): Int

    fun get(key: KEY): VALUE?

    fun getOrPut(
        key: KEY,
        provider: (key: KEY) -> VALUE,
    ): VALUE {
        val cached = this.get(key)
        if (cached != null) return cached
        val created = provider.invoke(key)
        this.put(key, created)
        return created
    }

    companion object {
        fun <KEY, VALUE> lruCache(capacity: Int): Cache<KEY, VALUE> {
            return if (capacity <= 0) {
                EmptyCache()
            } else {
                LruCache(capacity)
            }
        }
    }
}

private class EmptyCache<KEY, VALUE> : Cache<KEY, VALUE> {
    override fun put(
        key: KEY,
        value: VALUE,
    ) {}

    override fun remove(key: KEY) {}

    override fun size(): Int = 0

    override fun get(key: KEY): VALUE? = null
}

private class LruCache<KEY, VALUE>(private val capacity: Int) : Cache<KEY, VALUE> {
    private inner class Node(val key: KEY, val value: VALUE) {
        var next: Node? = null
        var prev: Node? = null
    }

    private val cache = HashMap<KEY, Node?>(capacity)
    private var first: Node? = null
    private var last: Node? = null

    init {
        require(capacity > 0) { "Expected capacity > 0" }
    }

    @Synchronized
    override fun put(
        key: KEY,
        value: VALUE,
    ) {
        val cached = cache[key]
        if (cached != null) {
            dequeue(cached)
        }
        add(Node(key, value))
        if (cache.size > capacity) {
            removeLru()
        }
    }

    @Synchronized
    override fun remove(key: KEY) {
        val node = cache[key]
        if (node != null) {
            remove(node)
        }
    }

    @Synchronized
    override fun size(): Int {
        return cache.size
    }

    @Synchronized
    override fun get(key: KEY): VALUE? {
        val node = cache[key] ?: return null
        dequeue(node)
        enqueue(node)
        return node.value
    }

    private fun add(node: Node) {
        cache[node.key] = node
        enqueue(node)
    }

    private fun removeLru() {
        val last = last
        if (last != null) {
            remove(last)
        }
    }

    private fun remove(node: Node) {
        cache.remove(node.key)
        dequeue(node)
    }

    private fun dequeue(node: Node) {
        if (node.prev != null) {
            node.prev!!.next = node.next
        }
        if (node.next != null) {
            node.next!!.prev = node.prev
        }
        if (first === node) {
            first = node.next
        }
        if (last === node) {
            last = node.prev
        }
    }

    private fun enqueue(node: Node) {
        if (first != null) {
            first!!.prev = node
        }
        node.next = first
        node.prev = null
        first = node
        if (last == null) {
            last = first
        }
    }
}
