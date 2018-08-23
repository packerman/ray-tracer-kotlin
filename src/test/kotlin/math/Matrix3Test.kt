package math

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class Matrix3Test {

    @Test
    fun createMatrix() {
        val m = Matrix3(-3f, -5f, 0f,
                1f, -2f, -7f,
                0f, 1f, 1f)

        assertEquals(3, m.size)
    }

    @Test
    fun subMatrix() {
        val a = Matrix3(1f, 5f, 0f,
                -3f, 2f, 7f,
                0f, 6f, -3f)

        val subMatrix = Matrix2(
                -3f, 2f,
                0f, 6f)

        assertEquals(subMatrix, a.subMatrix(0, 2))
    }

    @Test
    fun minor() {
        val a = Matrix3(
                3f, 5f, 0f,
                2f, -1f, -7f,
                6f, -1f, 5f)

        val b = a.subMatrix(1, 0)

        assertEquals(25f, b.determinant)
        assertEquals(25f, a.minor(1, 0))
    }

    @Test
    fun cofactor() {
        val a = Matrix3(
                3f, 5f, 0f,
                2f, -1f, -7f,
                6f, -1f, 5f)

        assertEquals(-12f, a.minor(0, 0))
        assertEquals(-12f, a.cofactor(0, 0))
        assertEquals(25f, a.minor(1, 0))
        assertEquals(-25f, a.cofactor(1, 0))
    }

    @Test
    fun determinant() {
        val a = Matrix3(
                1f, 2f, 6f,
                -5f, 8f, -4f,
                2f, 6f, 4f)

        assertEquals(56f, a.cofactor(0, 0))
        assertEquals(12f, a.cofactor(0, 1))
        assertEquals(-46f, a.cofactor(0, 2))
        assertEquals(-196f, a.determinant)
    }
}
