package raytracer.renderer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import raytracer.math.point
import raytracer.math.scaling
import raytracer.math.translation
import raytracer.math.vector

internal class RayTest {

    @Test
    fun createRay() {
        val origin = point(1f, 2f, 3f)
        val direction = vector(4f, 5f, 6f)

        val r = Ray(origin, direction)

        assertEquals(origin, r.origin)
        assertEquals(direction, r.direction)
    }

    @Test
    fun position() {
        val r = Ray(point(2f, 3f, 4f), vector(1f, 0f, 0f))

        assertEquals(point(2f, 3f, 4f), r.position(0f))
        assertEquals(point(3f, 3f, 4f), r.position(1f))
        assertEquals(point(1f, 3f, 4f), r.position(-1f))
        assertEquals(point(4.5f, 3f, 4f), r.position(2.5f))
    }

    @Test
    fun translateRay() {
        val r = Ray(point(1f, 2f, 3f), vector(0f, 1f, 0f))
        val m = translation(3f, 4f, 5f)

        val r2 = r.transform(m)

        assertEquals(point(4f, 6f, 8f), r2.origin)
        assertEquals(vector(0f, 1f, 0f), r2.direction)
    }

    @Test
    fun scaleRay() {
        val r = Ray(point(1f, 2f, 3f), vector(0f, 1f, 0f))
        val m = scaling(2f, 3f, 4f)

        val r2 = r.transform(m)

        assertEquals(point(2f, 6f, 12f), r2.origin)
        assertEquals(vector(0f, 3f, 0f), r2.direction)
    }
}
