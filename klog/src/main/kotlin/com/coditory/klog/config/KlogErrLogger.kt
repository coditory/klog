package com.coditory.klog.config

fun interface KlogErrLogger {
    fun log(message: () -> String)

    fun logDropped(e: Throwable) {
        log { "Could not publish log. Cause: " + e.stackTraceToString() }
    }

    companion object {
        val STDERR =
            KlogErrLogger { message ->
                System.err.println("[klog] " + message())
            }
    }
}
