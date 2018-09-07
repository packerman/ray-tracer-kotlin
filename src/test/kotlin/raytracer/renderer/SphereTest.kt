package raytracer.renderer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import raytracer.utils.assertTupleEquals
import kotlin.math.sqrt

internal class SphereTest {

    @Test
    fun rayIntersectsSphereAtTwoPoint() {
        val r = Ray(point(0f, 0f, -5f), vector(0f, 0f, 1f))
        val s = Sphere()

        val xs = s.intersect(r)

        assertEquals(2, xs.size)
        assertEquals(4f, xs[0].t)
        assertEquals(6f, xs[1].t)
    }

    @Test
    fun rayIntersectsSphereAtTangent() {
        val r = Ray(point(0f, 1f, -5f), vector(0f, 0f, 1f))
        val s = Sphere()

        val xs = s.intersect(r)

        assertEquals(2, xs.size)
        assertEquals(5f, xs[0].t)
        assertEquals(5f, xs[1].t)
    }

    @Test
    fun rayMissesSphere() {
        val r = Ray(point(0f, 2f, -5f), vector(0f, 0f, 1f))
        val s = Sphere()

        val xs = s.intersect(r)

        assertEquals(0, xs.size)
    }

    @Test
    fun rayOriginatesInSphere() {
        val r = Ray(point(0f, 0f, 0f), vector(0f, 0f, 1f))
        val s = Sphere()

        val xs = s.intersect(r)

        assertEquals(2, xs.size)
        assertEquals(-1f, xs[0].t)
        assertEquals(1f, xs[1].t)
    }

    @Test
    fun sphereBehindRay() {
        val r = Ray(point(0f, 0f, 5f), vector(0f, 0f, 1f))
        val s = Sphere()

        val xs = s.intersect(r)

        assertEquals(2, xs.size)
        assertEquals(-6f, xs[0].t)
        assertEquals(-4f, xs[1].t)
    }

    @Test
    fun normalOnSphereAtPointOnXAxis() {
        val s = Sphere()
        val n = s.normalAt(point(1f, 0f, 0f))

        assertEquals(vector(1f, 0f, 0f), n)
    }

    @Test
    fun normalOnSphereAtPointOnYAxis() {
        val s = Sphere()
        val n = s.normalAt(point(0f, 1f, 0f))

        assertEquals(vector(0f, 1f, 0f), n)
    }

    @Test
    fun normalOnSphereAtPointOnZAxis() {
        val s = Sphere()
        val n = s.normalAt(point(0f, 0f, 1f))

        assertEquals(vector(0f, 0f, 1f), n)
    }

    @Test
    fun normalOnSphereAtPointOnNonAxialPoint() {
        val s = Sphere()
        val n = s.normalAt(point(sqrt(3f) / 3, sqrt(3f) / 3, sqrt(3f) / 3))

        assertTupleEquals(vector(sqrt(3f) / 3f, sqrt(3f) / 3, sqrt(3f) / 3), n, epsilon)
    }

    @Test
    fun normalIsNormalizedVector() {
        val s = Sphere()
        val n = s.normalAt(point(sqrt(3f) / 3, sqrt(3f) / 3, sqrt(3f) / 3))

        assertTupleEquals(n, n.normalize(), epsilon)
    }

    private companion object {
        const val epsilon = 0.00001f
    }
}
