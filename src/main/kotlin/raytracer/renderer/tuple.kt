package raytracer.renderer

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

fun point(x: Float, y: Float, z: Float): Point = Tuple(x, y, z, 1.0f)

typealias Vector = Tuple

fun vector(x: Float, y: Float, z: Float): Vector = Tuple(x, y, z, 0.0f)

typealias Color = Tuple

fun color(red: Float, green: Float, blue: Float): Color = Tuple(red, green, blue, 0.0f)

val black = color(0f, 0f, 0f)
val white = color(1f, 1f, 1f)
val red = color(1f, 0f, 0f)
val green = color(0f, 1f, 0f)
val blue = color(0f, 0f, 1f)

fun Tuple.reflect(normal: Vector): Vector =
        this - normal * 2f * this.dot(normal)

class TupleBuilder(private var x: Float,
                   private var y: Float,
                   private var z: Float,
                   private var w: Float) {

    constructor() : this(0f, 0f, 0f, 0f)

    constructor(tuple: Tuple) : this(tuple.x, tuple.y, tuple.z, tuple.w)

    operator fun plusAssign(tuple: Tuple) {
        x += tuple.x
        y += tuple.y
        z += tuple.z
        w += tuple.w
    }

    fun build(): Tuple = Tuple(x, y, z, w)

    fun sumAll(tuple1: Tuple, tuple2: Tuple) {
        x += tuple1.x + tuple2.x
        y += tuple1.y + tuple2.y
        z += tuple1.z + tuple2.z
        w += tuple1.w + tuple2.w
    }

    fun sumAll(tuple1: Tuple, tuple2: Tuple, tuple3: Tuple) {
        x += tuple1.x + tuple2.x + tuple3.x
        y += tuple1.y + tuple2.y + tuple3.y
        z += tuple1.z + tuple2.z + tuple3.z
        w += tuple1.w + tuple2.w + tuple3.w
    }
}
