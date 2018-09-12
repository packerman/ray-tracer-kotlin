package raytracer.renderer

import kotlin.math.tan

class Camera(val hSize: Int, val vSize: Int, val fieldOfView: Float) {
    var transform: Matrix4 = Matrix4.identity

    val pixelSize: Float
    val halfWidth: Float
    val halfHeight: Float

    init {
        val halfView = tan(fieldOfView / 2)
        val aspect = hSize.toFloat() / vSize

        if (aspect >= 1) {
            halfWidth = halfView
            halfHeight = halfView / aspect
        } else {
            halfWidth = halfView * aspect
            halfHeight = halfView
        }

        pixelSize = halfWidth * 2f / hSize
    }
}

fun Camera.rayForPixel(px: Int, py: Int): Ray {
    val xOffset = (px + 0.5f) * pixelSize
    val yOffset = (py + 0.5f) * pixelSize

    val worldX = halfWidth - xOffset
    val worldY = halfHeight - yOffset

    val pixel = transform.inverse * point(worldX, worldY, -1f)
    val origin = transform.inverse * point(0f, 0f, 0f)
    val direction = (pixel - origin).normalize()
    return Ray(origin, direction)
}

fun Camera.render(world: World): Canvas {
    val image = Canvas(hSize, vSize)

    for (y in 0 until vSize) {
        for (x in 0 until hSize) {
            val ray = rayForPixel(x, y)
            val color = world.colorAt(ray)
            image.writePixel(x, y, color)
        }
    }
    return image
}
