package raytracer.renderer

import java.io.Reader

class Parser {
    var ignoredLinesCount: Int = 0
        private set

    private val vertices: MutableList<Point> = ArrayList()

    private fun addVertex(point: Point) {
        vertices.add(point)
    }

    fun getVertex(i: Int) = vertices[i - 1]

    companion object {
        fun parseObjFile(reader: Reader): Parser {
            val parser = Parser()
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
                            parser.addVertex(point(f1, f2, f3))
                        }
                        else -> parser.ignoredLinesCount++
                    }
                }


            }
            return parser
        }
    }
}
