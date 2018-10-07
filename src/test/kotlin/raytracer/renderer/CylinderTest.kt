package raytracer.renderer

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import java.util.stream.Stream

internal class CylinderTest {

    @ParameterizedTest
    @ArgumentsSource(RayMissesCylinder::class)
    fun rayMissesCylinder(origin: Point, direction: Vector) {
        val cyl = Cylinder()
        val dir = direction.normalize()
        val ray = Ray(origin, dir)

        val xs = cyl.intersect(ray)

        assertThat(xs, empty())
    }

    private class RayMissesCylinder : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext): Stream<Arguments> = Stream.of(
                Arguments.of(point(1f, 0f, 0f), vector(0f, 1f, 0f)),
                Arguments.of(point(0f, 0f, 0f), vector(0f, 1f, 0f)),
                Arguments.of(point(0f, 0f, -5f), vector(1f, 1f, 1f)))
    }

    @ParameterizedTest
    @ArgumentsSource(RayStrikesCylinder::class)
    fun rayStrikesCylinder(origin: Point, direction: Vector, t0: Float, t1: Float) {
        val cyl = Cylinder()
        val dir = direction.normalize()
        val ray = Ray(origin, dir)

        val xs = cyl.intersect(ray)

        assertThat(xs, hasSize(2))
        assertEquals(t0, xs[0].t, epsilon)
        assertEquals(t1, xs[1].t, epsilon)
    }

    private class RayStrikesCylinder : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext): Stream<Arguments> = Stream.of(
                Arguments.of(point(1f, 0f, -5f), vector(0f, 0f, 1f), 5f, 5f),
                Arguments.of(point(0f, 0f, -5f), vector(0f, 0f, 1f), 4f, 6f),
                Arguments.of(point(0.5f, 0f, -5f), vector(0.1f, 1f, 1f), 6.80798f, 7.08872f))
    }

    @ParameterizedTest
    @ArgumentsSource(NormalVectorOnCylinder::class)
    fun normalVectorOnCylinder(point: Point, normal: Vector) {
        val cyl = Cylinder()

        val n = cyl.normalAt(point)

        assertEquals(normal, n)
    }

    private class NormalVectorOnCylinder : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext): Stream<Arguments> = Stream.of(
                Arguments.of(point(1f, 0f, 0f), vector(1f, 0f, 0f)),
                Arguments.of(point(0f, 5f, -1f), vector(0f, 0f, -1f)),
                Arguments.of(point(0f, -2f, 1f), vector(0f, 0f, 1f)),
                Arguments.of(point(-1f, 1f, 0f), vector(-1f, 0f, 0f)))
    }

    @Test
    fun defaultMinimumAndMaximumForCylinder() {
        val cyl = Cylinder()

        assertEquals(Float.NEGATIVE_INFINITY, cyl.minimum)
        assertEquals(Float.POSITIVE_INFINITY, cyl.maximum)
    }

    @ParameterizedTest
    @ArgumentsSource(IntersectingConstrainedCylinder::class)
    fun intersectingConstrainedCylinder(point: Point, normal: Vector, count: Int) {
        val cyl = Cylinder(minimum = 1f, maximum = 2f)
        val dir = normal.normalize()
        val ray = Ray(point, dir)

        val xs = cyl.intersect(ray)

        assertThat(xs, hasSize(count))
    }

    private class IntersectingConstrainedCylinder : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext): Stream<Arguments> = Stream.of(
                Arguments.of(point(0f, 1.5f, 0f), vector(0.1f, 1f, 0f), 0),
                Arguments.of(point(0f, 3f, -5f), vector(0f, 0f, 1f), 0),
                Arguments.of(point(0f, 0f, -5f), vector(0f, 0f, 1f), 0),
                Arguments.of(point(0f, 2f, -5f), vector(0f, 0f, 1f), 0),
                Arguments.of(point(0f, 1f, -5f), vector(0f, 0f, 1f), 0),
                Arguments.of(point(0f, 1.5f, -2f), vector(0f, 0f, 1f), 2))
    }

    @Test
    fun defaultClosedValueForCylinder() {
        val cyl = Cylinder()
        assertFalse(cyl.closed)
    }

    @ParameterizedTest
    @ArgumentsSource(IntersectingCapsOfClosedCylinder::class)
    fun intersectingCapsOfClosedCylinder(point: Point, normal: Vector, count: Int) {
        val cyl = Cylinder(minimum = 1f, maximum = 2f, closed = true)
        val dir = normal.normalize()
        val ray = Ray(point, dir)

        val xs = cyl.intersect(ray)

        assertThat(xs, hasSize(count))
    }

    private class IntersectingCapsOfClosedCylinder : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext): Stream<Arguments> = Stream.of(
                Arguments.of(point(0f, 3f, 0f), vector(0f, -1f, 0f), 2),
                Arguments.of(point(0f, 3f, -2f), vector(0f, -1f, 2f), 2),
                Arguments.of(point(0f, 4f, -2f), vector(0f, -1f, 1f), 2),
                Arguments.of(point(0f, 0f, -2f), vector(0f, 1f, 2f), 2),
                Arguments.of(point(0f, -1f, -2f), vector(0f, 1f, 1f), 2))
    }

    @ParameterizedTest
    @ArgumentsSource(NormalOnCylinderEndCaps::class)
    fun normalOnCylinderEndCaps(point: Point, normal: Vector) {
        val cyl = Cylinder(minimum = 1f, maximum = 2f, closed = true)

        val n = cyl.normalAt(point)

        assertEquals(normal, n)
    }

    private class NormalOnCylinderEndCaps : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext): Stream<Arguments> = Stream.of(
                Arguments.of(point(0f, 1f, 0f), vector(0f, -1f, 0f)),
                Arguments.of(point(0.5f, 1f, 0f), vector(0f, -1f, 0f)),
                Arguments.of(point(0f, 1f, 0.5f), vector(0f, -1f, 0f)),
                Arguments.of(point(0f, 2f, 0f), vector(0f, 1f, 0f)),
                Arguments.of(point(0.5f, 2f, 0f), vector(0f, 1f, 0f)),
                Arguments.of(point(0f, 2f, 0.5f), vector(0f, 1f, 0f)))
    }

    companion object {
        private const val epsilon = 0.0001f
    }
}
