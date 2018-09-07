package raytracer.renderer

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import raytracer.utils.assertMatrixEquals

internal class MatrixTest {

    @Nested
    internal inner class Matrix4Test {

        @Test
        fun createMatrix4() {
            val m = Matrix4(
                    1f, 2f, 3f, 4f,
                    5.5f, 6.5f, 7.5f, 8.5f,
                    9f, 10f, 11f, 12f,
                    13.5f, 14.5f, 15.5f, 16.5f
            )

            Assertions.assertEquals(1f, m[0, 0])
            Assertions.assertEquals(4f, m[0, 3])
            Assertions.assertEquals(5.5f, m[1, 0])
            Assertions.assertEquals(7.5f, m[1, 2])
            Assertions.assertEquals(11f, m[2, 2])
            Assertions.assertEquals(13.5f, m[3, 0])
            Assertions.assertEquals(15.5f, m[3, 2])
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

            Assertions.assertEquals(c, a * b)
        }

        @Test
        fun multiplyMatrixByTuple() {
            val a = Matrix4(
                    1f, 2f, 3f, 4f,
                    2f, 4f, 4f, 2f,
                    8f, 6f, 4f, 1f,
                    0f, 0f, 0f, 1f)
            val b = Tuple(1f, 2f, 3f, 1f)

            Assertions.assertEquals(Tuple(18f, 24f, 33f, 1f), a * b)
        }

        @Test
        fun multiplyMatrixByIdentity() {
            val a = Matrix4(
                    0f, 1f, 2f, 4f,
                    1f, 2f, 4f, 8f,
                    2f, 4f, 8f, 16f,
                    4f, 8f, 16f, 32f)

            Assertions.assertEquals(a, a * Matrix4.identity)
        }

        @Test
        fun multiplyTupleByIdentity() {
            val a = Tuple(1f, 2f, 3f, 4f)

            Assertions.assertEquals(a, Matrix4.identity * a)
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

            Assertions.assertEquals(transposed, a.transpose())
        }

        @Test
        fun transposeIdentityMatrix() {
            Assertions.assertEquals(Matrix4.identity, Matrix4.identity.transpose())
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

            Assertions.assertEquals(subMatrix, a.subMatrix(2, 1))
        }

        @Test
        fun determinant() {
            val a = Matrix4(
                    -2f, -8f, 3f, 5f,
                    -3f, 1f, 7f, 3f,
                    1f, 2f, -9f, 6f,
                    -6f, 7f, 7f, -9f)

            Assertions.assertEquals(690f, a.cofactor(0, 0))
            Assertions.assertEquals(447f, a.cofactor(0, 1))
            Assertions.assertEquals(210f, a.cofactor(0, 2))
            Assertions.assertEquals(51f, a.cofactor(0, 3))
            Assertions.assertEquals(-4071f, a.determinant)
        }

        @Test
        fun invertibleMatrix() {
            val a = Matrix4(
                    6f, 4f, 4f, 4f,
                    5f, 5f, 7f, 6f,
                    4f, -9f, 3f, -7f,
                    9f, 1f, 7f, -6f)

            Assertions.assertTrue(a.isInvertible)
        }

        @Test
        fun nonInvertibleMatrix() {
            val a = Matrix4(
                    -4f, 2f, -2f, -3f,
                    9f, 6f, 2f, 6f,
                    0f, -5f, 1f, -5f,
                    0f, 0f, 0f, -0f)

            Assertions.assertFalse(a.isInvertible)
        }

        @Test
        fun inverseMatrix() {
            val a = Matrix4(
                    -5f, 2f, 6f, -8f,
                    1f, -5f, 1f, 8f,
                    7f, 7f, -6f, -7f,
                    1f, -3f, 7f, 4f)

            val b = a.inverse()

            Assertions.assertEquals(532f, a.determinant)
            Assertions.assertEquals(-160f, a.cofactor(2, 3))
            Assertions.assertEquals(-160f / 532, b[3, 2], epsilon)
            Assertions.assertEquals(105f, a.cofactor(3, 2))
            Assertions.assertEquals(105f / 532, b[2, 3], epsilon)

            val inverted = Matrix4(
                    0.21805f, 0.45113f, 0.24060f, -0.04511f,
                    -0.80827f, -1.45677f, -0.44361f, 0.52068f,
                    -0.07895f, -0.22368f, -0.05263f, 0.19737f,
                    -0.52256f, -0.81391f, -0.30075f, 0.30639f)

            assertMatrixEquals(inverted, b, epsilon)
        }

        @Test
        fun invertIdentity() {
            assertMatrixEquals(Matrix4.identity, Matrix4.identity.inverse(), epsilon)
        }

        @Test
        fun multiplyByInverse() {
            val a = Matrix4(
                    6f, 4f, 4f, 4f,
                    5f, 5f, 7f, 6f,
                    4f, -9f, 3f, -7f,
                    9f, 1f, 7f, -6f)

            assertMatrixEquals(Matrix4.identity, a * a.inverse(), epsilon)
        }
    }

