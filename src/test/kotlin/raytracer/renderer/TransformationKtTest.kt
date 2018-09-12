package raytracer.renderer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import raytracer.utils.assertTupleEquals
import kotlin.math.PI
import kotlin.math.sqrt

internal class TransformationKtTest {

    @Test
    fun translatePoint() {
        val transform = translation(5f, -3f, 2f)
        val p = point(-3f, 4f, 5f)

        assertEquals(point(2f, 1f, 7f), transform * p)
    }

    @Test
    fun inverseOfTranslation() {
        val transform = translation(5f, -3f, 2f)
        val inv = transform.inverse
        val p = point(-3f, 4f, 5f)

        assertEquals(point(-8f, 7f, 3f), inv * p)
    }

    @Test
    fun translateVector() {
        val transform = translation(5f, -3f, 2f)
        val v = vector(-3f, 4f, 5f)

        assertEquals(v, transform * v)
    }

    @Test
    fun scalePoint() {
        val transform = scaling(2f, 3f, 4f)
        val p = point(-4f, 6f, 8f)

        assertEquals(point(-8f, 18f, 32f), transform * p)
    }

    @Test
    fun scaleVector() {
        val transform = scaling(2f, 3f, 4f)
        val p = vector(-4f, 6f, 8f)

        assertEquals(vector(-8f, 18f, 32f), transform * p)
    }

    @Test
    fun scaleInverseVector() {
        val transform = scaling(2f, 3f, 4f)
        val inv = transform.inverse
        val p = vector(-4f, 6f, 8f)

        assertEquals(vector(-2f, 2f, 2f), inv * p)
    }

    @Test
    fun reflection() {
        val transform = scaling(-1f, 1f, 1f)
        val p = vector(2f, 3f, 4f)

        assertEquals(vector(-2f, 3f, 4f), transform * p)
    }

    @Test
    fun rotateX() {
        val p = point(0f, 1f, 0f)
        val halfQuarter = rotationX((PI / 4).toFloat())
        val fullQuarter = rotationX((PI / 2).toFloat())

        assertTupleEquals(point(0f, sqrt(2f) / 2, sqrt(2f) / 2), halfQuarter * p, epsilon)
        assertTupleEquals(point(0f, 0f, 1f), fullQuarter * p, epsilon)
    }

    @Test
    fun rotateXInverse() {
        val p = point(0f, 1f, 0f)
        val halfQuarter = rotationX((PI / 4).toFloat())
        val inv = halfQuarter.inverse

        assertTupleEquals(point(0f, sqrt(2f) / 2, -sqrt(2f) / 2), inv * p, epsilon)
    }

    @Test
    fun rotateY() {
        val p = point(0f, 0f, 1f)
        val halfQuarter = rotationY((PI / 4).toFloat())
        val fullQuarter = rotationY((PI / 2).toFloat())

        assertTupleEquals(point(sqrt(2f) / 2, 0f, sqrt(2f) / 2), halfQuarter * p, epsilon)
        assertTupleEquals(point(1f, 0f, 0f), fullQuarter * p, epsilon)
    }

    @Test
    fun rotateZ() {
        val p = point(0f, 1f, 0f)
        val halfQuarter = rotationZ((PI / 4).toFloat())
        val fullQuarter = rotationZ((PI / 2).toFloat())

        assertTupleEquals(point(-sqrt(2f) / 2, sqrt(2f) / 2, 0f), halfQuarter * p, epsilon)
        assertTupleEquals(point(-1f, 0f, 0f), fullQuarter * p, epsilon)
    }

    @Test
    fun shearingMovesXInProportionToY() {
        val transform = shearing(1f, 0f, 0f, 0f, 0f, 0f)
        val p = point(2f, 3f, 4f)
        assertEquals(point(5f, 3f, 4f), transform * p)
    }

    @Test
    fun shearingMovesXInProportionToZ() {
        val transform = shearing(0f, 1f, 0f, 0f, 0f, 0f)
        val p = point(2f, 3f, 4f)
        assertEquals(point(6f, 3f, 4f), transform * p)
    }

    @Test
    fun shearingMovesYInProportionToX() {
        val transform = shearing(0f, 0f, 1f, 0f, 0f, 0f)
        val p = point(2f, 3f, 4f)
        assertEquals(point(2f, 5f, 4f), transform * p)
    }

    @Test
    fun shearingMovesYInProportionToZ() {
        val transform = shearing(0f, 0f, 0f, 1f, 0f, 0f)
        val p = point(2f, 3f, 4f)
        assertEquals(point(2f, 7f, 4f), transform * p)
    }

    @Test
    fun shearingMovesZInProportionToX() {
        val transform = shearing(0f, 0f, 0f, 0f, 1f, 0f)
        val p = point(2f, 3f, 4f)
        assertEquals(point(2f, 3f, 6f), transform * p)
    }

    @Test
    fun shearingMovesZInProportionToY() {
        val transform = shearing(0f, 0f, 0f, 0f, 0f, 1f)
        val p = point(2f, 3f, 4f)
        assertEquals(point(2f, 3f, 7f), transform * p)
    }

    @Test
    fun individualTransformationsAppliedInSequence() {
        val p = point(1f, 0f, 1f)
        val a = rotationX((PI / 2).toFloat())
        val b = scaling(5f, 5f, 5f)
        val c = translation(10f, 5f, 7f)

        val p2 = a * p
        assertTupleEquals(point(1f, -1f, 0f), p2, epsilon)

        val p3 = b * p2
        assertTupleEquals(point(5f, -5f, 0f), p3, epsilon)

        val p4 = c * p3
        assertTupleEquals(point(15f, 0f, 7f), p4, epsilon)
    }

    @Test
    fun chainedTransformationMustBeAppliedInReverseOrder() {
        val p = point(1f, 0f, 1f)
        val a = rotationX((PI / 2).toFloat())
        val b = scaling(5f, 5f, 5f)
        val c = translation(10f, 5f, 7f)

        val t = c * b * a

        assertTupleEquals(point(15f, 0f, 7f), t * p)
    }

    @Test
    fun fluentInterface() {
        val p = point(1f, 0f, 1f)

        val t = identity()
                .rotateX((PI / 2).toFloat())
                .scale(5f, 5f, 5f)
                .translate(10f, 5f, 7f)

        assertTupleEquals(point(15f, 0f, 7f), t * p)
    }

    companion object {
        const val epsilon = 0.00001f
    }
}
