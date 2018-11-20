package raytracer.renderer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.lang.Math.toRadians
import kotlin.math.PI
import kotlin.math.sqrt

internal class BoundsTest {

    @Test
    fun transformBounds() {
        val b = Bounds(
                point(-1f, -1f, -1f),
                point(1f, 1f, 1f)
        )
        val t = translation(0.5f, 0.5f, 0.5f) * scaling(0.5f, 0.5f, 0.5f)

        val expected = Bounds(
                point(0f, 0f, 0f),
                point(1f, 1f, 1f)
        )
        assertEquals(expected, b.transform(t))
    }

    @Test
    fun rotateBounds() {
        val b = Bounds(
                point(-1f, -1f, -1f),
                point(1f, 1f, 1f)
        )
        val t = rotationY(toRadians(45.0).toFloat())

        val expected = Bounds(
                point(-sqrt(2f), -1f, -sqrt(2f)),
                point(sqrt(2f), 1f, sqrt(2f))
        )
        assertEquals(expected, b.transform(t))
    }

    @Test
    internal fun transformBoundsWithOneInfinity() {
        val b = Bounds(
                point(-1f, Float.NEGATIVE_INFINITY, -1f),
                point(-1f, Float.POSITIVE_INFINITY, 1f)
        )

        val t = scaling(2f, 2f, 2f)

        val expected = Bounds(
                point(-2f, Float.NEGATIVE_INFINITY, -2f),
                point(-2f, Float.POSITIVE_INFINITY, 2f)
        )
        assertEquals(expected, b.transform(t))
    }

    @Test
    internal fun transformBoundsWithTwoInfinity() {
        val b = Bounds(
                point(Float.NEGATIVE_INFINITY, 0f, Float.NEGATIVE_INFINITY),
                point(Float.POSITIVE_INFINITY, 0f, Float.POSITIVE_INFINITY)
        )

        val t = rotationY((PI / 4).toFloat())

        assertEquals(b, b.transform(t))
    }

    @Test
    internal fun transformBoundsWithTwoInfinit2() {
        val b = Bounds(
                point(0f, 0f, 0f),
                point(Float.POSITIVE_INFINITY, 0f, Float.POSITIVE_INFINITY)
        )

        val t = rotationY((PI / 2).toFloat())

        val expected = Bounds(
                point(0f, 0f, -Float.POSITIVE_INFINITY),
                point(Float.POSITIVE_INFINITY, 0f, 0f)
        )

        assertEquals(expected, b.transform(t))
    }
}
