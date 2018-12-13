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

        assertEquals(point(-1f, 1f, 0f), parser.vertices[0])
        assertEquals(point(-1f, 0.5f, 0f), parser.vertices[1])
        assertEquals(point(1f, 0f, 0f), parser.vertices[2])
        assertEquals(point(1f, 1f, 0f), parser.vertices[3])
    }

    @Test
    fun parseTriangleFaces() {
        val file = StringReader("""
            v -1 1 0
            v -1 0 0
            v 1 0 0
            v 1 1 0
            f 1 2 3
            f 1 3 4
        """.trimIndent())

        val parser = parseObjFile(file)
        val g = parser.defaultGroup
        val t1 = g.children[0] as Triangle
        val t2 = g.children[1] as Triangle

        assertEquals(parser.vertices[0], t1.p1)
        assertEquals(parser.vertices[1], t1.p2)
        assertEquals(parser.vertices[2], t1.p3)
        assertEquals(parser.vertices[0], t2.p1)
        assertEquals(parser.vertices[2], t2.p2)
        assertEquals(parser.vertices[3], t2.p3)
    }

    @Test
    fun triangulatePolygons() {
        val file = StringReader("""
            v -1 1 0
            v -1 0 0
            v 1 0 0
            v 1 1 0
            v 0 2 0
            f 1 2 3 4 5
        """.trimIndent())

        val parser = parseObjFile(file)
        val g = parser.defaultGroup
        val t1 = g.children[0] as Triangle
        val t2 = g.children[1] as Triangle
        val t3 = g.children[2] as Triangle

        assertEquals(parser.vertices[0], t1.p1)
        assertEquals(parser.vertices[1], t1.p2)
        assertEquals(parser.vertices[2], t1.p3)
        assertEquals(parser.vertices[0], t2.p1)
        assertEquals(parser.vertices[2], t2.p2)
        assertEquals(parser.vertices[3], t2.p3)
        assertEquals(parser.vertices[0], t3.p1)
        assertEquals(parser.vertices[3], t3.p2)
        assertEquals(parser.vertices[4], t3.p3)
    }

    @Test
    fun trianglesInGroups() {
        val file = StringReader("""
            v -1 1 0
            v -1 0 0
            v 1 0 0
            v 1 1 0

            g FirstGroup
            f 1 2 3
            g SecondGroup
            f 1 3 4
        """.trimIndent())

        val parser = parseObjFile(file)
        val g1 = parser.groups.getValue("FirstGroup")
        val g2 = parser.groups.getValue("SecondGroup")
        val t1 = g1.children[0] as Triangle
        val t2 = g2.children[0] as Triangle

        assertEquals(parser.vertices[0], t1.p1)
        assertEquals(parser.vertices[1], t1.p2)
        assertEquals(parser.vertices[2], t1.p3)
        assertEquals(parser.vertices[0], t2.p1)
        assertEquals(parser.vertices[2], t2.p2)
        assertEquals(parser.vertices[3], t2.p3)
    }

    @Test
    fun convertObjFileToGroup() {
        val file = StringReader("""
            v -1 1 0
            v -1 0 0
            v 1 0 0
            v 1 1 0

            g FirstGroup
            f 1 2 3
            g SecondGroup
            f 1 3 4
        """.trimIndent())

        val parser = parseObjFile(file)

        val g = parser.toGroup()

        val g1 = g.children[0] as Group
        val g2 = g.children[1] as Group

        val actualTriangles = setOf(g1.children[0] as Triangle, g2.children[0] as Triangle)

        val expectedTriangles = setOf(
                Triangle(point(-1f, 1f, 0f), point(-1f, 0f, 0f), point(1f, 0f, 0f)),
                Triangle(point(-1f, 1f, 0f), point(1f, 0f, 0f), point(1f, 1f, 0f))
        )

        assertEquals(expectedTriangles, actualTriangles)
    }
}
