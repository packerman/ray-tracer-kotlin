package math

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class Matrix4Test {

    @Test
    fun createMatrix4() {
        val m = Matrix4(
                1f, 2f, 3f, 4f,
                5.5f, 6.5f, 7.5f, 8.5f,
                9f, 10f, 11f, 12f,
                13.5f, 14.5f, 15.5f, 16.5f
        )

        assertEquals(1f, m[0, 0])
        assertEquals(4f, m[0, 3])
        assertEquals(5.5f, m[1, 0])
        assertEquals(7.5f, m[1, 2])
        assertEquals(11f, m[2, 2])
        assertEquals(13.5f, m[3, 0])
        assertEquals(15.5f, m[3, 2])
    }

    @Test
    fun multiplyMatrices() {
        val a = Matrix4(
                1f, 2f, 3f, 4f,
                2f, 3f, 4f, 5f,
                3f, 4f, 5f, 6f,
                4f, 5f, 6f, 7f)
        val b = Matrix4(
                0f, 1f, 2f, 4f,
                1f, 2f, 4f, 8f,
                2f, 4f, 8f, 16f,
                4f, 8f, 16f, 32f)

        val c = Matrix4(
                24f, 49f, 98f, 196f,
                31f, 64f, 128f, 256f,
                38f, 79f, 158f, 316f,
                45f, 94f, 188f, 376f)

        assertEquals(c, a * b)
    }

    @Test
    fun multiplyMatrixByTuple() {
        val a = Matrix4(
                1f, 2f, 3f, 4f,
                2f, 4f, 4f, 2f,
                8f, 6f, 4f, 1f,
                0f, 0f, 0f, 1f)
        val b = Tuple(1f, 2f, 3f, 1f)

        assertEquals(Tuple(18f, 24f, 33f, 1f), a * b)
    }

    @Test
    fun multiplyMatrixByIdentity() {
        val a = Matrix4(
                0f, 1f, 2f, 4f,
                1f, 2f, 4f, 8f,
                2f, 4f, 8f, 16f,
                4f, 8f, 16f, 32f)

        assertEquals(a, a * Matrix4.IDENTITY)
    }

    @Test
    fun multiplyTupleByIdentity() {
        val a = Tuple(1f, 2f, 3f, 4f)

        assertEquals(a, Matrix4.IDENTITY * a)
    }

    @Test
    fun transposeMatrix() {
        val a = Matrix4(
                0f, 9f, 3f, 0f,
                9f, 8f, 0f, 8f,
                1f, 8f, 5f, 3f,
                0f, 0f, 5f, 8f)

        val transposed = Matrix4(
                0f, 9f, 1f, 0f,
                9f, 8f, 8f, 0f,
                3f, 0f, 5f, 5f,
                0f, 8f, 3f, 8f
        )

        assertEquals(transposed, a.transpose())
    }

    @Test
    fun transposeIdentityMatrix() {
        assertEquals(Matrix4.IDENTITY, Matrix4.IDENTITY.transpose())
    }

    @Test
    fun subMatrix() {
        val a = Matrix4(
                -6f, 1f, 1f, 6f,
                -8f, 5f, 8f, 6f,
                -1f, 0f, 8f, 2f,
                -7f, 1f, -1f, 1f)

        val subMatrix = Matrix3(
                -6f, 1f, 6f,
                -8f, 8f, 6f,
                -7f, -1f, 1f)

        assertEquals(subMatrix, a.subMatrix(2, 1))
    }

    @Test
    fun determinant() {
        val a = Matrix4(
                -2f, -8f, 3f, 5f,
                -3f, 1f, 7f, 3f,
                1f, 2f, -9f, 6f,
                -6f, 7f, 7f, -9f)

        assertEquals(690f, a.cofactor(0, 0))
        assertEquals(447f, a.cofactor(0, 1))
        assertEquals(210f, a.cofactor(0, 2))
        assertEquals(51f, a.cofactor(0, 3))
        assertEquals(-4071f, a.determinant)
    }

    @Test
    fun invertibleMatrix() {
        val a = Matrix4(
                6f, 4f, 4f, 4f,
                5f, 5f, 7f, 6f,
                4f, -9f, 3f, -7f,
                9f, 1f, 7f, -6f)

        assertTrue(a.isInvertible)
    }

    @Test
    fun nonInvertibleMatrix() {
        val a = Matrix4(
                -4f, 2f, -2f, -3f,
                9f, 6f, 2f, 6f,
                0f, -5f, 1f, -5f,
                0f, 0f, 0f, -0f)

        assertFalse(a.isInvertible)
    }

    @Test
    fun inverseMatrix() {
        val a = Matrix4(
                -5f, 2f, 6f, -8f,
                1f, -5f, 1f, 8f,
                7f, 7f, -6f, -7f,
                1f, -3f, 7f, 4f)

        val b = a.inverse()

        assertEquals(532f, a.determinant)
        assertEquals(-160f, a.cofactor(2, 3))
        assertEquals(-160f / 532, b[3, 2], epsilon)
        assertEquals(105f, a.cofactor(3, 2))
        assertEquals(105f / 532, b[2, 3], epsilon)

        val inverted = Matrix4(
                0.21805f, 0.45113f, 0.24060f, -0.04511f,
                -0.80827f, -1.45677f, -0.44361f, 0.52068f,
                -0.07895f, -0.22368f, -0.05263f, 0.19737f,
                -0.52256f, -0.81391f, -0.30075f, 0.30639f)

        assertMatrixEquals(inverted, b, epsilon)
    }

    @Test
    fun invertIdentity() {
        assertMatrixEquals(Matrix4.IDENTITY, Matrix4.IDENTITY.inverse(), epsilon)
    }

    @Test
    fun multiplyByInverse() {
        val a = Matrix4(
                6f, 4f, 4f, 4f,
                5f, 5f, 7f, 6f,
                4f, -9f, 3f, -7f,
                9f, 1f, 7f, -6f)

        assertMatrixEquals(Matrix4.IDENTITY, a * a.inverse(), epsilon)
    }

    companion object {
        private const val epsilon = 0.00001f
    }
}
