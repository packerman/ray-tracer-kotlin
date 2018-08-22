package math

import kotlin.math.sqrt

data class Tuple(val x: Float, val y: Float, val z: Float, val w: Float) {

    val length: Float
        get() = sqrt(x * x + y * y + z * z + w * w)

    operator fun plus(other: Tuple) =
            Tuple(this.x + other.x, this.y + other.y, this.z + other.z, this.w + other.w)

    operator fun minus(other: Tuple) =
            Tuple(this.x - other.x, this.y - other.y, this.z - other.z, this.w - other.w)

    operator fun unaryMinus() =
            Tuple(-this.x, -this.y, -this.z, -this.w)

    operator fun times(s: Float) =
            Tuple(this.x * s, this.y * s, this.z * s, this.w * s)

    operator fun times(other: Tuple) =
            Tuple(this.x * other.x, this.y * other.y, this.z * other.z, this.w * other.w)

    operator fun div(s: Float) =
            Tuple(this.x / s, this.y / s, this.z / s, this.w / s)

    fun normalize() =
            Tuple(this.x / length, this.y / length, this.z / length, this.w / length)

    infix fun dot(other: Tuple): Float =
            this.x * other.x + this.y * other.y + this.z * other.z + this.w * other.w

    infix fun cross(other: Tuple) =
            vector(this.y * other.z - this.z * other.y,
                    this.z * other.x - this.x * other.z,
                    this.x * other.y - this.y * other.x)

    val red
        get() = x

    val green
        get() = y

    val blue
        get() = z
}

val Tuple.isPoint: Boolean
    get() = this.w == 1f

val Tuple.isVector: Boolean
    get() = this.w == 0f

typealias Point = Tuple

fun point(x: Float, y: Float, z: Float) = Tuple(x, y, z, 1.0f)

typealias Vector = Tuple

fun vector(x: Float, y: Float, z: Float) = Tuple(x, y, z, 0.0f)

typealias Color = Tuple

fun color(red: Float, green: Float, blue: Float) = Tuple(red, green, blue, 0.0f)
