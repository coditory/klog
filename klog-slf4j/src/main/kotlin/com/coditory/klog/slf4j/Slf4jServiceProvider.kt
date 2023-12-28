package com.coditory.klog.slf4j

import org.slf4j.ILoggerFactory
import org.slf4j.IMarkerFactory
import org.slf4j.helpers.BasicMarkerFactory
import org.slf4j.spi.MDCAdapter
import org.slf4j.spi.SLF4JServiceProvider

class Slf4jServiceProvider : SLF4JServiceProvider {
    private lateinit var loggerFactory: ILoggerFactory
    private lateinit var markerFactory: IMarkerFactory
    private lateinit var mdcAdapter: MDCAdapter

    override fun getLoggerFactory(): ILoggerFactory = loggerFactory

    override fun getMarkerFactory(): IMarkerFactory = markerFactory

    override fun getMDCAdapter(): MDCAdapter = mdcAdapter

    override fun getRequestedApiVersion(): String = REQUESTED_API_VERSION

    override fun initialize() {
        loggerFactory = Slf4jLoggerFactory()
        markerFactory = BasicMarkerFactory()
        mdcAdapter = Slf4jMdcAdapter()
    }

    companion object {
        const val REQUESTED_API_VERSION: String = "2.0.99"
    }
}
