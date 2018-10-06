package raytracer.renderer

import kotlin.math.abs
import kotlin.math.sqrt

abstract class Shape {
    var transform: Matrix4 = Matrix4.identity
    var material: Material = Material()

    fun intersect(ray: Ray): List<Intersection> {
        val localRay = ray.transform(transform.inverse)
        return localIntersect(localRay)
    }

    protected abstract fun localIntersect(ray: Ray): List<Intersection>

    fun normalAt(point: Point): Vector {
        val localPoint = transform.inverse * point
        val localNormal = localNormalAt(localPoint)
        val worldNormal = transform.inverse.transpose.times3x3(localNormal)
        return worldNormal.normalize()
    }

    protected abstract fun localNormalAt(point: Point): Vector
}

class Sphere : Shape() {
    override fun localIntersect(ray: Ray): List<Intersection> {
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

    override fun localNormalAt(point: Point): Vector {
        return point - point(0f, 0f, 0f)
    }
}

fun glassSphere(): Sphere =
        Sphere().apply {
            material = Material(transparency = 1f, refractiveIndex = 1.5f)
        }

class Plane : Shape() {
    override fun localIntersect(ray: Ray): List<Intersection> {
        if (abs(ray.direction.y) < 0.0001f) {
            return emptyList()
        }
        val t = -ray.origin.y / ray.direction.y
        return listOf(Intersection(t, this))
    }

    override fun localNormalAt(point: Point) = vector(0f, 1f, 0f)
}

class Cube : Shape() {
    override fun localIntersect(ray: Ray): List<Intersection> {
        val (xTMin, xTMax) = checkAxis(ray.origin.x, ray.direction.x)
        val (yTMin, yTMax) = checkAxis(ray.origin.y, ray.direction.y)
        val (zTMin, zTMax) = checkAxis(ray.origin.z, ray.direction.z)

        val tMin = maxOf(xTMin, yTMin, zTMin)
        val tMax = minOf(xTMax, yTMax, zTMax)

        return if (tMin > tMax) emptyList() else listOf(Intersection(tMin, this), Intersection(tMax, this))
    }

    private fun checkAxis(origin: Float, direction: Float): Pair<Float, Float> {
        val tMin = (-1f - origin) / direction
        val tMax = (1f - origin) / direction

        return if (tMin > tMax) Pair(tMax, tMin) else Pair(tMin, tMax)
    }

    override fun localNormalAt(point: Point): Vector {
        val maxC = maxOf(abs(point.x), abs(point.y), abs(point.z))
        return when (maxC) {
            abs(point.x) -> vector(point.x, 0f, 0f)
            abs(point.y) -> vector(0f, point.y, 0f)
            else -> vector(0f, 0f, point.z)
        }
    }
}

class Cylinder(val minimum: Float = Float.NEGATIVE_INFINITY, val maximum: Float = Float.POSITIVE_INFINITY) : Shape() {
    override fun localIntersect(ray: Ray): List<Intersection> {
        val a = ray.direction.x * ray.direction.x + ray.direction.z * ray.direction.z
        if (a < 0.0001) {
            return emptyList()
        }
        val b = 2 * ray.origin.x * ray.direction.x + 2 * ray.origin.z * ray.direction.z
        val c = ray.origin.x * ray.origin.x + ray.origin.z * ray.origin.z - 1
        val discriminant = b * b - 4 * a * c
        if (discriminant < 0) {
            return emptyList()
        }
        val t0 = (-b - sqrt(discriminant)) / (2 * a)
        val t1 = (-b + sqrt(discriminant)) / (2 * a)

        val xs = mutableListOf<Intersection>()

        val y0 = ray.origin.y + t0 * ray.direction.y
        if (this.minimum < y0 && y0 < this.maximum) {
            xs.add(Intersection(t0, this))
        }
        val y1 = ray.origin.y + t1 * ray.direction.y
        if (this.minimum < y1 && y1 < this.maximum) {
            xs.add(Intersection(t1, this))
        }
        return xs
    }

    override fun localNormalAt(point: Point) = vector(point.x, 0f, point.z)

}
