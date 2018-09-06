package raytracer.renderer

import raytracer.math.Color
import raytracer.math.Point
import kotlin.math.floor

data class StripePattern(val a: Color, val b: Color) {

    fun stripeAt(point: Point): Color =
            if (floor(point.x).toInt() % 2 == 0) a else b
}
