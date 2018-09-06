package raytracer.renderer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import raytracer.math.*
import kotlin.math.sqrt

internal class ShapeTest {

    private var savedRay: Ray? = null

    private fun testShape() = object : Shape() {

        override fun localIntersect(ray: Ray): List<Intersection> {
            savedRay = ray
            return emptyList()
        }

        override fun localNormalAt(point: Point): Vector {
            return point - point(0f, 0f, 0f)
        }
    }

    @Test
    fun defaultTransformation() {
        val s = testShape()
        assertEquals(Matrix4.IDENTITY, s.transform)
    }

    @Test
    fun assignTransformation() {
        val s = testShape()
        val t = translation(2f, 3f, 4f)
        s.transform = t
        assertEquals(t, s.transform)
    }

    @Test
    fun defaultMaterial() {
        val s = testShape()
        val m = s.material

        assertEquals(Material(), m)
    }

    @Test
    fun assignMaterial() {
        val s = testShape()
        val m = Material(ambient = 1f)

        s.material = m

        assertEquals(m, s.material)
    }

    @Test
    fun intersectScaledShape() {
        val r = Ray(point(0f, 0f, -5f), vector(0f, 0f, 1f))
        val s = testShape()
        s.transform = scaling(2f, 2f, 2f)

        s.intersect(r)

        assertEquals(point(0f, 0f, -2.5f), savedRay?.origin)
        assertEquals(vector(0f, 0f, 0.5f), savedRay?.direction)
    }

    @Test
    fun intersectTranslatedShape() {
        val r = Ray(point(0f, 0f, -5f), vector(0f, 0f, 1f))
        val s = testShape()
        s.transform = translation(5f, 0f, 0f)

        s.intersect(r)

        assertEquals(point(-5f, 0f, -5f), savedRay?.origin)
        assertEquals(vector(0f, 0f, 1f), savedRay?.direction)
    }

    @Test
    fun normalOnTranslatedShape() {
        val s = testShape()
        s.transform = translation(0f, 5f, 0f)

        val n = s.normalAt(point(1f, 5f, 0f))

        assertTupleEquals(vector(1f, 0f, 0f), n, epsilon)
    }

    @Test
    fun normalOnScaledShape() {
        val s = testShape()
        s.transform = scaling(1f, 0.5f, 1f)

        val n = s.normalAt(point(0f, sqrt(2f) / 2, -sqrt(2f) / 2))

        assertTupleEquals(vector(0f, 0.97014f, -0.24254f), n, epsilon)
    }

    private companion object {
        const val epsilon = 0.00001f
    }
}
