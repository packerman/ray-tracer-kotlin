package raytracer.renderer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import raytracer.math.point
import raytracer.math.vector

internal class IntersectionTest {

    @Test
    fun intersection() {
        val s = Sphere()
        val i = Intersection(3.5f, s)

        assertEquals(3.5f, i.t)
        assertEquals(s, i.obj)
    }

    @Test
    fun intersections() {
        val s = Sphere()

        val i1 = Intersection(1f, s)
        val i2 = Intersection(2f, s)

        val xs = intersections(i1, i2)

        assertEquals(2, xs.size)
        assertEquals(1f, xs[0].t)
        assertEquals(2f, xs[1].t)
    }

    @Test
    fun setObjectOnIntersection() {
        val r = Ray(point(0f, 0f, -5f), vector(0f, 0f, 1f))
        val s = Sphere()
        val xs = s.intersect(r)

        assertEquals(2, xs.size)
        assertEquals(s, xs[0].obj)
        assertEquals(s, xs[1].obj)
    }

    @Test
    fun hitAllIntersectionsHavePositive() {
        val s = Sphere()
        val i1 = Intersection(1f, s)
        val i2 = Intersection(2f, s)

        val xs = intersections(i2, i1)

        val h = xs.hit()
        assertEquals(i1, h)
    }

    @Test
    fun hitSomeIntersectionsHaveNegative() {
        val s = Sphere()
        val i1 = Intersection(-1f, s)
        val i2 = Intersection(1f, s)

        val xs = intersections(i2, i1)

        val h = xs.hit()
        assertEquals(i2, h)
    }

    @Test
    fun hitAllIntersectionsHaveNegative() {
        val s = Sphere()
        val i1 = Intersection(-2f, s)
        val i2 = Intersection(-1f, s)

        val xs = intersections(i2, i1)

        val h = xs.hit()
        assertNull(h)
    }

    @Test
    fun hitIsLowestNonNegative() {
        val s = Sphere()
        val i1 = Intersection(5f, s)
        val i2 = Intersection(7f, s)
        val i3 = Intersection(-3f, s)
        val i4 = Intersection(2f, s)

        val xs = intersections(i1, i2, i3, i4)

        val h = xs.hit()
        assertEquals(i4, h)
    }
}
