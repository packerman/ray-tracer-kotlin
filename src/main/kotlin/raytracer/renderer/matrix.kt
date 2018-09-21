package raytracer.renderer

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

    operator fun times(other: Matrix4) =
            Matrix4(this.matrix[0] * other.matrix[0] + this.matrix[1] * other.matrix[4] + this.matrix[2] * other.matrix[8] + this.matrix[3] * other.matrix[12],
                    this.matrix[0] * other.matrix[1] + this.matrix[1] * other.matrix[5] + this.matrix[2] * other.matrix[9] + this.matrix[3] * other.matrix[13],
                    this.matrix[0] * other.matrix[2] + this.matrix[1] * other.matrix[6] + this.matrix[2] * other.matrix[10] + this.matrix[3] * other.matrix[14],
                    this.matrix[0] * other.matrix[3] + this.matrix[1] * other.matrix[7] + this.matrix[2] * other.matrix[11] + this.matrix[3] * other.matrix[15],
                    this.matrix[4] * other.matrix[0] + this.matrix[5] * other.matrix[4] + this.matrix[6] * other.matrix[8] + this.matrix[7] * other.matrix[12],
                    this.matrix[4] * other.matrix[1] + this.matrix[5] * other.matrix[5] + this.matrix[6] * other.matrix[9] + this.matrix[7] * other.matrix[13],
                    this.matrix[4] * other.matrix[2] + this.matrix[5] * other.matrix[6] + this.matrix[6] * other.matrix[10] + this.matrix[7] * other.matrix[14],
                    this.matrix[4] * other.matrix[3] + this.matrix[5] * other.matrix[7] + this.matrix[6] * other.matrix[11] + this.matrix[7] * other.matrix[15],
                    this.matrix[8] * other.matrix[0] + this.matrix[9] * other.matrix[4] + this.matrix[10] * other.matrix[8] + this.matrix[11] * other.matrix[12],
                    this.matrix[8] * other.matrix[1] + this.matrix[9] * other.matrix[5] + this.matrix[10] * other.matrix[9] + this.matrix[11] * other.matrix[13],
                    this.matrix[8] * other.matrix[2] + this.matrix[9] * other.matrix[6] + this.matrix[10] * other.matrix[10] + this.matrix[11] * other.matrix[14],
                    this.matrix[8] * other.matrix[3] + this.matrix[9] * other.matrix[7] + this.matrix[10] * other.matrix[11] + this.matrix[11] * other.matrix[15],
                    this.matrix[12] * other.matrix[0] + this.matrix[13] * other.matrix[4] + this.matrix[14] * other.matrix[8] + this.matrix[15] * other.matrix[12],
                    this.matrix[12] * other.matrix[1] + this.matrix[13] * other.matrix[5] + this.matrix[14] * other.matrix[9] + this.matrix[15] * other.matrix[13],
                    this.matrix[12] * other.matrix[2] + this.matrix[13] * other.matrix[6] + this.matrix[14] * other.matrix[10] + this.matrix[15] * other.matrix[14],
                    this.matrix[12] * other.matrix[3] + this.matrix[13] * other.matrix[7] + this.matrix[14] * other.matrix[11] + this.matrix[15] * other.matrix[15])

    operator fun times(tuple: Tuple) = Tuple(this.matrix[0] * tuple.x + this.matrix[1] * tuple.y + this.matrix[2] * tuple.z + this.matrix[3] * tuple.w,
            this.matrix[4] * tuple.x + this.matrix[5] * tuple.y + this.matrix[6] * tuple.z + this.matrix[7] * tuple.w,
            this.matrix[8] * tuple.x + this.matrix[9] * tuple.y + this.matrix[10] * tuple.z + this.matrix[11] * tuple.w,
            this.matrix[12] * tuple.x + this.matrix[13] * tuple.y + this.matrix[14] * tuple.z + this.matrix[15] * tuple.w)

    fun times3x3(tuple: Tuple) = Tuple(this.matrix[0] * tuple.x + this.matrix[1] * tuple.y + this.matrix[2] * tuple.z,
            this.matrix[4] * tuple.x + this.matrix[5] * tuple.y + this.matrix[6] * tuple.z,
            this.matrix[8] * tuple.x + this.matrix[9] * tuple.y + this.matrix[10] * tuple.z,
            0f)

    val determinant: Float
        get() = matrix[3] * matrix[6] * matrix[9] * matrix[12] - matrix[2] * matrix[7] * matrix[9] * matrix[12] - matrix[3] * matrix[5] * matrix[10] * matrix[12] + matrix[1] * matrix[7] * matrix[10] * matrix[12] +
                matrix[2] * matrix[5] * matrix[11] * matrix[12] - matrix[1] * matrix[6] * matrix[11] * matrix[12] - matrix[3] * matrix[6] * matrix[8] * matrix[13] + matrix[2] * matrix[7] * matrix[8] * matrix[13] +
                matrix[3] * matrix[4] * matrix[10] * matrix[13] - matrix[0] * matrix[7] * matrix[10] * matrix[13] - matrix[2] * matrix[4] * matrix[11] * matrix[13] + matrix[0] * matrix[6] * matrix[11] * matrix[13] +
                matrix[3] * matrix[5] * matrix[8] * matrix[14] - matrix[1] * matrix[7] * matrix[8] * matrix[14] - matrix[3] * matrix[4] * matrix[9] * matrix[14] + matrix[0] * matrix[7] * matrix[9] * matrix[14] +
                matrix[1] * matrix[4] * matrix[11] * matrix[14] - matrix[0] * matrix[5] * matrix[11] * matrix[14] - matrix[2] * matrix[5] * matrix[8] * matrix[15] + matrix[1] * matrix[6] * matrix[8] * matrix[15] +
                matrix[2] * matrix[4] * matrix[9] * matrix[15] - matrix[0] * matrix[6] * matrix[9] * matrix[15] - matrix[1] * matrix[4] * matrix[10] * matrix[15] + matrix[0] * matrix[5] * matrix[10] * matrix[15]

    val inverse: Matrix4
        get() {
            val detInv = 1f / determinant
            return Matrix4((matrix[6] * matrix[11] * matrix[13] - matrix[7] * matrix[10] * matrix[13] + matrix[7] * matrix[9] * matrix[14] - matrix[5] * matrix[11] * matrix[14] - matrix[6] * matrix[9] * matrix[15] + matrix[5] * matrix[10] * matrix[15]) * detInv,
                    (matrix[3] * matrix[10] * matrix[13] - matrix[2] * matrix[11] * matrix[13] - matrix[3] * matrix[9] * matrix[14] + matrix[1] * matrix[11] * matrix[14] + matrix[2] * matrix[9] * matrix[15] - matrix[1] * matrix[10] * matrix[15]) * detInv,
                    (matrix[2] * matrix[7] * matrix[13] - matrix[3] * matrix[6] * matrix[13] + matrix[3] * matrix[5] * matrix[14] - matrix[1] * matrix[7] * matrix[14] - matrix[2] * matrix[5] * matrix[15] + matrix[1] * matrix[6] * matrix[15]) * detInv,
                    (matrix[3] * matrix[6] * matrix[9] - matrix[2] * matrix[7] * matrix[9] - matrix[3] * matrix[5] * matrix[10] + matrix[1] * matrix[7] * matrix[10] + matrix[2] * matrix[5] * matrix[11] - matrix[1] * matrix[6] * matrix[11]) * detInv,
                    (matrix[7] * matrix[10] * matrix[12] - matrix[6] * matrix[11] * matrix[12] - matrix[7] * matrix[8] * matrix[14] + matrix[4] * matrix[11] * matrix[14] + matrix[6] * matrix[8] * matrix[15] - matrix[4] * matrix[10] * matrix[15]) * detInv,
                    (matrix[2] * matrix[11] * matrix[12] - matrix[3] * matrix[10] * matrix[12] + matrix[3] * matrix[8] * matrix[14] - matrix[0] * matrix[11] * matrix[14] - matrix[2] * matrix[8] * matrix[15] + matrix[0] * matrix[10] * matrix[15]) * detInv,
                    (matrix[3] * matrix[6] * matrix[12] - matrix[2] * matrix[7] * matrix[12] - matrix[3] * matrix[4] * matrix[14] + matrix[0] * matrix[7] * matrix[14] + matrix[2] * matrix[4] * matrix[15] - matrix[0] * matrix[6] * matrix[15]) * detInv,
                    (matrix[2] * matrix[7] * matrix[8] - matrix[3] * matrix[6] * matrix[8] + matrix[3] * matrix[4] * matrix[10] - matrix[0] * matrix[7] * matrix[10] - matrix[2] * matrix[4] * matrix[11] + matrix[0] * matrix[6] * matrix[11]) * detInv,
                    (matrix[5] * matrix[11] * matrix[12] - matrix[7] * matrix[9] * matrix[12] + matrix[7] * matrix[8] * matrix[13] - matrix[4] * matrix[11] * matrix[13] - matrix[5] * matrix[8] * matrix[15] + matrix[4] * matrix[9] * matrix[15]) * detInv,
                    (matrix[3] * matrix[9] * matrix[12] - matrix[1] * matrix[11] * matrix[12] - matrix[3] * matrix[8] * matrix[13] + matrix[0] * matrix[11] * matrix[13] + matrix[1] * matrix[8] * matrix[15] - matrix[0] * matrix[9] * matrix[15]) * detInv,
                    (matrix[1] * matrix[7] * matrix[12] - matrix[3] * matrix[5] * matrix[12] + matrix[3] * matrix[4] * matrix[13] - matrix[0] * matrix[7] * matrix[13] - matrix[1] * matrix[4] * matrix[15] + matrix[0] * matrix[5] * matrix[15]) * detInv,
                    (matrix[3] * matrix[5] * matrix[8] - matrix[1] * matrix[7] * matrix[8] - matrix[3] * matrix[4] * matrix[9] + matrix[0] * matrix[7] * matrix[9] + matrix[1] * matrix[4] * matrix[11] - matrix[0] * matrix[5] * matrix[11]) * detInv,
                    (matrix[6] * matrix[9] * matrix[12] - matrix[5] * matrix[10] * matrix[12] - matrix[6] * matrix[8] * matrix[13] + matrix[4] * matrix[10] * matrix[13] + matrix[5] * matrix[8] * matrix[14] - matrix[4] * matrix[9] * matrix[14]) * detInv,
                    (matrix[1] * matrix[10] * matrix[12] - matrix[2] * matrix[9] * matrix[12] + matrix[2] * matrix[8] * matrix[13] - matrix[0] * matrix[10] * matrix[13] - matrix[1] * matrix[8] * matrix[14] + matrix[0] * matrix[9] * matrix[14]) * detInv,
                    (matrix[2] * matrix[5] * matrix[12] - matrix[1] * matrix[6] * matrix[12] - matrix[2] * matrix[4] * matrix[13] + matrix[0] * matrix[6] * matrix[13] + matrix[1] * matrix[4] * matrix[14] - matrix[0] * matrix[5] * matrix[14]) * detInv,
                    (matrix[1] * matrix[6] * matrix[8] - matrix[2] * matrix[5] * matrix[8] + matrix[2] * matrix[4] * matrix[9] - matrix[0] * matrix[6] * matrix[9] - matrix[1] * matrix[4] * matrix[10] + matrix[0] * matrix[5] * matrix[10]) * detInv)
        }

    val transpose: Matrix4
        get() =
            Matrix4(this.matrix[0], this.matrix[4], this.matrix[8], this.matrix[12],
                    this.matrix[1], this.matrix[5], this.matrix[9], this.matrix[13],
                    this.matrix[2], this.matrix[6], this.matrix[10], this.matrix[14],
                    this.matrix[3], this.matrix[7], this.matrix[11], this.matrix[15])

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Matrix4

        return Arrays.equals(matrix, other.matrix)
    }

    override fun hashCode(): Int = Arrays.hashCode(matrix)

    companion object {
        val identity = Matrix4(
                1f, 0f, 0f, 0f,
                0f, 1f, 0f, 0f,
                0f, 0f, 1f, 0f,
                0f, 0f, 0f, 1f)
    }
}

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
