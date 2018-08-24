package raytracer.utils

class LineBreaker(val appendable: Appendable,
                  val maxLineLength: Int = Int.MAX_VALUE) {

    private var line = StringBuilder()

    fun append(s: String, separator: String = ""): LineBreaker {
        require(s.length <= maxLineLength)
        if (line.length + separator.length + s.length <= maxLineLength) {
            if (line.isNotEmpty()) line.append(separator)
            line.append(s)
            return this
        } else {
            appendable.appendln(line)
            line = StringBuilder(s)

        }
        return this
    }

    fun appendln(s: String, separator: String = ""): LineBreaker =
            append(s, separator).newLine()

    fun newLine(): LineBreaker {
        appendable.appendln(line)
        line = StringBuilder()
        return this
    }

    fun flush(): LineBreaker {
        appendable.append(line)
        line = StringBuilder()
        return this
    }
}


