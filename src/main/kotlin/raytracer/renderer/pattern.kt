package raytracer.renderer

import raytracer.math.*
import kotlin.math.floor

data class StripePattern(val a: Color, val b: Color) {

    var transform: Matrix4 = Matrix4.IDENTITY

    fun stripeAt(point: Point): Color =
            if (floor(point.x).toInt() % 2 == 0) a else b

    fun stripeAtObject(obj: Shape, point: Point): Color {
        val objectPoint = obj.transform.inverse() * point
        val patternPoint = this.transform.inverse() * objectPoint
        return stripeAt(patternPoint)
    }
}
