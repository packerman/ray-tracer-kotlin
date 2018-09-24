package raytracer.renderer

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import raytracer.utils.assertTupleEquals
import kotlin.math.sqrt

internal class TupleKtTest {

    @Test
    fun tupleWithWEqualsOneIsAPoint() {
        val a = Tuple(4.3f, -4.2f, 3.1f, 1.0f)

        assertEquals(4.3f, a.x)
        assertEquals(-4.2f, a.y)
        assertEquals(3.1f, a.z)
        assertEquals(1.0f, a.w)

        assertTrue(a.isPoint)
        assertFalse(a.isVector)
    }

    @Test
    fun tupleWithWEqualsZeroIsAVector() {
        val a = Tuple(4.3f, -4.2f, 3.1f, 0.0f)

        assertEquals(4.3f, a.x)
        assertEquals(-4.2f, a.y)
        assertEquals(3.1f, a.z)
        assertEquals(0.0f, a.w)

        assertFalse(a.isPoint)
        assertTrue(a.isVector)
    }

    @Test
    fun createPoint() {
        val p = point(4f, -4f, 3f)
        assertEquals(Tuple(4f, -4f, 3f, 1f), p)
    }

    @Test
    fun createVector() {
        val v = vector(4f, -4f, 3f)
        assertEquals(Tuple(4f, -4f, 3f, 0f), v)
    }

    @Test
    fun add() {
        val a1 = Tuple(3f, -2f, 5f, 1f)
        val a2 = Tuple(-2f, 3f, 1f, 0f)

        assertEquals(Tuple(1f, 1f, 6f, 1f), a1 + a2)
    }

    @Test
    fun subtractTwoPoints() {
        val p1 = point(3f, 2f, 1f)
        val p2 = point(5f, 6f, 7f)

        assertEquals(vector(-2f, -4f, -6f), p1 - p2)
    }

    @Test
    fun subtractVectorFromPoint() {
        val p = point(3f, 2f, 1f)
        val v = vector(5f, 6f, 7f)

        assertEquals(point(-2f, -4f, -6f), p - v)
    }

    @Test
    fun subtractTwoVectors() {
        val v1 = vector(3f, 2f, 1f)
        val v2 = vector(5f, 6f, 7f)

        assertEquals(vector(-2f, -4f, -6f), v1 - v2)
    }

    @Test
    fun subtractVectorFromZeroVector() {
        val zero = vector(0f, 0f, 0f)
        val v = vector(1f, -2f, -3f)

        assertEquals(vector(-1f, 2f, 3f), zero - v)
    }

    @Test
    fun negateTuple() {
        val a = Tuple(1f, -2f, 3f, -4f)

        assertEquals(Tuple(-1f, 2f, -3f, 4f), -a)
    }

    @Test
    fun multiplyTupleByScalar() {
        val a = Tuple(1f, -2f, 3f, -4f)

        assertEquals(Tuple(3.5f, -7f, 10.5f, -14f), a * 3.5f)
    }

    @Test
    fun multiplyTupleByFraction() {
        val a = Tuple(1f, -2f, 3f, -4f)

        assertEquals(Tuple(0.5f, -1f, 1.5f, -2f), a * 0.5f)
    }

    @Test
    fun divideTupleByScalar() {
        val a = Tuple(1f, -2f, 3f, -4f)

        assertEquals(Tuple(0.5f, -1f, 1.5f, -2f), a / 2f)
    }

    @Test
    fun magnitudeOfVector100() {
        val v = vector(1f, 0f, 0f)
        assertEquals(1f, v.length)
    }

    @Test
    fun magnitudeOfVector010() {
        val v = vector(0f, 1f, 0f)
        assertEquals(1f, v.length)
    }

    @Test
    fun magnitudeOfVector001() {
        val v = vector(0f, 0f, 1f)
        assertEquals(1f, v.length)
    }

    @Test
    fun magnitudeOfVector123() {
        val v = vector(1f, 2f, 3f)
        assertEquals(sqrt(14f), v.length)
    }

    @Test
    fun magnitudeOfVector_1_2_3() {
        val v = vector(-1f, -2f, -3f)
        assertEquals(sqrt(14f), v.length)
    }

    @Test
    fun normalizeVector400Gives100() {
        val v = vector(4f, 0f, 0f)
        assertEquals(vector(1f, 0f, 0f), v.normalize())
    }

