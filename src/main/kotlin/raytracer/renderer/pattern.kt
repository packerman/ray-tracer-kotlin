package raytracer.renderer

import kotlin.math.floor
import kotlin.math.sqrt

abstract class Pattern {

    var transform: Matrix4 = Matrix4.identity

    abstract fun patternAt(point: Point): Color

    fun patternAtShape(obj: Shape, point: Point): Color {
        val objectPoint = obj.transform.inverse() * point
        val patternPoint = this.transform.inverse() * objectPoint
        return patternAt(patternPoint)
    }
}

class StripePattern(val a: Color, val b: Color) : Pattern() {

    override fun patternAt(point: Point): Color =
            if (floor(point.x).toInt() % 2 == 0) a else b
}

class GradientPattern(val a: Color, val b: Color) : Pattern() {

    override fun patternAt(point: Point): Color =
            a + (b - a) * (point.x - floor(point.x))
}

class RingPattern(val a: Color, val b: Color) : Pattern() {

    override fun patternAt(point: Point): Color =
            if (floor(sqrt(point.x * point.x + point.z * point.z)).toInt() % 2 == 0) a else b
}

class CheckerPattern(val a: Color, val b: Color) : Pattern() {

    override fun patternAt(point: Point): Color =
            if ((floor(point.x) + floor(point.y) + floor(point.z)).toInt() % 2 == 0) a else b
}
