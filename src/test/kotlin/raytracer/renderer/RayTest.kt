package raytracer.renderer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import raytracer.math.*

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
}
