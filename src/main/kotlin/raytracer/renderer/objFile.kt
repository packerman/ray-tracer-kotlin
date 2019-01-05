package raytracer.renderer

import java.io.Reader

class Parser {
    fun toGroup(): Group {
        val g = Group()
        if (defaultGroup.isNotEmpty()) {
            g.addChild(defaultGroup)
        }
        _groups.values.forEach(g::addChild)
        return g
    }

    var ignoredLinesCount: Int = 0
        private set

    private val _vertices: MutableList<Point> = ArrayList()

    val vertices: List<Point> = _vertices

    private val _normals: MutableList<Vector> = ArrayList()

    val normals: List<Vector> = _normals

    val defaultGroup: Group = Group()

    private val _groups = HashMap<String, Group>()

    val groups: Map<String, Group> = _groups

    private fun parseFace(string: String): Face {
        val splitted = string.split("/")
        return when (splitted.size) {
            1 -> Face(vertices[splitted[0].toInt() - 1])
            3 -> Face(vertices[splitted[0].toInt() - 1], normals[splitted[2].toInt() - 1])
            else -> error("Unsupported number of face components: ${splitted.size}")
        }
    }

    companion object {
        fun parseObjFile(reader: Reader): Parser {
            val parser = Parser()
            var currentGroup = parser.defaultGroup
            reader.forEachLine { line ->
                val splitted = line.split(" ")
                if (splitted.isEmpty()) {
                    parser.ignoredLinesCount++
                } else {
                    when (splitted[0]) {
                        "v" -> {
                            val f1 = splitted[1].toFloat()
                            val f2 = splitted[2].toFloat()
                            val f3 = splitted[3].toFloat()
                            parser._vertices.add(point(f1, f2, f3))
                        }
                        "vn" -> {
                            val f1 = splitted[1].toFloat()
                            val f2 = splitted[2].toFloat()
                            val f3 = splitted[3].toFloat()
                            parser._normals.add(vector(f1, f2, f3))
                        }
                        "f" -> {
                            val vs = splitted.subList(1, splitted.size)
                                    .map { parser.parseFace(it) }
                            fanTriangulation(vs).forEach(currentGroup::addChild)
                        }
                        "g" -> {
                            currentGroup = parser._groups.getOrPut(splitted[1]) { Group() }
                        }
                        else -> parser.ignoredLinesCount++
                    }
                }
            }
            return parser
        }

        private fun fanTriangulation(faces: List<Face>): List<Shape> =
                (1..faces.size - 2).map { i ->
                    val f1 = faces[0]
                    val f2 = faces[i]
                    val f3 = faces[i + 1]
                    if (f1.normal != null && f2.normal != null && f3.normal != null) {
                        SmoothTriangle(f1.vertex, f2.vertex, f3.vertex, f1.normal, f2.normal, f3.normal)
                    } else {
                        Triangle(f1.vertex, f2.vertex, f3.vertex)
                    }
                }
    }
}

data class Face(val vertex: Point, val normal: Vector? = null)
