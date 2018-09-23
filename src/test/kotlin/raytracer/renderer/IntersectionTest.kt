package raytracer.renderer

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import raytracer.utils.assertTupleEquals
import java.util.stream.Stream
import kotlin.math.sqrt

internal class IntersectionTest {

    @Test
    fun intersection() {
        val s = Sphere()
        val i = Intersection(3.5f, s)

        assertEquals(3.5f, i.t)
        assertEquals(s, i.shape)
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
        assertEquals(s, xs[0].shape)
        assertEquals(s, xs[1].shape)
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

    @Test
    fun prepareHit() {
        val ray = Ray(point(0f, 0f, -5f), vector(0f, 0f, 1f))
        val shape = Sphere()

        val intersection = Intersection(4f, shape)

        val hit = intersection.prepareHit(ray)

        assertTupleEquals(point(0f, 0f, -1f), hit.point, epsilon)
        assertTupleEquals(vector(0f, 0f, -1f), hit.eye, epsilon)
        assertEquals(vector(0f, 0f, -1f), hit.normal)
    }

    @Test
    fun intersectionOccursOnOutside() {
        val ray = Ray(point(0f, 0f, -5f), vector(0f, 0f, 1f))
        val shape = Sphere()
        val intersection = Intersection(4f, shape)

        val hit = intersection.prepareHit(ray)

        assertFalse(hit.inside)
    }

    @Test
    fun intersectionOccursInside() {
        val ray = Ray(point(0f, 0f, 0f), vector(0f, 0f, 1f))
        val shape = Sphere()
        val intersection = Intersection(1f, shape)

        val hit = intersection.prepareHit(ray)

        assertTupleEquals(point(0f, 0f, 1f), hit.point, epsilon)
        assertTupleEquals(vector(0f, 0f, -1f), hit.eye, epsilon)
        assertTrue(hit.inside)
        assertTupleEquals(vector(0f, 0f, -1f), hit.normal, epsilon)
    }

    @Test
    fun pointIsOffset() {
        val ray = Ray(point(0f, 0f, -5f), vector(0f, 0f, 1f))
        val shape = Sphere()
        val intersection = Intersection(4f, shape)

        val hit = intersection.prepareHit(ray)

        assertTrue(hit.point.z > -1.1f && hit.point.z < -1f) { "Actual value: ${hit.point.z}" }
    }

    @Test
    internal fun precomputeReflectionVector() {
        val shape = Plane()
        val ray = Ray(point(0f, 1f, -1f), vector(0f, -sqrt(2f) / 2, sqrt(2f) / 2))
        val intersection = Intersection(sqrt(2f), shape)
        val hit = intersection.prepareHit(ray)
        assertEquals(vector(0f, sqrt(2f) / 2, sqrt(2f) / 2), hit.reflect)
    }

    @ParameterizedTest
    @ArgumentsSource(RefractiveIndexAtVariousIntersections::class)
    fun refractiveIndexAtVariousIntersections(index: Int, n1: Float, n2: Float) {
        val a = glassSphere().apply {
            transform = scaling(2f, 2f, 2f)
            material = material.copy(refractiveIndex = 1.5f)
        }
        val b = glassSphere().apply {
            transform = translation(0f, 0f, -0.25f)
            material = material.copy(refractiveIndex = 2.0f)
        }
        val c = glassSphere().apply {
            transform = translation(0f, 0f, 0.25f)
            material = material.copy(refractiveIndex = 2.5f)
        }
        val ray = Ray(point(0f, 0f, -4f), vector(0f, 0f, 1f))
        val xs = intersections(Intersection(2f, a),
                Intersection(2.75f, b),
                Intersection(3.25f, c),
                Intersection(4.75f, b),
                Intersection(5.25f, c),
                Intersection(6f, a))

        val hit = xs[index].prepareHit(ray, xs)

        assertEquals(n1, hit.n1)
        assertEquals(n2, hit.n2)
    }

    class RefractiveIndexAtVariousIntersections : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext): Stream<Arguments> = Stream.of(
                Arguments.of(0, 1f, 1.5f),
                Arguments.of(1, 1.5f, 2f),
                Arguments.of(2, 2f, 2.5f),
                Arguments.of(3, 2.5f, 2.5f),
                Arguments.of(4, 2.5f, 1.5f),
                Arguments.of(5, 1.5f, 1f)
        )
    }

    @Test
    fun underPointIsOffsetBelowSurface() {
        val shape = glassSphere()
        val ray = Ray(point(0f, 0f, -5f), vector(0f, 0f, 1f))
        val intersection = Intersection(4f, shape)
        val xs = raytracer.renderer.intersections(intersection)

        val hit = intersection.prepareHit(ray, xs)

        assertThat(hit.underPoint.z, allOf(greaterThan(-1f), lessThan(-0.9f)))
    }

    @Test
    fun approximationUnderTotalInternalReflection() {
        val shape = glassSphere()
        val ray = Ray(point(0f, 0f, sqrt(2f) / 2), vector(0f, 1f, 0f))
        val xs = listOf(Intersection(-sqrt(2f) / 2, shape), Intersection(sqrt(2f) / 2, shape))

        val hit = xs[1].prepareHit(ray, xs)
        val reflectance = hit.schlick()

        assertEquals(1f, reflectance)
    }

    @Test
    fun approximationWithPerpendicularViewingAngle() {
        val shape = glassSphere()
        val ray = Ray(point(0f, 0f, 0f), vector(0f, 1f, 0f))
        val xs = listOf(Intersection(-1f, shape), Intersection(1f, shape))

        val hit = xs[1].prepareHit(ray, xs)
        val reflectance = hit.schlick()

        assertEquals(0.04f, reflectance, epsilon)
    }

    @Test
    fun approximationWithSmallAngleAndN2GreaterThanN1() {
        val shape = glassSphere()
        val ray = Ray(point(0f, 0.99f, -2f), vector(0f, 0f, 1f))
        val xs = listOf(Intersection(1.8589f, shape))

        val hit = xs[0].prepareHit(ray, xs)
        val reflectance = hit.schlick()

        assertEquals(0.48873f, reflectance, epsilon)
    }

    private companion object {
        val epsilon = 0.001f
    }
}