    @Nested
    internal inner class ViewTransformTest {

        @Test
        fun transformationMatrixForDefaultOrientation() {
            val from = point(0f, 0f, 0f)
            val to = point(0f, 0f, -1f)
            val up = vector(0f, 1f, 0f)

            val t = viewTransform(from, to, up)

            Assertions.assertEquals(Matrix4.identity, t)
        }

        @Test
        fun viewTransformationMatrixLookingInPositiveZDirection() {
            val from = point(0f, 0f, 0f)
            val to = point(0f, 0f, 1f)
            val up = vector(0f, 1f, 0f)

            val t = viewTransform(from, to, up)

            Assertions.assertEquals(scaling(-1f, 1f, -1f), t)
        }

        @Test
        fun viewTransformationMovesWorld() {
            val from = point(0f, 0f, 8f)
            val to = point(0f, 0f, 0f)
            val up = vector(0f, 1f, 0f)

            val t = viewTransform(from, to, up)

            Assertions.assertEquals(translation(0f, 0f, -8f), t)
        }

        @Test
        fun arbitraryViewTransformation() {
            val from = point(1f, 3f, 2f)
            val to = point(4f, -2f, 8f)
            val up = vector(1f, 1f, 0f)

            val t = viewTransform(from, to, up)

            val expected = Matrix4(
                    -0.50709f, 0.50709f, 0.67612f, -2.36643f,
                    0.76772f, 0.60609f, 0.12122f, -2.82843f,
                    -0.35857f, 0.59761f, -0.71714f, 0.0f,
                    0.0f, 0.0f, 0.0f, 1.0f)

            assertMatrixEquals(expected, t, epsilon)
        }
    }

    @Nested
    internal inner class Matrix3Test {

        @Test
        fun createMatrix() {
            val m = Matrix3(-3f, -5f, 0f,
                    1f, -2f, -7f,
                    0f, 1f, 1f)

            Assertions.assertEquals(3, m.size)
        }

        @Test
        fun subMatrix() {
            val a = Matrix3(1f, 5f, 0f,
                    -3f, 2f, 7f,
                    0f, 6f, -3f)

            val subMatrix = Matrix2(
                    -3f, 2f,
                    0f, 6f)

            Assertions.assertEquals(subMatrix, a.subMatrix(0, 2))
        }

        @Test
        fun minor() {
            val a = Matrix3(
                    3f, 5f, 0f,
                    2f, -1f, -7f,
                    6f, -1f, 5f)

            val b = a.subMatrix(1, 0)

            Assertions.assertEquals(25f, b.determinant)
            Assertions.assertEquals(25f, a.minor(1, 0))
        }

        @Test
        fun cofactor() {
            val a = Matrix3(
                    3f, 5f, 0f,
                    2f, -1f, -7f,
                    6f, -1f, 5f)

            Assertions.assertEquals(-12f, a.minor(0, 0))
            Assertions.assertEquals(-12f, a.cofactor(0, 0))
            Assertions.assertEquals(25f, a.minor(1, 0))
            Assertions.assertEquals(-25f, a.cofactor(1, 0))
        }

        @Test
        fun determinant() {
            val a = Matrix3(
                    1f, 2f, 6f,
                    -5f, 8f, -4f,
                    2f, 6f, 4f)

            Assertions.assertEquals(56f, a.cofactor(0, 0))
            Assertions.assertEquals(12f, a.cofactor(0, 1))
            Assertions.assertEquals(-46f, a.cofactor(0, 2))
            Assertions.assertEquals(-196f, a.determinant)
        }
    }

    @Nested
    internal inner class Matrix2Test {

        @Test
        fun createMatrix() {
            val m = Matrix2(-3f, -5f,
                    1f, -2f)

            Assertions.assertEquals(2, m.size)
        }

        @Test
        fun determinant() {
            val a = Matrix2(1f, 5f,
                    -3f, 2f)

            Assertions.assertEquals(17f, a.determinant)
        }
    }

    companion object {
        private const val epsilon = 0.00001f
    }
}
