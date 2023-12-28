package com.coditory.klog.config

fun interface KlogErrLogger {
    fun log(message: () -> String)

    companion object {
        val STDERR =
            KlogErrLogger { message ->
                System.err.println("[klog] " + message())
            }
    }
}
