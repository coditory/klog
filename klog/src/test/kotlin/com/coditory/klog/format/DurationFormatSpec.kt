package com.coditory.klog.format

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.tuple
import io.kotest.matchers.shouldBe
import java.time.Duration

class DurationFormatSpec : FunSpec({
    listOf(
        tuple(Duration.ofDays(0), "0.00s"),
        tuple(Duration.ofDays(-3), "-3.00d"),
        tuple(Duration.ofDays(3), "3.00d"),
        tuple(Duration.ofHours(36), "1.50d"),
        tuple(Duration.ofHours(24), "1.00d"),
        tuple(Duration.ofHours(16), "16.00h"),
        tuple(Duration.ofMinutes(110), "1.83h"),
        tuple(Duration.ofMinutes(60), "1.00h"),
        tuple(Duration.ofMinutes(35), "35.00m"),
        tuple(Duration.ofSeconds(110), "1.83m"),
        tuple(Duration.ofSeconds(60), "1.00m"),
        tuple(Duration.ofSeconds(35), "35.00s"),
        tuple(Duration.ofMillis(1100), "1.10s"),
        tuple(Duration.ofMillis(1000), "1.00s"),
        tuple(Duration.ofMillis(200), "200ms"),
        tuple(Duration.ofNanos(1100000), "1ms"),
        tuple(Duration.ofNanos(1000000), "1ms"),
        tuple(Duration.ofNanos(1000), "1000ns"),
        tuple(Duration.ofNanos(200), "200ns"),
        tuple(Duration.ofNanos(-200), "-200ns"),
    ).forEach {
        test("format: ${it.a} -> \"${it.b}\"") {
            DurationFormat.format(it.a) shouldBe it.b
        }
    }

    listOf(
        tuple(Duration.ofDays(0), "0s"),
        tuple(Duration.ofDays(-3), "-3d"),
        tuple(Duration.ofDays(3), "3d"),
        tuple(Duration.ofHours(36), "1d"),
        tuple(Duration.ofHours(24), "1d"),
        tuple(Duration.ofHours(16), "16h"),
        tuple(Duration.ofMinutes(110), "1h"),
        tuple(Duration.ofMinutes(60), "1h"),
        tuple(Duration.ofMinutes(35), "35m"),
        tuple(Duration.ofSeconds(110), "1m"),
        tuple(Duration.ofSeconds(60), "1m"),
        tuple(Duration.ofSeconds(35), "35s"),
        tuple(Duration.ofMillis(1100), "1s"),
        tuple(Duration.ofMillis(1000), "1s"),
        tuple(Duration.ofMillis(200), "200ms"),
        tuple(Duration.ofNanos(1100000), "1ms"),
        tuple(Duration.ofNanos(1000000), "1ms"),
        tuple(Duration.ofNanos(1000), "1000ns"),
        tuple(Duration.ofNanos(200), "200ns"),
        tuple(Duration.ofNanos(-200), "-200ns"),
    ).forEach {
        test("format significant: ${it.a} -> \"${it.b}\"") {
            DurationFormat.formatSignificant(it.a) shouldBe it.b
        }
    }
})
