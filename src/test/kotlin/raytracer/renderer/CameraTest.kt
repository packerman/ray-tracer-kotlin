package raytracer.renderer

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import raytracer.utils.assertTupleEquals
import kotlin.math.PI
import kotlin.math.sqrt

internal class CameraTest {

    @Test
    fun createCamera() {
        val hSize = 160
        val vSize = 120
        val fieldOfView = halfPi

        val c = Camera(hSize, vSize, fieldOfView)
        assertEquals(160, c.hSize)
        assertEquals(120, c.vSize)
        assertEquals(halfPi, c.fieldOfView)
        assertEquals(Matrix4.identity, c.transform)
    }

    @Test
    fun pixelSizeForHorizontalCanvas() {
        val c = Camera(200, 125, halfPi)

        assertEquals(0.01f, c.pixelSize)
    }

    @Test
    fun pixelSizeForVerticalCanvas() {
        val c = Camera(125, 200, halfPi)

        assertEquals(0.01f, c.pixelSize)
    }

    @Test
    fun rayThroughCenterOfCanvas() {
        val c = Camera(201, 101, halfPi)

        val r = c.rayForPixel(100, 50)

        assertTupleEquals(point(0f, 0f, 0f), r.origin, epsilon)
        assertTupleEquals(vector(0f, 0f, -1f), r.direction, epsilon)
    }

    @Test
    fun rayThroughCornerOfCanvas() {
        val c = Camera(201, 101, halfPi)

        val r = c.rayForPixel(0, 0)

        assertTupleEquals(point(0f, 0f, 0f), r.origin, epsilon)
        assertTupleEquals(vector(0.66519f, 0.33259f, -0.66851f), r.direction, epsilon)
    }

    @Test
    fun rayWhenCameraTransformed() {
        val c = Camera(201, 101, halfPi)
        c.transform = rotationY((Math.PI / 4).toFloat()) * translation(0f, -2f, 5f)

        val r = c.rayForPixel(100, 50)

        assertTupleEquals(point(0f, 2f, -5f), r.origin, epsilon)
        assertTupleEquals(vector(sqrt(2f) / 2, 0f, -sqrt(2f) / 2), r.direction, epsilon)
    }

    @Test
    fun render() {
        val w = defaultWorld()
        val c = Camera(11, 11, (PI / 2).toFloat())

        val from = point(0f, 0f, -5f)
        val to = point(0f, 0f, 0f)
        val up = vector(0f, 1f, 0f)

        c.transform = viewTransform(from, to, up)

        val image = c.render(w)
        assertTupleEquals(color(0.38066f, 0.47583f, 0.2855f), image.pixelAt(5, 5), epsilon)
    }

    @Test
    fun renderTask() {
        val w = defaultWorld()
        val c = Camera(21, 21, (PI / 2).toFloat())

        val from = point(0f, 0f, -5f)
        val to = point(0f, 0f, 0f)
        val up = vector(0f, 1f, 0f)

        c.transform = viewTransform(from, to, up)

        val renderResult = c.render(w, RenderTask(5, 5, 11, 11))
        assertEquals(5, renderResult.xOffset)
        assertEquals(5, renderResult.yOffset)
        assertTupleEquals(color(0.38066f, 0.47583f, 0.2855f), renderResult.canvas.pixelAt(5, 5), epsilon)
    }

    @Test
    fun renderTasks() {
        val c = Camera(20, 10, 1f)
        val renderTasks = c.renderTasks(4).toSet()

        assertThat(renderTasks, hasSize(4))
        assertThat(renderTasks, containsInAnyOrder(
                RenderTask(0, 0, 20, 2),
                RenderTask(0, 2, 20, 2),
                RenderTask(0, 4, 20, 2),
                RenderTask(0, 6, 20, 4)))
    }

    @Test
    fun onlyOneRenderTask() {
        val c = Camera(10, 10, 1f)
        val renderTasks = c.renderTasks(1).toSet()

        assertThat(renderTasks, hasSize(1))
        assertThat(renderTasks, contains(
                RenderTask(0, 0, 10, 10)))
    }

    @Test
    fun numberOfTasksHaveToBePositive() {
        val c = Camera(10, 10, 1f)

        assertThrows(IllegalArgumentException::class.java) { c.renderTasks(0) }
        assertThrows(IllegalArgumentException::class.java) { c.renderTasks(-1) }
    }

    companion object {
        val halfPi = (PI / 2).toFloat()
        const val epsilon = 0.0001f
    }
}
