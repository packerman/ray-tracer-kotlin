package raytracer.renderer

import kotlin.math.abs
import kotlin.math.sqrt

data class Bounds(val minimum: Point, val maximum: Point)

abstract class Shape {
    var transform: Matrix4 = Matrix4.identity
    var material: Material = Material()
    var parent: Shape? = null

    fun intersect(ray: Ray): List<Intersection> {
        val localRay = ray.transform(transform.inverse)
        return localIntersect(localRay)
    }

    protected abstract fun localIntersect(ray: Ray): List<Intersection>

    fun normalAt(worldPoint: Point): Vector {
        val localPoint = worldToObject(worldPoint)
        val localNormal = localNormalAt(localPoint)
        return normalToWorld(localNormal)
    }

    protected abstract fun localNormalAt(point: Point): Vector

    fun worldToObject(point: Point): Point {
        val parentPoint = this.parent?.worldToObject(point) ?: point
        return this.transform.inverse * parentPoint
    }

    fun normalToWorld(normal: Vector): Vector {
        val localNormal = this.transform.inverse.transpose
                .times3x3(normal)
                .normalize()
        return this.parent?.normalToWorld(localNormal) ?: localNormal
    }

    abstract fun bounds(): Bounds
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

        return intersections(i1, i2)
    }

    override fun localNormalAt(point: Point): Vector {
        return point - point(0f, 0f, 0f)
    }

    override fun bounds(): Bounds =
            Bounds(point(-1f, -1f, -1f), point(1f, 1f, 1f))
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

    override fun bounds(): Bounds =
            Bounds(point(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY),
                    point(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY))
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

    override fun bounds(): Bounds =
            Bounds(point(-1f, -1f, -1f),
                    point(1f, 1f, 1f))
}

class Cylinder(val minimum: Float = Float.NEGATIVE_INFINITY,
               val maximum: Float = Float.POSITIVE_INFINITY,
               val closed: Boolean = false) : Shape() {

    override fun localIntersect(ray: Ray): List<Intersection> {
        val xs = mutableListOf<Intersection>()

        val a = ray.direction.x * ray.direction.x + ray.direction.z * ray.direction.z
        if (a < epsilon) {
            return intersectCaps(ray, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, xs)
        }
        val b = 2 * ray.origin.x * ray.direction.x + 2 * ray.origin.z * ray.direction.z
        val c = ray.origin.x * ray.origin.x + ray.origin.z * ray.origin.z - 1
        val discriminant = b * b - 4 * a * c
        if (discriminant < 0) {
            return emptyList()
        }
        val t0 = (-b - sqrt(discriminant)) / (2 * a)
        val t1 = (-b + sqrt(discriminant)) / (2 * a)

        val y0 = ray.origin.y + t0 * ray.direction.y
        if (this.minimum < y0 && y0 < this.maximum) {
            xs.add(Intersection(t0, this))
        }
        val y1 = ray.origin.y + t1 * ray.direction.y
        if (this.minimum < y1 && y1 < this.maximum) {
            xs.add(Intersection(t1, this))
        }
        return intersectCaps(ray, y0, y1, xs)
    }

    override fun localNormalAt(point: Point): Vector {
        val dist = point.x * point.x + point.z * point.z
        return when {
            dist < 1f && point.y >= this.maximum - epsilon -> vector(0f, 1f, 0f)
            dist < 1f && point.y <= this.minimum + epsilon -> vector(0f, -1f, 0f)
            else -> vector(point.x, 0f, point.z)
        }
    }

    override fun bounds(): Bounds = Bounds(
            point(-1f, this.minimum, -1f),
            point(1f, this.maximum, 1f)
    )

    private fun intersectCaps(ray: Ray, y0: Float, y1: Float, xs: MutableList<Intersection>): MutableList<Intersection> {
        if (!this.closed || abs(ray.direction.y) < epsilon) {
            return xs
        }
        val yRange = if (y0 <= y1) y0..y1 else y1..y0
        if (this.minimum in yRange) {
            val t = (this.minimum - ray.origin.y) / ray.direction.y
            xs.add(Intersection(t, this))
        }
        if (this.maximum in yRange) {
            val t = (this.maximum - ray.origin.y) / ray.direction.y
            xs.add(Intersection(t, this))
        }
        return xs
    }

    private companion object {
        const val epsilon = 0.0001f
    }
}
