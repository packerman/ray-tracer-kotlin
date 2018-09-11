package raytracer.renderer

import java.util.*

data class Matrix4 internal constructor(private val m: FloatArray) {

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
            m[4 * i + j]

    operator fun div(d: Float): Matrix4 =
            Matrix4 { row, column ->
                this[row, column] / d
            }

    val determinant: Float
        get() = m[3] * m[6] * m[9] * m[12] - m[2] * m[7] * m[9] * m[12] - m[3] * m[5] * m[10] * m[12] + m[1] * m[7] * m[10] * m[12] +
                m[2] * m[5] * m[11] * m[12] - m[1] * m[6] * m[11] * m[12] - m[3] * m[6] * m[8] * m[13] + m[2] * m[7] * m[8] * m[13] +
                m[3] * m[4] * m[10] * m[13] - m[0] * m[7] * m[10] * m[13] - m[2] * m[4] * m[11] * m[13] + m[0] * m[6] * m[11] * m[13] +
                m[3] * m[5] * m[8] * m[14] - m[1] * m[7] * m[8] * m[14] - m[3] * m[4] * m[9] * m[14] + m[0] * m[7] * m[9] * m[14] +
                m[1] * m[4] * m[11] * m[14] - m[0] * m[5] * m[11] * m[14] - m[2] * m[5] * m[8] * m[15] + m[1] * m[6] * m[8] * m[15] +
                m[2] * m[4] * m[9] * m[15] - m[0] * m[6] * m[9] * m[15] - m[1] * m[4] * m[10] * m[15] + m[0] * m[5] * m[10] * m[15]

    fun inverse(): Matrix4 {
        val detInv = 1f / determinant
        return Matrix4(
                (m[6] * m[11] * m[13] - m[7] * m[10] * m[13] + m[7] * m[9] * m[14] - m[5] * m[11] * m[14] - m[6] * m[9] * m[15] + m[5] * m[10] * m[15]) * detInv,
                (m[3] * m[10] * m[13] - m[2] * m[11] * m[13] - m[3] * m[9] * m[14] + m[1] * m[11] * m[14] + m[2] * m[9] * m[15] - m[1] * m[10] * m[15]) * detInv,
                (m[2] * m[7] * m[13] - m[3] * m[6] * m[13] + m[3] * m[5] * m[14] - m[1] * m[7] * m[14] - m[2] * m[5] * m[15] + m[1] * m[6] * m[15]) * detInv,
                (m[3] * m[6] * m[9] - m[2] * m[7] * m[9] - m[3] * m[5] * m[10] + m[1] * m[7] * m[10] + m[2] * m[5] * m[11] - m[1] * m[6] * m[11]) * detInv,
                (m[7] * m[10] * m[12] - m[6] * m[11] * m[12] - m[7] * m[8] * m[14] + m[4] * m[11] * m[14] + m[6] * m[8] * m[15] - m[4] * m[10] * m[15]) * detInv,
                (m[2] * m[11] * m[12] - m[3] * m[10] * m[12] + m[3] * m[8] * m[14] - m[0] * m[11] * m[14] - m[2] * m[8] * m[15] + m[0] * m[10] * m[15]) * detInv,
                (m[3] * m[6] * m[12] - m[2] * m[7] * m[12] - m[3] * m[4] * m[14] + m[0] * m[7] * m[14] + m[2] * m[4] * m[15] - m[0] * m[6] * m[15]) * detInv,
                (m[2] * m[7] * m[8] - m[3] * m[6] * m[8] + m[3] * m[4] * m[10] - m[0] * m[7] * m[10] - m[2] * m[4] * m[11] + m[0] * m[6] * m[11]) * detInv,
                (m[5] * m[11] * m[12] - m[7] * m[9] * m[12] + m[7] * m[8] * m[13] - m[4] * m[11] * m[13] - m[5] * m[8] * m[15] + m[4] * m[9] * m[15]) * detInv,
                (m[3] * m[9] * m[12] - m[1] * m[11] * m[12] - m[3] * m[8] * m[13] + m[0] * m[11] * m[13] + m[1] * m[8] * m[15] - m[0] * m[9] * m[15]) * detInv,
                (m[1] * m[7] * m[12] - m[3] * m[5] * m[12] + m[3] * m[4] * m[13] - m[0] * m[7] * m[13] - m[1] * m[4] * m[15] + m[0] * m[5] * m[15]) * detInv,
                (m[3] * m[5] * m[8] - m[1] * m[7] * m[8] - m[3] * m[4] * m[9] + m[0] * m[7] * m[9] + m[1] * m[4] * m[11] - m[0] * m[5] * m[11]) * detInv,
                (m[6] * m[9] * m[12] - m[5] * m[10] * m[12] - m[6] * m[8] * m[13] + m[4] * m[10] * m[13] + m[5] * m[8] * m[14] - m[4] * m[9] * m[14]) * detInv,
                (m[1] * m[10] * m[12] - m[2] * m[9] * m[12] + m[2] * m[8] * m[13] - m[0] * m[10] * m[13] - m[1] * m[8] * m[14] + m[0] * m[9] * m[14]) * detInv,
                (m[2] * m[5] * m[12] - m[1] * m[6] * m[12] - m[2] * m[4] * m[13] + m[0] * m[6] * m[13] + m[1] * m[4] * m[14] - m[0] * m[5] * m[14]) * detInv,
                (m[1] * m[6] * m[8] - m[2] * m[5] * m[8] + m[2] * m[4] * m[9] - m[0] * m[6] * m[9] - m[1] * m[4] * m[10] + m[0] * m[5] * m[10]) * detInv
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Matrix4

        return Arrays.equals(m, other.m)
    }

    override fun hashCode(): Int = Arrays.hashCode(m)

    companion object {
        val identity = Matrix4(
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

val Matrix4.isInvertible: Boolean
    get() = this.determinant != 0f

fun viewTransform(from: Point, to: Point, up: Vector): Matrix4 {
    val forward = (to - from).normalize()
    val left = forward.cross(up.normalize())
    val trueUp = left.cross(forward)
    val orientation = Matrix4(
            left.x, left.y, left.z, 0f,
            trueUp.x, trueUp.y, trueUp.z, 0f,
            -forward.x, -forward.y, -forward.z, 0f,
            0f, 0f, 0f, 1f)
    return orientation * translation(-from.x, -from.y, -from.z)
}
