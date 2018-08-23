package math

import org.junit.jupiter.api.Assertions.assertEquals
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
}
