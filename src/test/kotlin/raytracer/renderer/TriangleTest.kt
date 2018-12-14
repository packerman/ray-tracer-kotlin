package raytracer.renderer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import raytracer.utils.assertTupleEquals

internal class TriangleTest {

    @Nested
    internal inner class TriangleTest {

        @Test
        fun constructTriangle() {
            val p1 = point(0f, 1f, 0f)
            val p2 = point(-1f, 0f, 0f)
            val p3 = point(1f, 0f, 0f)
            val t = Triangle(p1, p2, p3)

            assertEquals(p1, t.p1)
            assertEquals(p2, t.p2)
            assertEquals(p3, t.p3)
            assertEquals(vector(-1f, -1f, 0f), t.e1)
            assertEquals(vector(1f, -1f, 0f), t.e2)
            assertTupleEquals(vector(0f, 0f, -1f), t.normal, epsilon)
        }

        @Test
        fun findNormalOnTriangle() {
            val t = Triangle(point(0f, 1f, 0f), point(-1f, 0f, 0f), point(1f, 0f, 0f))

            val n1 = t.normalAt(point(0f, 0.5f, 0f))
            val n2 = t.normalAt(point(-0.5f, 0.75f, 0f))
            val n3 = t.normalAt(point(0.5f, 0.25f, 0f))

            assertTupleEquals(t.normal, n1, epsilon)
            assertTupleEquals(t.normal, n2, epsilon)
            assertTupleEquals(t.normal, n3, epsilon)
        }

        @Test
        fun intersectRayParallelToTheTriangle() {
            val t = Triangle(point(0f, 1f, 0f), point(-1f, 0f, 0f), point(1f, 0f, 0f))
            val r = Ray(point(0f, -1f, -2f), vector(0f, 1f, 0f))

            val xs = t.intersect(r)

            assertTrue(xs.isEmpty())
        }

        @Test
        fun rayMissesP1P3Edge() {
            val t = Triangle(point(0f, 1f, 0f), point(-1f, 0f, 0f), point(1f, 0f, 0f))
            val r = Ray(point(1f, 1f, -2f), vector(0f, 0f, 1f))

            val xs = t.intersect(r)

            assertTrue(xs.isEmpty())
        }

        @Test
        fun rayMissesP1P2Edge() {
            val t = Triangle(point(0f, 1f, 0f), point(-1f, 0f, 0f), point(1f, 0f, 0f))
            val r = Ray(point(-1f, 1f, -2f), vector(0f, 0f, 1f))

            val xs = t.intersect(r)

            assertTrue(xs.isEmpty())
        }

        @Test
        fun rayMissesP2P3Edge() {
            val t = Triangle(point(0f, 1f, 0f), point(-1f, 0f, 0f), point(1f, 0f, 0f))
            val r = Ray(point(0f, -1f, -2f), vector(0f, 0f, 1f))

            val xs = t.intersect(r)

            assertTrue(xs.isEmpty())
        }

        @Test
        fun rayStrikesTriangle() {
            val t = Triangle(point(0f, 1f, 0f), point(-1f, 0f, 0f), point(1f, 0f, 0f))
            val r = Ray(point(0f, 0.5f, -2f), vector(0f, 0f, 1f))

            val xs = t.intersect(r)

            assertEquals(1, xs.size)
            assertEquals(2f, xs[0].t)
        }
    }

    @Nested
    internal inner class SmoothTriangleTest {

        private val p1 = point(0f, 1f, 0f)
        private val p2 = point(-1f, 0f, 0f)
        private val p3 = point(1f, 0f, 0f)
        private val n1 = vector(1f, 0f, 0f)
        private val n2 = vector(-1f, 0f, 0f)
        private val n3 = vector(1f, 0f, 0f)
        private val triangle = SmoothTriangle(p1, p2, p3, n1, n2, n3)

        @Test
        fun createSmoothTriangle() {
            assertEquals(p1, triangle.p1)
            assertEquals(p2, triangle.p2)
            assertEquals(p3, triangle.p3)
            assertEquals(n1, triangle.n1)
            assertEquals(n2, triangle.n2)
            assertEquals(n3, triangle.n3)
        }

        @Test
        fun intersectionWithSmoothTriangle() {
            val r = Ray(point(-0.2f, 0.3f, -2f), vector(0f, 0f, 1f))
            val xs = triangle.intersect(r)
            assertEquals(0.45f, xs[0].u)
            assertEquals(0.25f, xs[0].v)
        }
    }

    private companion object {
        const val epsilon = 0.00001f
    }
}
