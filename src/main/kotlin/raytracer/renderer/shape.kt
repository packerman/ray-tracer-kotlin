package raytracer.renderer

import raytracer.math.*
import kotlin.math.sqrt

abstract class Shape {
    var transform: Matrix4 = Matrix4.IDENTITY
    var material: Material = Material()

    fun intersect(ray: Ray): List<Intersection> {
        val localRay = ray.transform(transform.inverse())
        return localIntersect(localRay)
    }

    protected abstract fun localIntersect(ray: Ray): List<Intersection>

    fun normalAt(point: Point): Vector {
        val localPoint = transform.inverse() * point
        val localNormal = localNormalAt(localPoint)
        val worldNormal = transform.inverse().transpose() * localNormal
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