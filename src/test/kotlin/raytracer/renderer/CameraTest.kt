package raytracer.renderer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import raytracer.math.*
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

    companion object {
        const val halfPi = (PI / 2).toFloat()
        const val epsilon = 0.00001f
    }

}
