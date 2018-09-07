package raytracer.renderer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import raytracer.math.*

internal class StripePatternTest {

    @Test
    internal fun createStripePattern() {
        val pattern = StripePattern(white, black)

        assertEquals(white, pattern.a)
        assertEquals(black, pattern.b)
    }

    @Test
    internal fun stripePatternIsConstantInY() {
        val pattern = StripePattern(white, black)

        assertEquals(white, pattern.stripeAt(point(0f, 0f, 0f)))
        assertEquals(white, pattern.stripeAt(point(0f, 1f, 0f)))
        assertEquals(white, pattern.stripeAt(point(0f, 2f, 0f)))
    }

    @Test
    internal fun stripePatternIsConstantInZ() {
        val pattern = StripePattern(white, black)

        assertEquals(white, pattern.stripeAt(point(0f, 0f, 0f)))
        assertEquals(white, pattern.stripeAt(point(0f, 0f, 1f)))
        assertEquals(white, pattern.stripeAt(point(0f, 2f, 2f)))
    }

    @Test
    internal fun stripePatternAlternatesInX() {
        val pattern = StripePattern(white, black)

        assertEquals(white, pattern.stripeAt(point(0f, 0f, 0f)))
        assertEquals(white, pattern.stripeAt(point(0.9f, 0f, 0f)))
        assertEquals(black, pattern.stripeAt(point(1f, 0f, 0f)))
        assertEquals(black, pattern.stripeAt(point(-0.1f, 0f, 0f)))
        assertEquals(black, pattern.stripeAt(point(-1f, 0f, 0f)))
        assertEquals(white, pattern.stripeAt(point(-1.1f, 0f, 0f)))
    }

    @Test
    internal fun stripesWithObjectTransformation() {
        val obj = Sphere().apply {
            transform = scaling(2f, 2f, 2f)
        }
        val pattern = StripePattern(black, white)

        val c = pattern.stripeAtObject(obj, point(1.5f, 0f, 0f))

        assertEquals(black, c)
    }

    @Test
    internal fun stripesWithPatternTransformation() {
        val obj = Sphere().apply {
            transform = scaling(2f, 2f, 2f)
        }
        val pattern = StripePattern(black, white)

        val c = pattern.stripeAtObject(obj, point(1.5f, 0f, 0f))

        assertEquals(black, c)
    }

    @Test
    internal fun stripesWithBothObjectAndPatternTransformation() {
        val obj = Sphere().apply {
            transform = scaling(2f, 2f, 2f)
        }
        val pattern = StripePattern(black, white).apply {
            transform = translation(0.5f, 0f, 0f)
        }

        val c = pattern.stripeAtObject(obj, point(2.5f, 0f, 0f))

        assertEquals(black, c)
    }
}
