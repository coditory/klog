package com.coditory.klog.slf4j

import com.coditory.klog.Klog
import org.slf4j.ILoggerFactory
import org.slf4j.Logger

class Slf4jLoggerFactory : ILoggerFactory {
    override fun getLogger(name: String?): Logger = Slf4jLoggerWrapper(Klog.logger(name!!))
}
