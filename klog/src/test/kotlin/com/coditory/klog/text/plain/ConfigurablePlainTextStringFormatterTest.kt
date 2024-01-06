package com.coditory.klog.text.plain

import com.coditory.klog.shared.formatToString
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.tuple
import io.kotest.matchers.shouldBe

class ConfigurablePlainTextStringFormatterTest : FunSpec({
    listOf(
        tuple("", "         "),
        tuple("abcdef", "   abcdef"),
        tuple("abcdefghi", "abcdefghi"),
        tuple("abcdefghijkl", "abcdefghi..."),
    ).forEach {
        test("should pad left to 9 chars: \"${it.a}\" -> \"${it.b}\"") {
            val formatted =
                PlainTextStringFormatter.builder()
                    .padLeft()
                    .maxLength(9)
                    .maxLengthMarker("...")
                    .build()
                    .formatToString(it.a)
            formatted shouldBe it.b
        }
    }

    listOf(
        tuple("", "         "),
        tuple("abcdef", "abcdef   "),
        tuple("abcdefghi", "abcdefghi"),
        tuple("abcdefghijkl", "abcdefghi..."),
    ).forEach {
        test("should pad right to 9 chars: \"${it.a}\" -> \"${it.b}\"") {
            val formatted =
                PlainTextStringFormatter.builder()
                    .padRight()
                    .maxLength(9)
                    .maxLengthMarker("...")
                    .build()
                    .formatToString(it.a)
            formatted shouldBe it.b
        }
    }
})
