package com.coditory.klog.shared

import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class UpdatableFixedClock(
    private var fixedTime: Instant = DEFAULT_FIXED_TIME,
    private val zoneId: ZoneId = DEFAULT_ZONE_ID,
) : Clock() {
    override fun getZone(): ZoneId = zoneId

    override fun withZone(zoneId: ZoneId): UpdatableFixedClock = UpdatableFixedClock(fixedTime, zoneId)

    override fun instant(): Instant = fixedTime

    fun zonedDateTime(): ZonedDateTime = ZonedDateTime.ofInstant(fixedTime, zoneId)

    fun reset() {
        this.fixedTime = DEFAULT_FIXED_TIME
    }

    fun tick(duration: Duration = Duration.ofSeconds(1)) {
        this.fixedTime = this.fixedTime.plus(duration)
    }

    fun setup(instant: Instant) {
        this.fixedTime = instant
    }

    companion object {
        // Always use instant with nanos for testing. Some databases (like mongo) trim nanos - you should test for that!
        val DEFAULT_FIXED_TIME = Instant.parse("2015-12-03T10:15:30.123456Z")
        val DEFAULT_ZONE_ID = ZoneId.of("Europe/Warsaw")
    }
}
