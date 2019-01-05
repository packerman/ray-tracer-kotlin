package raytracer.renderer

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class GroupTest {

    @Test
    fun creatingNewGroup() {
        val g = Group()

        assertEquals(Matrix4.identity, g.transform)
        assertTrue(g.isEmpty())
    }

    @Test
    fun addingChildToGroup() {
        val g = Group()
        val s = testShape()

        g.addChild(s)

        assertTrue(g.contains(s))
        assertEquals(g, s.parent)
    }

    @Test
    fun intersectingRayWithEmptyGroup() {
        val g = Group()
        val r = Ray(point(0f, 0f, 0f), vector(0f, 0f, 1f))

        val xs = g.intersect(r)

        assertTrue(xs.isEmpty())
    }

    @Test
    fun intersectingRayWithNonEmptyGroup() {
        val g = Group()
        val s1 = Sphere()
        val s2 = Sphere()
        s2.transform = translation(0f, 0f, -3f)
        val s3 = Sphere()
        s3.transform = translation(5f, 0f, 0f)
        g.addChild(s1)
        g.addChild(s2)
        g.addChild(s3)

        val r = Ray(point(0f, 0f, -5f), vector(0f, 0f, 1f))
        val xs = g.intersect(r)

        assertThat(xs, hasSize(4))

        assertEquals(s2, xs[0].shape)
        assertEquals(s2, xs[1].shape)
        assertEquals(s1, xs[2].shape)
        assertEquals(s1, xs[3].shape)
    }

    @Test
    fun groupTransformations() {
        val g = Group()
        g.transform = scaling(2f, 2f, 2f)
        val s = Sphere()
        s.transform = translation(5f, 0f, 0f)
        g.addChild(s)

        val r = Ray(point(10f, 0f, -10f), vector(0f, 0f, 1f))
        val xs = g.intersect(r)

        assertThat(xs, hasSize(2))
    }

    private fun testShape() = object : Shape() {

        override fun localIntersect(ray: Ray): List<Intersection> = emptyList()

        override fun localNormalAt(point: Point, hit: Intersection?): Vector = point - point(0f, 0f, 0f)
    }
}
