package com.coditory.klog

import com.coditory.klog.publish.InMemoryTextPublisher
import com.coditory.klog.publish.InMemoryTextPublisher.Companion.TEST_THREAD_NAME
import com.coditory.klog.shared.UpdatableFixedClock
import com.coditory.klog.shared.UpdatableFixedClock.Companion.DEFAULT_FIXED_TIME_LOG_STR
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class KlogComplexPlainTextSpec : FunSpec({
    val clock = UpdatableFixedClock()
    val publisher = InMemoryTextPublisher.testPublisher()
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
        publisher.getLastLog() shouldBe "$DEFAULT_FIXED_TIME_LOG_STR INFO $TEST_THREAD_NAME com.coditory.Logger: Hello"
    }
})
