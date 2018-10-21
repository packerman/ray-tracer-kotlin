package raytracer.renderer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class PlaneTest {

    @Test
    fun normalPlaneIsConstantEverywhere() {
        val p = Plane()
        val n1 = p.normalAt(point(0f, 0f, 0f))
        val n2 = p.normalAt(point(10f, 0f, -10f))
        val n3 = p.normalAt(point(-5f, 0f, 150f))

        assertEquals(vector(0f, 1f, 0f), n1)
        assertEquals(vector(0f, 1f, 0f), n2)
        assertEquals(vector(0f, 1f, 0f), n3)
    }

    @Test
    fun intersectWithRayParallelToPlane() {
        val p = Plane()
        val r = Ray(point(0f, 10f, 0f), vector(0f, 0f, 1f))

        val xs = p.intersect(r)

        assertTrue(xs.isEmpty())
    }

    @Test
    fun intersectWithCoplanarRay() {
        val p = Plane()
        val r = Ray(point(0f, 0f, 0f), vector(0f, 0f, 1f))

        val xs = p.intersect(r)

        assertTrue(xs.isEmpty())
    }

    @Test
    fun rayIntersectingPlaneFromAbove() {
        val p = Plane()
        val r = Ray(point(0f, 1f, 0f), vector(0f, -1f, 0f))

        val xs = p.intersect(r)

        assertEquals(1, xs.size)
        assertEquals(1f, xs[0].t)
        assertEquals(p, xs[0].shape)
    }

    @Test
    fun rayIntersectingPlaneFromBelow() {
        val p = Plane()
        val r = Ray(point(0f, -1f, 0f), vector(0f, 1f, 0f))

        val xs = p.intersect(r)

        assertEquals(1, xs.size)
        assertEquals(1f, xs[0].t)
        assertEquals(p, xs[0].shape)
    }

    @Test
    internal fun boundsForPlane() {
        val s = Plane()

        val expectedBounds = Bounds(
                point(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY),
                point(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
        )
        assertEquals(expectedBounds, s.bounds())
    }
}
