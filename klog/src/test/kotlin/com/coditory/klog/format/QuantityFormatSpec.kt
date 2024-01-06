package com.coditory.klog.format

import com.coditory.klog.format.QuantityFormat.formatQuantityBin
import com.coditory.klog.format.QuantityFormat.formatQuantitySI
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.tuple
import io.kotest.matchers.shouldBe

class QuantityFormatSpec : FunSpec({
    listOf(
        tuple(1L, "1"),
        tuple(123L, "123"),
        tuple(999L, "999"),
        tuple(1000L, "1.0 k"),
        tuple(1001L, "1.0 k"),
        tuple(123456L, "123.5 k"),
        tuple(123456L * 1000, "123.5 M"),
        tuple(123456L * 1000 * 1000, "123.5 G"),
        tuple(123456L * 1000 * 1000 * 1000, "123.5 T"),
        tuple(123456L * 1000 * 1000 * 1000 * 1000, "123.5 P"),
        tuple(123456L * 1000 * 1000 * 1000 * 1000 * 10, "1.2 E"),
    ).forEach {
        test("format SI: ${it.a} -> \"${it.b}\"") {
            formatQuantitySI(it.a) shouldBe it.b
        }
    }

    listOf(
        tuple(1L, "1"),
        tuple(123L, "123"),
        tuple(1023L, "1023"),
        tuple(1024L, "1.0 Ki"),
        tuple(1025L, "1.0 Ki"),
        tuple(123456L, "120.6 Ki"),
        tuple(123456L * 1024, "120.6 Mi"),
        tuple(123456L * 1024 * 1024, "120.6 Gi"),
        tuple(123456L * 1024 * 1024 * 1024, "120.6 Ti"),
        tuple(123456L * 1024 * 1024 * 1024 * 1024, "120.6 Pi"),
        tuple(123456L * 1024 * 1024 * 1024 * 1024 * 10, "1.2 Ei"),
    ).forEach {
        test("format bin: ${it.a} -> \"${it.b}\"") {
            formatQuantityBin(it.a) shouldBe it.b
        }
    }
})
