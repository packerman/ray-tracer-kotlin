package raytracer.renderer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import raytracer.math.*

internal class PatternTest {

    @Test
    internal fun defaultPatternTransformation() {
        val pattern = testPattern()

        assertEquals(Matrix4.identity, pattern.transform)
    }

    @Test
    internal fun assignTransformation() {
        val pattern = testPattern()

        pattern.transform = translation(1f, 2f, 3f)

        assertEquals(translation(1f, 2f, 3f), pattern.transform)
    }

    @Test
    internal fun patternWithObjectTransformation() {
        val shape = Sphere().apply {
            transform = scaling(2f, 2f, 2f)
        }
        val pattern = testPattern()

        val c = pattern.patternAtShape(shape, point(2f, 3f, 4f))

        assertEquals(color(1f, 1.5f, 2f), c)
    }

    @Test
    internal fun patternWithPatternTransformation() {
        val shape = Sphere()
        val pattern = testPattern().apply {
            transform = scaling(2f, 2f, 2f)
        }

        val c = pattern.patternAtShape(shape, point(2f, 3f, 4f))

        assertEquals(color(1f, 1.5f, 2f), c)
    }

    @Test
    internal fun patternWithBothObjectAndPatternTransformation() {
        val shape = Sphere().apply {
            transform = scaling(2f, 2f, 2f)
        }
        val pattern = testPattern().apply {
            transform = translation(0.5f, 1f, 1.5f)
        }

        val c = pattern.patternAtShape(shape, point(2.5f, 3f, 3.5f))

        assertEquals(color(0.75f, 0.5f, 0.25f), c)
    }

    private fun testPattern(): Pattern {
        return object : Pattern() {
            override fun patternAt(point: Point): Color = color(point.x, point.y, point.z)
        }
    }

    @Test
    internal fun createStripePattern() {
        val pattern = StripePattern(white, black)

        assertEquals(white, pattern.a)
        assertEquals(black, pattern.b)
    }

    @Test
    internal fun stripePatternIsConstantInY() {
        val pattern = StripePattern(white, black)

        assertEquals(white, pattern.patternAt(point(0f, 0f, 0f)))
        assertEquals(white, pattern.patternAt(point(0f, 1f, 0f)))
        assertEquals(white, pattern.patternAt(point(0f, 2f, 0f)))
    }

    @Test
    internal fun stripePatternIsConstantInZ() {
        val pattern = StripePattern(white, black)

        assertEquals(white, pattern.patternAt(point(0f, 0f, 0f)))
        assertEquals(white, pattern.patternAt(point(0f, 0f, 1f)))
        assertEquals(white, pattern.patternAt(point(0f, 2f, 2f)))
    }

    @Test
    internal fun stripePatternAlternatesInX() {
        val pattern = StripePattern(white, black)

        assertEquals(white, pattern.patternAt(point(0f, 0f, 0f)))
        assertEquals(white, pattern.patternAt(point(0.9f, 0f, 0f)))
        assertEquals(black, pattern.patternAt(point(1f, 0f, 0f)))
        assertEquals(black, pattern.patternAt(point(-0.1f, 0f, 0f)))
        assertEquals(black, pattern.patternAt(point(-1f, 0f, 0f)))
        assertEquals(white, pattern.patternAt(point(-1.1f, 0f, 0f)))
    }

    @Test
    internal fun gradientLinearlyInterpolatesBetweenColors() {
        val pattern = GradientPattern(black, white)

        assertEquals(black, pattern.patternAt(point(0f, 0f, 0f)))
        assertEquals(color(0.25f, 0.25f, 0.25f), pattern.patternAt(point(0.25f, 0f, 0f)))
        assertEquals(color(0.5f, 0.5f, 0.5f), pattern.patternAt(point(0.5f, 0f, 0f)))
        assertEquals(color(0.75f, 0.75f, 0.75f), pattern.patternAt(point(0.75f, 0f, 0f)))
    }

    @Test
    internal fun ringShouldExtendInBothXAndZ() {
        val pattern = RingPattern(black, white)

        assertEquals(black, pattern.patternAt(point(0f, 0f, 0f)))
        assertEquals(white, pattern.patternAt(point(1f, 0f, 0f)))
        assertEquals(white, pattern.patternAt(point(0f, 0f, 1f)))
        assertEquals(white, pattern.patternAt(point(0.708f, 0f, 0.708f)))
    }

    @Test
    internal fun checkerShouldRepeatInX() {
        val pattern = CheckerPattern(black, white)

        assertEquals(black, pattern.patternAt(point(0f, 0f, 0f)))
        assertEquals(black, pattern.patternAt(point(0.99f, 0f, 0f)))
        assertEquals(white, pattern.patternAt(point(1.01f, 0f, 0f)))
    }

    @Test
    internal fun checkerShouldRepeatInY() {
        val pattern = CheckerPattern(black, white)

        assertEquals(black, pattern.patternAt(point(0f, 0f, 0f)))
        assertEquals(black, pattern.patternAt(point(0f, 0.99f, 0f)))
        assertEquals(white, pattern.patternAt(point(0f, 1.01f, 0f)))
    }

    @Test
    internal fun checkerShouldRepeatInZ() {
        val pattern = CheckerPattern(black, white)

        assertEquals(black, pattern.patternAt(point(0f, 0f, 0f)))
        assertEquals(black, pattern.patternAt(point(0f, 0f, 0.99f)))
        assertEquals(white, pattern.patternAt(point(0f, 0f, 1.01f)))
    }
}
