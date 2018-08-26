package raytracer.renderer

import raytracer.math.Color
import raytracer.math.color
import raytracer.utils.LineBreaker
import raytracer.utils.clamp
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