package utils

class LineBreaker(val maxLineLength: Int = Int.MAX_VALUE) {

    private val builder = StringBuilder()
    private var line = StringBuilder()

    fun append(s: String, separator: String = ""): LineBreaker {
        require(s.length <= maxLineLength)
        if (line.length + separator.length + s.length <= maxLineLength) {
            if (line.isNotEmpty()) line.append(separator)
            line.append(s)
            return this
        } else {
            builder.appendln(line)
            line = StringBuilder(s)

        }
        return this
    }

    fun appendln(s: String, separator: String = ""): LineBreaker =
            append(s, separator).newLine()

    fun newLine(): LineBreaker {
        builder.appendln(line)
        line = StringBuilder()
        return this
    }

    override fun toString(): String =
            builder.toString() + line.toString()
}

fun buildStringWithLines(maxLineLength: Int, action: LineBreaker.() -> Unit) =
        LineBreaker(maxLineLength).apply(action).toString()
