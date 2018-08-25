package raytracer.renderer

import raytracer.math.Point
import raytracer.math.Vector
import raytracer.math.point
import kotlin.math.sqrt

class Sphere {
    fun intersects(ray: Ray): List<Intersection> {
        val sphereToRay = ray.origin - point(0f, 0f, 0f)

        val a = ray.direction dot ray.direction
        val b = (ray.direction dot sphereToRay) * 2f
        val c = (sphereToRay dot sphereToRay) - 1f

        val discriminant = b * b - 4f * a * c
        if (discriminant < 0f) {
            return intersections()
        }

        val i1 = Intersection((-b - sqrt(discriminant)) / (2f * a), this)
        val i2 = Intersection((-b + sqrt(discriminant)) / (2f * a), this)

        return if (i1.t > i2.t) intersections(i2, i1) else intersections(i1, i2)
    }
}

data class Ray(val origin: Point, val direction: Vector) {
    fun position(t: Float): Point = origin + direction * t
}

class Intersection(val t: Float, val obj: Sphere)

fun intersections(vararg i: Intersection) = listOf(*i)
