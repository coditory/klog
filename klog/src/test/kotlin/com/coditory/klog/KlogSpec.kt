package com.coditory.klog

import com.coditory.klog.publish.InMemoryPublisher
import com.coditory.klog.shared.UpdatableFixedClock
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class KlogSpec : FunSpec({
    val clock = UpdatableFixedClock()
    val publisher = InMemoryPublisher()
    val klog =
        klog {
            clock(clock)
            stream {
                blockingPublisher(publisher)
            }
        }

    val logger = klog.logger("com.coditory.Logger")

    beforeTest {
        publisher.clear()
    }

    test("should emit a basic info log event") {
        logger.info { "Hello" }
        publisher.getLogs().size shouldBe 1
        publisher.getLastLog() shouldBe
            LogEvent(
                priority = LogPriority.STANDARD,
                timestamp = clock.zonedDateTime(),
                logger = "com.coditory.Logger",
                level = Level.INFO,
                thread = Thread.currentThread().name,
                message = "Hello",
                throwable = null,
                context = emptyMap(),
                items = emptyMap(),
            )
    }

    test("should emit a basic error log event") {
        val e = IllegalArgumentException("SampleException")
        logger.error(e) { "Something went wrong" }
        publisher.getLogs().size shouldBe 1
        publisher.getLastLog() shouldBe
            LogEvent(
                priority = LogPriority.STANDARD,
                timestamp = clock.zonedDateTime(),
                logger = "com.coditory.Logger",
                level = Level.ERROR,
                thread = Thread.currentThread().name,
                message = "Something went wrong",
                throwable = e,
                context = emptyMap(),
                items = emptyMap(),
            )
    }
})
