package raytracer.renderer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import raytracer.math.black
import raytracer.math.point
import raytracer.math.white

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

}
