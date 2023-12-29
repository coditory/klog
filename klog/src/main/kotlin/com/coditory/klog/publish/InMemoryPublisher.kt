package com.coditory.klog.publish

import com.coditory.klog.LogEvent

class InMemoryPublisher : BlockingPublisher {
    private val events = mutableListOf<LogEvent>()

    override fun publishBlocking(event: LogEvent) {
        events.addLast(event)
    }

    fun getLogs(): List<LogEvent> = events
    fun getLastLog(): LogEvent? = events.lastOrNull()
}
