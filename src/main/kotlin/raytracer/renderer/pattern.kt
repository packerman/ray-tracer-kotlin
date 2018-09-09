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

data class SolidPattern(val color: Color) : Pattern() {

    override fun patternAt(point: Point): Color = color
}

data class StripePattern(val a: Pattern, val b: Pattern) : Pattern() {

    constructor(a: Color, b: Color) : this(SolidPattern(a), SolidPattern(b))

    override fun patternAt(point: Point): Color =
            if (floor(point.x).toInt() % 2 == 0) a.patternAt(point) else b.patternAt(point)
}

data class GradientPattern(val a: Pattern, val b: Pattern) : Pattern() {

    constructor(a: Color, b: Color) : this(SolidPattern(a), SolidPattern(b))

    override fun patternAt(point: Point): Color =
            a.patternAt(point) + (b.patternAt(point) - a.patternAt(point)) * (point.x - floor(point.x))
}

data class RingPattern(val a: Pattern, val b: Pattern) : Pattern() {

    constructor(a: Color, b: Color) : this(SolidPattern(a), SolidPattern(b))

    override fun patternAt(point: Point): Color =
            if (floor(sqrt(point.x * point.x + point.z * point.z)).toInt() % 2 == 0) a.patternAt(point) else b.patternAt(point)
}

data class CheckerPattern(val a: Pattern, val b: Pattern) : Pattern() {

    constructor(a: Color, b: Color) : this(SolidPattern(a), SolidPattern(b))

    override fun patternAt(point: Point): Color =
            if ((floor(point.x) + floor(point.y) + floor(point.z)).toInt() % 2 == 0) a.patternAt(point) else b.patternAt(point)
}
