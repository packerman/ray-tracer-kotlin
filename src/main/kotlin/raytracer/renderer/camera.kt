package raytracer.renderer

import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.coroutineScope
import kotlinx.coroutines.experimental.runBlocking
import kotlin.coroutines.experimental.buildSequence
import kotlin.math.max
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

suspend fun Camera.render(world: World, parallelism: Int = 1): Canvas = coroutineScope {
    require(world.lights.isNotEmpty())

    val image = Canvas(hSize, vSize)

    renderTasks(parallelism)
            .map { renderTask -> async { render(world, renderTask) } }
            .map { it.await() }
            .forEach { renderResult -> image.copyPixels(renderResult.xOffset, renderResult.yOffset, renderResult.canvas) }

    image
}


fun Camera.render(world: World): Canvas {
    val n = max(Runtime.getRuntime().availableProcessors() - 1, 1)
    return runBlocking(context = Dispatchers.Default) { render(world, n) }
}

internal data class RenderTask(val xOffset: Int, val yOffset: Int, val width: Int, val height: Int)

internal data class RenderResult(val xOffset: Int, val yOffset: Int, val canvas: Canvas)

internal fun Camera.render(world: World, renderTask: RenderTask): RenderResult {
    val image = Canvas(renderTask.width, renderTask.height)

    for (y in 0 until renderTask.height) {
        for (x in 0 until renderTask.width) {
            val ray = rayForPixel(renderTask.xOffset + x, renderTask.yOffset + y)
            val color = world.colorAt(ray)
            image.writePixel(x, y, color)
        }
    }
    return RenderResult(renderTask.xOffset, renderTask.yOffset, image)
}

internal fun Camera.renderTasks(n: Int): List<RenderTask> {
    require(n > 0)
    val height = vSize / n
    return buildSequence {
        for (i in 0..n - 2) {
            yield(RenderTask(0, i * height, hSize, height))
        }
        yield(RenderTask(0, (n - 1) * height, hSize, vSize - (n - 1) * height))
    }.toList()
}
