package com.coditory.klog.text.plain

import com.coditory.klog.shared.formatToString
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.tuple
import io.kotest.matchers.shouldBe

class CompactedPlainTextStringFormatterTest : FunSpec({
    listOf(
        tuple("", "         "),
        tuple("abcdef", "   abcdef"),
        tuple("abcdefghi", "abcdefghi"),
        tuple("abcdefghijkl", "abcdefghi..."),
        tuple("x123.y123.z123.abcdef", " z.abcdef"),
        tuple("x123.y123.z123.abcde", "y.z.abcde"),
        tuple("x123.y123.z123.abcd", " y.z.abcd"),
        tuple("x123.y123.z123.abc", "x.y.z.abc"),
        tuple("x123.y123.z123.abcdefghijkl", "abcdefghi..."),
    ).forEach {
        test("should pad left to 9 chars: \"${it.a}\" -> \"${it.b}\"") {
            val formatted =
                PlainTextStringFormatter.builder()
                    .compactSections()
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
        tuple("x123.y123.z123.abcdef", "z.abcdef "),
        tuple("x123.y123.z123.abcde", "y.z.abcde"),
        tuple("x123.y123.z123.abcd", "y.z.abcd "),
        tuple("x123.y123.z123.abc", "x.y.z.abc"),
        tuple("x123.y123.z123.abcdefghijkl", "abcdefghi..."),
    ).forEach {
        test("should pad right to 9 chars: \"${it.a}\" -> \"${it.b}\"") {
            val formatted =
                PlainTextStringFormatter.builder()
                    .compactSections()
                    .padRight()
                    .maxLength(9)
                    .maxLengthMarker("...")
                    .build()
                    .formatToString(it.a)
            formatted shouldBe it.b
        }
    }
})
