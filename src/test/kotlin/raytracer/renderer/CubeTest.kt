package raytracer.renderer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import java.util.stream.Stream

internal class CubeTest {

    @ParameterizedTest
    @ArgumentsSource(RayIntersectingCube::class)
    fun rayIntersectingCube(origin: Point, direction: Vector, t1: Float, t2: Float) {
        val cube = Cube()
        val ray = Ray(origin, direction)
        val xs = cube.intersect(ray)

        assertEquals(2, xs.size)
        assertEquals(t1, xs[0].t)
        assertEquals(t2, xs[1].t)
    }

    private class RayIntersectingCube : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> = Stream.of(
                Arguments.of(point(5f, 0.5f, 0f), vector(-1f, 0f, 0f), 4f, 6f),
                Arguments.of(point(-5f, 0.5f, 0f), vector(1f, 0f, 0f), 4f, 6f),
                Arguments.of(point(0.5f, 5f, 0f), vector(0f, -1f, 0f), 4f, 6f),
                Arguments.of(point(0.5f, -5f, 0f), vector(0f, 1f, 0f), 4f, 6f),
                Arguments.of(point(0.5f, 0f, 5f), vector(0f, 0f, -1f), 4f, 6f),
                Arguments.of(point(0.5f, 0f, -5f), vector(0f, 0f, 1f), 4f, 6f),
                Arguments.of(point(0f, 0.5f, 0f), vector(0f, 0f, 1f), -1f, 1f))
    }

    @ParameterizedTest
    @ArgumentsSource(RayMissesCube::class)
    fun rayMissesCube(origin: Point, direction: Vector) {
        val cube = Cube()
        val ray = Ray(origin, direction)
        val xs = cube.intersect(ray)

        assertEquals(0, xs.size)
    }

    private class RayMissesCube : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> = Stream.of(
                Arguments.of(point(-2f, 0f, 0f), vector(0.2673f, 0.5345f, 0.8018f)),
                Arguments.of(point(0f, -2f, 0f), vector(0.8018f, 0.2673f, 0.5345f)),
                Arguments.of(point(0f, 0f, -2f), vector(0.5345f, 0.8018f, 0.2673f)),
                Arguments.of(point(2f, 0f, 2f), vector(0f, 0f, -1f)),
                Arguments.of(point(0f, 2f, 2f), vector(0f, -1f, 0f)),
                Arguments.of(point(2f, 2f, 0f), vector(-1f, 0f, 0f)))
    }

    @ParameterizedTest
    @ArgumentsSource(NormalOnSurfaceOfCube::class)
    fun normalOnSurfaceOfCube(point: Point, normal: Vector) {
        val cube = Cube()
        val normalAt = cube.normalAt(point)

        assertEquals(normal, normalAt)
    }

    private class NormalOnSurfaceOfCube : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext?): Stream<out Arguments> = Stream.of(
                Arguments.of(point(1f, 0.5f, -0.8f), vector(1f, 0f, 0f)),
                Arguments.of(point(-1f, -0.2f, 0.9f), vector(-1f, 0f, 0f)),
                Arguments.of(point(-0.4f, 1f, -0.1f), vector(0f, 1f, 0f)),
                Arguments.of(point(0.3f, -1f, -0.7f), vector(0f, -1f, 0f)),
                Arguments.of(point(-0.6f, 0.3f, 1f), vector(0f, 0f, 1f)),
                Arguments.of(point(0.4f, 0.4f, -1f), vector(0f, 0f, -1f)),
                Arguments.of(point(1f, 1f, 1f), vector(1f, 0f, 0f)),
                Arguments.of(point(-1f, -1f, -1f), vector(-1f, 0f, 0f)))
    }
}
