package com.coditory.klog.jpl

import com.coditory.klog.Klog
import java.util.concurrent.ConcurrentHashMap

class SystemLoggerFinder : System.LoggerFinder() {
    private val cache = ConcurrentHashMap<String, SystemLogger>()

    override fun getLogger(name: String, module: Module?): System.Logger {
        return cache.computeIfAbsent(name) {
            SystemLogger(Klog.logger(name))
        }
    }
}
