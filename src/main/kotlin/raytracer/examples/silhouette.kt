package raytracer.examples

import raytracer.renderer.*


fun main(args: Array<String>) {
    val rayOrigin = point(0f, 0f, -5f)
    val wallZ = 10f
    val wallSize = 7f
    val canvasPixels = 100

    val pixelSize = wallSize / canvasPixels
    val half = wallSize / 2

    val canvas = Canvas(canvasPixels, canvasPixels)

    val color = color(1f, 0f, 0f)

    val s = Sphere()

    for (y in 0 until canvasPixels) {
        val worldY = half - pixelSize * y
        for (x in 0 until canvasPixels) {
            val worldX = -half + pixelSize * x
            val position = point(worldX, worldY, wallZ)
            val r = Ray(rayOrigin, (position - rayOrigin).normalize())
            val xs = s.intersect(r)
            if (xs.hit() != null) {
                canvas.writePixel(x, y, color)
            }
        }
    }

    canvas.saveToFile("silhouette.ppm")
}
