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

    constructor() : this(FloatArray(16))

    operator fun get(i: Int, j: Int): Float =
            matrix[4 * i + j]

    internal operator fun set(row: Int, col: Int, value: Float) {
        matrix[4 * row + col] = value
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

operator fun Matrix4.times(other: Matrix4): Matrix4 {
    val m = Matrix4()
    for (row in 0..3) {
        for (col in 0..3) {
            m[row, col] = this[row, 0] * other[0, col] +
                    this[row, 1] * other[1, col] +
                    this[row, 2] * other[2, col] +
                    this[row, 3] * other[3, col]
        }
    }
    return m
}

operator fun Matrix4.times(tuple: Tuple) = Tuple(
        this[0, 0] * tuple.x + this[0, 1] * tuple.y + this[0, 2] * tuple.z + this[0, 3] * tuple.w,
        this[1, 0] * tuple.x + this[1, 1] * tuple.y + this[1, 2] * tuple.z + this[1, 3] * tuple.w,
        this[2, 0] * tuple.x + this[2, 1] * tuple.y + this[2, 2] * tuple.z + this[2, 3] * tuple.w,
        this[3, 0] * tuple.x + this[3, 1] * tuple.y + this[3, 2] * tuple.z + this[3, 3] * tuple.w)

fun Matrix4.transpose() =
        Matrix4(this[0, 0], this[1, 0], this[2, 0], this[3, 0],
                this[0, 1], this[1, 1], this[2, 1], this[3, 1],
                this[0, 2], this[1, 2], this[2, 2], this[3, 2],
                this[0, 3], this[1, 3], this[2, 3], this[3, 3])

data class Matrix3 internal constructor(private val matrix: FloatArray) {

    constructor(m00: Float, m01: Float, m02: Float,
                m10: Float, m11: Float, m12: Float,
                m20: Float, m21: Float, m22: Float) : this(floatArrayOf(
            m00, m01, m02,
            m10, m11, m12,
            m20, m21, m22
    ))

    val size = 3

    operator fun get(i: Int, j: Int): Float =
            matrix[3 * i + j]
}

data class Matrix2 internal constructor(private val matrix: FloatArray) {

    constructor(m00: Float, m01: Float,
                m10: Float, m11: Float) : this(floatArrayOf(
            m00, m01,
            m10, m11))

    val size = 2

    operator fun get(i: Int, j: Int): Float =
            matrix[2 * i + j]
}
