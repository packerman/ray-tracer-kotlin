package raytracer.renderer

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
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

            assertEquals(a, a * Matrix4.identity)
        }

        @Test
        fun multiplyTupleByIdentity() {
            val a = Tuple(1f, 2f, 3f, 4f)

            assertEquals(a, Matrix4.identity * a)
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
            assertEquals(Matrix4.identity, Matrix4.identity.transpose())
        }

        @Test
        fun determinant() {
            val a = Matrix4(
                    -2f, -8f, 3f, 5f,
                    -3f, 1f, 7f, 3f,
                    1f, 2f, -9f, 6f,
                    -6f, 7f, 7f, -9f)

            assertEquals(-4071f, a.determinant)
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
            assertEquals(-160f / 532, b[3, 2], epsilon)
            assertEquals(105f / 532, b[2, 3], epsilon)

            val inverted = Matrix4(
                    0.21805f, 0.45113f, 0.24060f, -0.04511f,
                    -0.80827f, -1.45677f, -0.44361f, 0.52068f,
                    -0.07895f, -0.22368f, -0.05263f, 0.19737f,
                    -0.52256f, -0.81391f, -0.30075f, 0.30639f)

            assertMatrixEquals(inverted, b, epsilon)
        }

        @Test
        internal fun inverseOfAnotherMatrix() {
            val a = Matrix4(
                    8f, -5f, 9f, 2f,
                    7f, 5f, 6f, 1f,
                    -6f, 0f, 9f, 6f,
                    -3f, 0f, -9f, -4f)

            val inverse = Matrix4(
                    -0.15385f, -0.15385f, -0.28205f, -0.53846f,
                    -0.07692f, 0.12308f, 0.02564f, 0.03077f,
                    0.35897f, 0.35897f, 0.43590f, 0.92308f,
                    -0.69231f, -0.69231f, -0.76923f, -1.92308f)

            assertMatrixEquals(inverse, a.inverse(), epsilon)
        }

        @Test
        internal fun inverseOfThirdMatrix() {
            val a = Matrix4(
                    9f, 3f, 0f, 9f,
                    -5f, -2f, -6f, -3f,
                    -4f, 9f, 6f, 4f,
                    -7f, 6f, 6f, 2f)

            val inverse = Matrix4(
                    -0.04074f, -0.07778f, 0.14444f, -0.22222f,
                    -0.07778f, 0.03333f, 0.36667f, -0.33333f,
                    -0.02901f, -0.14630f, -0.10926f, 0.12963f,
                    0.17778f, 0.06667f, -0.26667f, 0.33333f)

            assertMatrixEquals(inverse, a.inverse(), epsilon)
        }

        @Test
        internal fun multiplyProductByItsInverse() {
            val a = Matrix4(
                    3f, -9f, 7f, 3f,
                    3f, -8f, 2f, -9f,
                    -4f, 4f, 4f, 1f,
                    -6f, 5f, -1f, 1f)
            val b = Matrix4(
                    8f, 2f, 2f, 2f,
                    3f, -1f, 7f, 0f,
                    7f, 0f, 5f, 4f,
                    6f, -2f, 0f, 5f)

            val c = a * b

            assertMatrixEquals(a, c * b.inverse(), epsilon)
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

            assertEquals(Matrix4.identity, t)
        }

        @Test
        fun viewTransformationMatrixLookingInPositiveZDirection() {
            val from = point(0f, 0f, 0f)
            val to = point(0f, 0f, 1f)
            val up = vector(0f, 1f, 0f)

            val t = viewTransform(from, to, up)

            assertEquals(scaling(-1f, 1f, -1f), t)
        }

        @Test
        fun viewTransformationMovesWorld() {
            val from = point(0f, 0f, 8f)
            val to = point(0f, 0f, 0f)
            val up = vector(0f, 1f, 0f)

            val t = viewTransform(from, to, up)

            assertEquals(translation(0f, 0f, -8f), t)
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

    companion object {
        private const val epsilon = 0.00001f
    }
}
