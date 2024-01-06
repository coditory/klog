package com.coditory.klog.format

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.tuple
import io.kotest.matchers.shouldBe

class BytesFormatSpec : FunSpec({
    listOf(
        tuple(1L, "1 B"),
        tuple(123L, "123 B"),
        tuple(999L, "999 B"),
        tuple(1000L, "1.0 kB"),
        tuple(1001L, "1.0 kB"),
        tuple(123456L, "123.5 kB"),
        tuple(123456L * 1000, "123.5 MB"),
        tuple(123456L * 1000 * 1000, "123.5 GB"),
        tuple(123456L * 1000 * 1000 * 1000, "123.5 TB"),
        tuple(123456L * 1000 * 1000 * 1000 * 1000, "123.5 PB"),
        tuple(123456L * 1000 * 1000 * 1000 * 1000 * 10, "1.2 EB"),
    ).forEach {
        test("format SI: ${it.a} -> \"${it.b}\"") {
            BytesFormat.formatBytesSI(it.a) shouldBe it.b
        }
    }

    listOf(
        tuple(1L, "1 B"),
        tuple(123L, "123 B"),
        tuple(1023L, "1023 B"),
        tuple(1024L, "1.0 KiB"),
        tuple(1025L, "1.0 KiB"),
        tuple(123456L, "120.6 KiB"),
        tuple(123456L * 1024, "120.6 MiB"),
        tuple(123456L * 1024 * 1024, "120.6 GiB"),
        tuple(123456L * 1024 * 1024 * 1024, "120.6 TiB"),
        tuple(123456L * 1024 * 1024 * 1024 * 1024, "120.6 PiB"),
        tuple(123456L * 1024 * 1024 * 1024 * 1024 * 10, "1.2 EiB"),
    ).forEach {
        test("format bin: ${it.a} -> \"${it.b}\"") {
            BytesFormat.formatBytesBin(it.a) shouldBe it.b
        }
    }
})
