package com.coditory.klog

import com.coditory.klog.publish.InMemoryTextPublisher
import com.coditory.klog.publish.InMemoryTextPublisher.Companion.TEST_THREAD_NAME
import com.coditory.klog.shared.InMemoryLogListener
import com.coditory.klog.shared.UpdatableFixedClock
import com.coditory.klog.shared.UpdatableFixedClock.Companion.DEFAULT_FIXED_TIME_STR
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

class KlogBatchingPublisherSpec : FunSpec({
    val listener = InMemoryLogListener()
    val publisher = InMemoryTextPublisher.testPublisher()
    val maxBatchStaleness = 10.milliseconds
    val publishDelay = maxBatchStaleness + 1.milliseconds

    val newLogger: () -> KlogLogger = {
        val klog = klog {
            clock(UpdatableFixedClock())
            listener(listener)
            stream {
                asyncPublisher(publisher) {
                    batchSize(3)
                    maxBatchStaleness(maxBatchStaleness)
                }
            }
        }
        klog.logger("com.coditory.Logger")
    }

    beforeTest {
        listener.clear()
        publisher.clear()
    }

    test("should emit a log event with delay") {
        val logger = newLogger()
        logger.info { "Hello" }
        publisher.getLogs().size shouldBe 0
        delay(publishDelay)
        publisher.getLogs().size shouldBe 1
        publisher.getLastLog() shouldBe "$DEFAULT_FIXED_TIME_STR INFO $TEST_THREAD_NAME com.coditory.Logger: Hello"
    }

    test("should call log listener") {
        val logger = newLogger()
        logger.info { "Hello" }
        publisher.getLogs().size shouldBe 0
        delay(publishDelay)
        publisher.getLogs().size shouldBe 1
        listener.received() shouldBe listOf(
            "LogStart: Hello",
            "StreamStart: Hello",
            "Received: Hello",
            "StreamEnd: Hello",
            "LogEnd: Hello",
            "Published: [Hello]",
        )
    }

    test("should emit full batch with no delay") {
        val logger = newLogger()
        logger.info { "Hello 1" }
        logger.info { "Hello 2" }
        logger.info { "Hello 3" }
        logger.info { "Hello 4" }
        delay(1.milliseconds)
        publisher.getLogs().size shouldBe 3
        publisher.getLastLog() shouldBe "$DEFAULT_FIXED_TIME_STR INFO $TEST_THREAD_NAME com.coditory.Logger: Hello 3"
        delay(publishDelay)
        publisher.getLogs().size shouldBe 4
        publisher.getLastLog() shouldBe "$DEFAULT_FIXED_TIME_STR INFO $TEST_THREAD_NAME com.coditory.Logger: Hello 4"
    }
})