    @Test
    fun normalizeVector123() {
        val v = vector(1f, 2f, 3f)
        assertTupleEquals(vector(0.26726f, 0.53452f, 0.80178f), v.normalize(), epsilon)
    }

    @Test
    fun magnitudeOfNormalizedVector() {
        val v = vector(1f, 2f, 3f)
        val n = v.normalize()

        assertEquals(1f, n.length, epsilon)
    }

    @Test
    fun dotProduct() {
        val a = vector(1f, 2f, 3f)
        val b = vector(2f, 3f, 4f)

        assertEquals(20f, a dot b)
    }

    @Test
    fun crossProduct() {
        val a = vector(1f, 2f, 3f)
        val b = vector(2f, 3f, 4f)

        assertEquals(vector(-1f, 2f, -1f), a cross b)
        assertEquals(vector(1f, -2f, 1f), b cross a)
    }

    @Test
    fun createColor() {
        val c = color(-0.5f, 0.4f, 1.7f)

        assertEquals(-0.5f, c.red)
        assertEquals(0.4f, c.green)
        assertEquals(1.7f, c.blue)
    }

    @Test
    fun addColors() {
        val c1 = color(0.9f, 0.6f, 0.75f)
        val c2 = color(0.7f, 0.1f, 0.25f)

        assertTupleEquals(color(1.6f, 0.7f, 1f), c1 + c2, epsilon)
    }

    @Test
    fun subtractColors() {
        val c1 = color(0.9f, 0.6f, 0.75f)
        val c2 = color(0.7f, 0.1f, 0.25f)

        assertTupleEquals(color(0.2f, 0.5f, 0.5f), c1 - c2, epsilon)
    }

    @Test
    fun multiplyColorByScalar() {
        val c = color(0.2f, 0.3f, 0.4f)

        assertTupleEquals(color(0.4f, 0.6f, 0.8f), c * 2f, epsilon)
    }

    @Test
    fun multiplyColors() {
        val c1 = color(1f, 0.2f, 0.4f)
        val c2 = color(0.9f, 1f, 0.1f)

        assertTupleEquals(color(0.9f, 0.2f, 0.04f), c1 * c2, epsilon)
    }

    @Test
    fun reflectVectorApproachingAt45Degrees() {
        val v = vector(1f, -1f, 0f)
        val n = vector(0f, 1f, 0f)

        val r = v.reflect(n)

        assertEquals(vector(1f, 1f, 0f), r)
    }

    @Test
    fun reflectVectorOffSlantedSurface() {
        val v = vector(0f, -1f, 0f)
        val n = vector(sqrt(2f) / 2, sqrt(2f) / 2, 0f)
        val r = v.reflect(n)

        assertTupleEquals(vector(1f, 0f, 0f), r, epsilon)
    }

    @Test
    fun tupleBuilderWithDefaultValue() {
        val tb = TupleBuilder()

        assertEquals(Tuple(0f, 0f, 0f, 0f), tb.build())
    }

    @Test
    fun tupleBuilderInitialValue() {
        val t = Tuple(1f, 2f, 3f, 4f)
        val tb = TupleBuilder(t)

        assertEquals(t, tb.build())
    }

    @Test
    fun canAddTupleBuilder() {
        val tb = TupleBuilder()

        tb += Tuple(1f, 2f, 3f, 4f)
        tb += Tuple(5f, 6f, 7f, 8f)

        assertEquals(Tuple(6f, 8f, 10f, 12f), tb.build())
    }

    @Test
    fun sumAllTupleBuilder() {
        val tb = TupleBuilder()

        tb.sumAll(Tuple(1f, 2f, 3f, 4f), Tuple(5f, 6f, 7f, 8f))

        assertEquals(Tuple(6f, 8f, 10f, 12f), tb.build())
    }

    @Test
    fun sumAllThreeTupleBuilder() {
        val tb = TupleBuilder()

        tb.sumAll(Tuple(1f, 2f, 3f, 4f),
                Tuple(5f, 6f, 7f, 8f),
                Tuple(10f, 11f, 12f, 13f))

        assertEquals(Tuple(16f, 19f, 22f, 25f), tb.build())
    }

    companion object {
        private const val epsilon = 0.00001f
    }
}
