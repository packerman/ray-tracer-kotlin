package raytracer.renderer

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class CanvasTest {

    @Nested
    internal inner class CanvasTest {

        @Test
        fun createCanvas() {
            val c = Canvas(10, 20)

            Assertions.assertEquals(10, c.width)
            Assertions.assertEquals(20, c.height)

            Assertions.assertEquals(10 * 20, c.pixels().toList().size)

            c.pixels().forEach { pixel ->
                Assertions.assertEquals(color(0f, 0f, 0f), pixel)
            }
        }

        @Test
        fun writePixelToCanvas() {
            val c = Canvas(10, 20)
            val red = color(1f, 0f, 0f)

            c.writePixel(2, 3, red)
            Assertions.assertEquals(red, c.pixelAt(2, 3))
        }

        @Test
        fun ppmHeader() {
            val c = Canvas(5, 3)

            val ppm = c.toPpm()

            val header = listOf("P3",
                    "5 3",
                    "255")
            Assertions.assertEquals(header, ppm.lines().take(3))
        }

        @Test
        fun ppmPixelData() {
            val c = Canvas(5, 3)
            val c1 = color(1.5f, 0f, 0f)
            val c2 = color(0f, 0.5f, 0f)
            val c3 = color(-0.5f, 0f, 1f)

            c.writePixel(0, 0, c1)
            c.writePixel(2, 1, c2)
            c.writePixel(4, 2, c3)

            val ppm = c.toPpm()

            val pixelData = listOf(
                    "255 0 0 0 0 0 0 0 0 0 0 0 0 0 0",
                    "0 0 0 0 0 0 0 128 0 0 0 0 0 0 0",
                    "0 0 0 0 0 0 0 0 0 0 0 0 0 0 255"
            )

            Assertions.assertEquals(pixelData, ppm.lines().subList(3, 6))
        }

        @Test
        fun splitLongLines() {
            val c = Canvas(10, 2)
            for (i in 0 until 10) {
                for (j in 0 until 2) {
                    c.writePixel(i, j, color(1f, 0.8f, 0.6f))
                }
            }

            val ppm = c.toPpm()

            val pixelData = listOf(
                    "255 204 153 255 204 153 255 204 153 255 204 153 255 204 153 255 204",
                    "153 255 204 153 255 204 153 255 204 153 255 204 153",
                    "255 204 153 255 204 153 255 204 153 255 204 153 255 204 153 255 204",
                    "153 255 204 153 255 204 153 255 204 153 255 204 153"
            )

            Assertions.assertEquals(pixelData, ppm.lines().subList(3, 7))
        }

        @Test
        fun ppmFilesAreTerminatedByNewline() {
            val c = Canvas(5, 3)

            val ppm = c.toPpm()

            Assertions.assertTrue(ppm.endsWith(System.lineSeparator()))
        }
    }

    @Nested
    internal inner class LineBreakerTest {

        @Test
        fun testWriteOneLine() {
            val b = StringBuilder()
            LineBreaker(b)
                    .append("abc")
                    .flush()
            Assertions.assertEquals("abc", b.toString())
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

            Assertions.assertEquals(expected, b.toString().lines())
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

            Assertions.assertEquals(expected, b.toString().lines())
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

            Assertions.assertEquals(expected, b.toString().lines())
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

            Assertions.assertEquals(expected, b.toString().lines())
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

            Assertions.assertEquals(expected, b.toString().lines())
        }
    }
}
