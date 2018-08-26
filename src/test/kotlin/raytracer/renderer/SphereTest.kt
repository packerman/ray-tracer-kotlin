package raytracer.renderer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import raytracer.math.*
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
    fun sphereDefaultTransformation() {
        val s = Sphere()
        assertEquals(Matrix4.IDENTITY, s.transform)
    }

    @Test
    fun changeSphereTransformation() {
        val s = Sphere()
        val t = translation(2f, 3f, 4f)
        s.transform = t
        assertEquals(t, s.transform)
    }

    @Test
    fun intersectScaledSphere() {
        val r = Ray(point(0f, 0f, -5f), vector(0f, 0f, 1f))
        val s = Sphere()
        s.transform = scaling(2f, 2f, 2f)

        val xs = s.intersect(r)

        assertEquals(2, xs.size)
        assertEquals(3f, xs[0].t)
        assertEquals(7f, xs[1].t)
    }

    @Test
    fun intersectTranslatedSphere() {
        val r = Ray(point(0f, 0f, -5f), vector(0f, 0f, 1f))
        val s = Sphere()
        s.transform = translation(5f, 0f, 0f)

        val xs = s.intersect(r)

        assertEquals(0, xs.size)
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

    @Test
    fun normalOnTranslatedSphere() {
        val s = Sphere()
        s.transform = translation(0f, 5f, 0f)

        val n = s.normalAt(point(1f, 5f, 0f))

        assertTupleEquals(vector(1f, 0f, 0f), n, epsilon)
    }

    @Test
    fun normalOnScaledSphere() {
        val s = Sphere()
        s.transform = scaling(1f, 0.5f, 1f)

        val n = s.normalAt(point(0f, sqrt(2f) / 2, -sqrt(2f) / 2))

        assertTupleEquals(vector(0f, 0.97014f, -0.24254f), n, epsilon)
    }

    @Test
    fun sphereHasDefaultMaterial() {
        val s = Sphere()
        val m = s.material

        assertEquals(Material(), m)
    }

    @Test
    fun sphereMayBeAssignedMaterial() {
        val s = Sphere()
        val m = Material(ambient = 1f)

        s.material = m

        assertEquals(m, s.material)
    }

    companion object {
        const val epsilon = 0.00001f
    }
}
