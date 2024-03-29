package raytracer.examples

import raytracer.renderer.*
import raytracer.renderer.color


fun main(args: Array<String>) {
    val rayOrigin = point(0f, 0f, -5f)
    val wallZ = 10f
    val wallSize = 7f
    val canvasPixels = 100

    val pixelSize = wallSize / canvasPixels
    val half = wallSize / 2

    val canvas = Canvas(canvasPixels, canvasPixels)

    val s = Sphere()
    s.material = Material(color = color(1f, 0.2f, 1f))

    val light = PointLight(point(-10f, 10f, -10f), color(1f, 1f, 1f))

    for (y in 0 until canvasPixels) {
        val worldY = half - pixelSize * y
        for (x in 0 until canvasPixels) {
            val worldX = -half + pixelSize * x
            val position = point(worldX, worldY, wallZ)
            val ray = Ray(rayOrigin, (position - rayOrigin).normalize())
            val xs = s.intersect(ray)
            xs.hit()?.let { hit ->
                val point = ray.position(hit.t)
                val normal = hit.shape.normalAt(point)
                val eye = -ray.direction
                val color = hit.shape.material.lighting(hit.shape, light, point, eye, normal)
                canvas.writePixel(x, y, color)
            }
        }
    }

    canvas.saveToFile("sphereLighting.ppm")
}
