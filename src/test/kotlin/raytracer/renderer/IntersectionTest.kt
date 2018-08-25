package raytracer.renderer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

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
}
