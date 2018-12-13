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

    val defaultGroup: Group = Group()

    private val _groups = HashMap<String, Group>()

    val groups: Map<String, Group> = _groups

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
                        "f" -> {
                            val vs = splitted.subList(1, splitted.size)
                                    .map { parser.vertices[it.toInt() - 1] }
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

        private fun fanTriangulation(vertices: List<Point>): List<Triangle> =
                (1..vertices.size - 2).map { i ->
                    Triangle(vertices[0], vertices[i], vertices[i + 1])
                }
    }
}
