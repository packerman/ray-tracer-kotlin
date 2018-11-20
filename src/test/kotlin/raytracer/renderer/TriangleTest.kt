package raytracer.renderer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import raytracer.utils.assertTupleEquals

internal class TriangleTest {

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
    internal fun findNormalOnTriangle() {
        val t = Triangle(point(0f, 1f, 0f), point(-1f, 0f, 0f), point(1f, 0f, 0f))

        val n1 = t.normalAt(point(0f, 0.5f, 0f))
        val n2 = t.normalAt(point(-0.5f, 0.75f, 0f))
        val n3 = t.normalAt(point(0.5f, 0.25f, 0f))

        assertEquals(t.normal, n1)
        assertEquals(t.normal, n2)
        assertEquals(t.normal, n3)
    }

    private companion object {
        const val epsilon = 0.00001f
    }
}
