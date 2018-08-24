package raytracer.examples

import raytracer.math.*
import raytracer.renderer.Canvas
import raytracer.renderer.saveToFile
import kotlin.math.PI

private val twelve = point(0f, 0f, 1f)

private fun hourRotation(i: Int) = rotationY((i * PI / 6).toFloat())

fun main(args: Array<String>) {
    val white = color(1f, 1f, 1f)

    val width = 600
    val height = 600

    val canvas = Canvas(600, 600)

    val s = scaling(3f * width / 8, 0f, 3f * height / 8)
    val t = translation(width / 2f, 0f, height / 2f)

    (1..12).asSequence()
            .map { i -> hourRotation(i) * twelve }
            .map { p -> s * p }
            .map { p -> t * p }
            .forEach { p -> canvas.writePixel(p.x.toInt(), p.z.toInt(), white) }

    canvas.saveToFile("clock.ppm")
}
