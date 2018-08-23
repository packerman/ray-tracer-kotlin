package math

import java.util.*

data class Matrix4 internal constructor(private val matrix: FloatArray) {

    constructor(m00: Float, m01: Float, m02: Float, m03: Float,
                m10: Float, m11: Float, m12: Float, m13: Float,
                m20: Float, m21: Float, m22: Float, m23: Float,
                m30: Float, m31: Float, m32: Float, m33: Float) : this(floatArrayOf(
            m00, m01, m02, m03,
            m10, m11, m12, m13,
            m20, m21, m22, m23,
            m30, m31, m32, m33))

    constructor(f: (Int, Int) -> Float) : this(floatArrayOf(
            f(0, 0), f(0, 1), f(0, 2), f(0, 3),
            f(1, 0), f(1, 1), f(1, 2), f(1, 3),
            f(2, 0), f(2, 1), f(2, 2), f(2, 3),
            f(3, 0), f(3, 1), f(3, 2), f(3, 3)))

    constructor() : this(FloatArray(16))

    operator fun get(i: Int, j: Int): Float =
            matrix[4 * i + j]

    operator fun div(d: Float): Matrix4 =
            Matrix4 { row, column ->
                this[row, column] / d
            }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Matrix4

        return Arrays.equals(matrix, other.matrix)
    }

    override fun hashCode(): Int = Arrays.hashCode(matrix)

    companion object {
        val IDENTITY = Matrix4(
                1f, 0f, 0f, 0f,
                0f, 1f, 0f, 0f,
                0f, 0f, 1f, 0f,
                0f, 0f, 0f, 1f)
    }
}

operator fun Matrix4.times(other: Matrix4) =
        Matrix4 { row, column ->
            this[row, 0] * other[0, column] +
                    this[row, 1] * other[1, column] +
                    this[row, 2] * other[2, column] +
                    this[row, 3] * other[3, column]
        }

operator fun Matrix4.times(tuple: Tuple) = Tuple { i ->
    this[i, 0] * tuple.x + this[i, 1] * tuple.y + this[i, 2] * tuple.z + this[i, 3] * tuple.w
}

fun Matrix4.transpose() =
        Matrix4(this[0, 0], this[1, 0], this[2, 0], this[3, 0],
                this[0, 1], this[1, 1], this[2, 1], this[3, 1],
                this[0, 2], this[1, 2], this[2, 2], this[3, 2],
                this[0, 3], this[1, 3], this[2, 3], this[3, 3])

fun Matrix4.inverse(): Matrix4 {
    return Matrix4 { row, column ->
        this.cofactor(row, column)
    }.transpose() / determinant
}

fun Matrix4.subMatrix(row: Int, column: Int): Matrix3 {
    val matrix = FloatArray(9)
    var k = 0
    for (i in 0..3) {
        for (j in 0..3) {
            if (i != row && j != column) {
                matrix[k++] = this[i, j]
            }
        }
    }
    return Matrix3(matrix)
}

val Matrix4.isInvertible: Boolean
    get() = this.determinant != 0f

val Matrix4.determinant: Float
    get() = this[0, 0] * cofactor(0, 0) +
            this[0, 1] * cofactor(0, 1) +
            this[0, 2] * cofactor(0, 2) +
            this[0, 3] * cofactor(0, 3)

fun Matrix4.minor(row: Int, column: Int): Float =
        subMatrix(row, column).determinant

fun Matrix4.cofactor(row: Int, column: Int): Float =
        cofactorSign(row, column) * minor(row, column)

data class Matrix3 internal constructor(private val matrix: FloatArray) {

    constructor(m00: Float, m01: Float, m02: Float,
                m10: Float, m11: Float, m12: Float,
                m20: Float, m21: Float, m22: Float) : this(floatArrayOf(
            m00, m01, m02,
            m10, m11, m12,
            m20, m21, m22))

    val size = 3

    operator fun get(i: Int, j: Int): Float =
            matrix[3 * i + j]

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Matrix3

        return Arrays.equals(matrix, other.matrix)
    }

    override fun hashCode(): Int {
        var result = Arrays.hashCode(matrix)
        result = 31 * result + size
        return result
    }
}

fun Matrix3.subMatrix(row: Int, column: Int): Matrix2 {
    val matrix = FloatArray(4)
    var k = 0
    for (i in 0..2) {
        for (j in 0..2) {
            if (i != row && j != column) {
                matrix[k++] = this[i, j]
            }
        }
    }
    return Matrix2(matrix)
}

val Matrix3.determinant: Float
    get() = this[0, 0] * cofactor(0, 0) +
            this[0, 1] * cofactor(0, 1) +
            this[0, 2] * cofactor(0, 2)

fun Matrix3.minor(row: Int, column: Int): Float =
        subMatrix(row, column).determinant

fun Matrix3.cofactor(row: Int, column: Int): Float =
        cofactorSign(row, column) * minor(row, column)

data class Matrix2 internal constructor(private val matrix: FloatArray) {

    constructor(m00: Float, m01: Float,
                m10: Float, m11: Float) : this(floatArrayOf(
            m00, m01,
            m10, m11))

    val size = 2

    operator fun get(i: Int, j: Int): Float =
            matrix[2 * i + j]

    val determinant: Float
        get() = matrix[0] * matrix[3] - matrix[1] * matrix[2]

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Matrix2

        return Arrays.equals(matrix, other.matrix)
    }

    override fun hashCode(): Int {
        var result = Arrays.hashCode(matrix)
        result = 31 * result + size
        return result
    }
}

private fun cofactorSign(row: Int, column: Int): Int =
        if ((row + column) % 2 == 0) 1 else -1
