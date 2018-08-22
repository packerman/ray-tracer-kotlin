import math.Color
import math.color
import utils.buildStringWithLines
import utils.clamp
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
        pixels[j][i] = color
    }

    fun pixelAt(i: Int, j: Int): Color = pixels[j][i]
}

fun Canvas.toPpm(): String = buildStringWithLines(70) {
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
