package raytracer.renderer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import raytracer.renderer.Parser.Companion.parseObjFile
import java.io.StringReader

class ObjFileTest {

    @Test
    fun ignoreUnrecognizedLines() {
        val file = StringReader("""
            There was a young lady named Bright
            who traveled much faster than light.
            She set out one day
            in a relative way,
            and came back the previous night.
        """.trimIndent())

        val parser = parseObjFile(file)

        assertEquals(5, parser.ignoredLinesCount)
    }

    @Test
    fun vertexRecords() {
        val file = StringReader("""
            v -1 1 0
            v -1.0000 0.5000 0.0000
            v 1 0 0
            v 1 1 0
        """.trimIndent())

        val parser = parseObjFile(file)

        assertEquals(point(-1f, 1f, 0f), parser.getVertex(1))
        assertEquals(point(-1f, 0.5f, 0f), parser.getVertex(2))
        assertEquals(point(1f, 0f, 0f), parser.getVertex(3))
        assertEquals(point(1f, 1f, 0f), parser.getVertex(4))
    }
}
