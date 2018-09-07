package raytracer.renderer

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.coroutines.experimental.buildSequence
import kotlin.math.roundToInt

class Canvas(val width: Int, val height: Int) {

    private val pixels = Array(height) { _ ->
        Array(width) { color(0f, 0f, 0f) }
    }

    fun pixels(): Sequence<Color> = buildSequence {
        for (row in pixels) {
            yieldAll(row.asSequence())
        }
    }

    fun rows(): Sequence<Sequence<Color>> = buildSequence {
        for (row in pixels) {
            yield(row.asSequence())
        }
    }

    fun writePixel(i: Int, j: Int, color: Color) {
        if (i < 0 || i >= width) return
        if (j < 0 || j >= height) return
        pixels[j][i] = color
    }

    fun pixelAt(i: Int, j: Int): Color = pixels[j][i]
}

fun Canvas.toPpm(): String {
    val sb = StringBuilder()
    writeToAppendable(sb)
    return sb.toString()
}

fun Canvas.saveToFile(path: Path) {
    Files.newBufferedWriter(path, Charsets.US_ASCII)
            .use(this::writeToAppendable)
}

fun Canvas.saveToFile(path: String) {
    saveToFile(Paths.get(path))
}

private fun Canvas.writeToAppendable(appendable: Appendable) {
    LineBreaker(appendable, 70).apply {
        appendln("P3")
        appendln("$width $height")
        appendln("255")

        fun formatValue(value: Float) = (value.clamp(0f, 1f) * 255f).roundToInt().toString()

        for (row in rows()) {
            row.forEach { pixel ->
                append(formatValue(pixel.red), " ")
                append(formatValue(pixel.green), " ")
                append(formatValue(pixel.blue), " ")
            }
            newLine()
        }
    }
}

private fun Float.clamp(bottom: Float, top: Float) = when {
    this < bottom -> bottom
    this > top -> top
    else -> this
}

internal class LineBreaker(val appendable: Appendable,
                           val maxLineLength: Int = Int.MAX_VALUE) {

    private var line = StringBuilder()

    fun append(s: String, separator: String = ""): LineBreaker {
        require(s.length <= maxLineLength)
        return if (line.length + separator.length + s.length <= maxLineLength) {
            if (line.isNotEmpty()) line.append(separator)
            line.append(s)
            this
        } else {
            appendable.appendln(line)
            line = StringBuilder(s)
            this
        }
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
