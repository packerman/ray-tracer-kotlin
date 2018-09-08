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

fun Matrix4.inverse(): Matrix4 {
    return Matrix4 { row, column ->
        this.cofactor(row, column)
    }.transpose() / determinant
}

fun Matrix4.subMatrix(row: Int, column: Int): Matrix3 {
    return object : Matrix3() {
        override fun get(i: Int, j: Int): Float {
            return this@subMatrix[if (i < row) i else i + 1, if (j < column) j else j + 1]
        }
    }
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

abstract class Matrix3 {

    val size = 3

    abstract operator fun get(i: Int, j: Int): Float

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Matrix3) return false

        for (i in 0..2) {
            for (j in 0..2) {
                if (this[i, j] != other[i, j]) {
                    return false
                }
            }
        }
        return true
    }

    override fun hashCode(): Int = Objects.hash(
            this[0, 1], this[0, 1], this[0, 2],
            this[1, 1], this[1, 1], this[1, 2],
            this[2, 1], this[2, 1], this[2, 2]
    )

    override fun toString(): String {
        return "Matrix3(${this[0, 0]}, ${this[0, 1]}, ${this[0, 2]}, ${this[1, 0]}, ${this[1, 1]}, ${this[1, 2]}, ${this[2, 0]}, ${this[2, 1]}, ${this[2, 2]})"
    }

    companion object {
        operator fun invoke(m00: Float, m01: Float, m02: Float,
                            m10: Float, m11: Float, m12: Float,
                            m20: Float, m21: Float, m22: Float) = object : Matrix3() {

            override fun get(i: Int, j: Int): Float {
                return array[3 * i + j]
            }

            private val array = floatArrayOf(
                    m00, m01, m02,
                    m10, m11, m12,
                    m20, m21, m22)
        }
    }
}

fun Matrix3.subMatrix(row: Int, column: Int): Matrix2 {
    return object : Matrix2() {
        override fun get(i: Int, j: Int): Float {
            return this@subMatrix[if (i < row) i else i + 1, if (j < column) j else j + 1]
        }
    }
}

val Matrix3.determinant: Float
    get() = this[0, 0] * cofactor(0, 0) +
            this[0, 1] * cofactor(0, 1) +
            this[0, 2] * cofactor(0, 2)

fun Matrix3.minor(row: Int, column: Int): Float =
        subMatrix(row, column).determinant

fun Matrix3.cofactor(row: Int, column: Int): Float =
        cofactorSign(row, column) * minor(row, column)

abstract class Matrix2 {

    val size = 2

    abstract operator fun get(i: Int, j: Int): Float

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Matrix2) return false

        for (i in 0..1) {
            for (j in 0..1) {
                if (this[i, j] != other[i, j]) {
                    return false
                }
            }
        }
        return true
    }

    override fun hashCode(): Int = Objects.hash(
            this[0, 1], this[0, 1],
            this[1, 1], this[1, 1])

    override fun toString(): String {
        return "Matrix2(${this[0, 0]}, ${this[0, 1]}, ${this[1, 0]}, ${this[1, 1]})"
    }

    companion object {
        operator fun invoke(m00: Float, m01: Float,
                            m10: Float, m11: Float) = object : Matrix2() {

            override operator fun get(i: Int, j: Int): Float =
                    array[2 * i + j]

            private val array = floatArrayOf(
                    m00, m01,
                    m10, m11)
        }
    }
}

val Matrix2.determinant: Float
    get() = this[0, 0] * this[1, 1] - this[0, 1] * this[1, 0]

private fun cofactorSign(row: Int, column: Int): Int =
        if ((row + column) % 2 == 0) 1 else -1
