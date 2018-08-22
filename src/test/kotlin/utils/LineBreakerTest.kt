package utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class LineBreakerTest {

    @Test
    fun testWriteOneLine() {
        val lb = LineBreaker()
        assertEquals("abc", lb.append("abc").toString())
    }

    @Test
    fun testWriteManyLines() {
        val lb = LineBreaker()

        val expected = listOf(
                "abc",
                "def"
        )

        assertEquals(expected, lb
                .append("abc")
                .newLine()
                .append("def")
                .toString().lines())
    }

    @Test
    fun testBreakLine() {
        val lb = LineBreaker(6)

        val expected = listOf(
                "abc",
                " def")

        assertEquals(expected, lb
                .append("abc")
                .append(" def")
                .toString()
                .lines())
    }

    @Test
    fun testSeparatorWhenNoBreakLine() {
        val lb = LineBreaker(10)

        val expected = listOf(
                "abc def")

        assertEquals(expected, lb
                .append("abc")
                .append("def", " ")
                .toString()
                .lines())
    }

    @Test
    fun testNoSeparatorWhenBreakLine() {
        val lb = LineBreaker(5)

        val expected = listOf(
                "abc",
                "def")

        assertEquals(expected, lb
                .append("abc")
                .append("def", " ")
                .toString()
                .lines())
    }

    @Test
    fun testNoSeparatorOnLineBeginning() {
        val lb = LineBreaker(10)

        val expected = listOf(
                "abc",
                "def")

        assertEquals(expected, lb
                .append("abc")
                .newLine()
                .append("def", " ")
                .toString()
                .lines())
    }
}
