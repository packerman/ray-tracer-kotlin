package utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class LineBreakerTest {

    @Test
    fun testWriteOneLine() {
        val b = StringBuilder()
        LineBreaker(b)
                .append("abc")
                .flush()
        assertEquals("abc", b.toString())
    }

    @Test
    fun testWriteManyLines() {
        val b = StringBuilder()
        LineBreaker(b)
                .append("abc")
                .newLine()
                .append("def")
                .flush()

        val expected = listOf(
                "abc",
                "def"
        )

        assertEquals(expected, b.toString().lines())
    }

    @Test
    fun testBreakLine() {
        val b = StringBuilder()
        LineBreaker(b, 6)
                .append("abc")
                .append(" def")
                .flush()

        val expected = listOf(
                "abc",
                " def")

        assertEquals(expected, b.toString().lines())
    }

    @Test
    fun testSeparatorWhenNoBreakLine() {
        val b = StringBuilder()
        LineBreaker(b, 10)
                .append("abc")
                .append("def", " ")
                .flush()

        val expected = listOf(
                "abc def")

        assertEquals(expected, b.toString().lines())
    }

    @Test
    fun testNoSeparatorWhenBreakLine() {
        val b = StringBuilder()
        LineBreaker(b, 5)
                .append("abc")
                .append("def", " ")
                .flush()

        val expected = listOf(
                "abc",
                "def")

        assertEquals(expected, b.toString().lines())
    }

    @Test
    fun testNoSeparatorOnLineBeginning() {
        val b = StringBuilder()
        LineBreaker(b, 10)
                .append("abc")
                .newLine()
                .append("def", " ")
                .flush()

        val expected = listOf(
                "abc",
                "def")

        assertEquals(expected, b.toString().lines())
    }
}
