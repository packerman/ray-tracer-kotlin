package raytracer.renderer

import raytracer.math.*
import kotlin.math.sqrt

class Sphere {
    var transform: Matrix4 = Matrix4.IDENTITY
    var material: Material = Material()

    fun intersect(ray: Ray): List<Intersection> {
        val ray2 = ray.transform(this.transform.inverse())

        val sphereToRay = ray2.origin - point(0f, 0f, 0f)

        val a = ray2.direction dot ray2.direction
        val b = (ray2.direction dot sphereToRay) * 2f
        val c = (sphereToRay dot sphereToRay) - 1f

        val discriminant = b * b - 4f * a * c
        if (discriminant < 0f) {
            return intersections()
        }

        val i1 = Intersection((-b - sqrt(discriminant)) / (2f * a), this)
        val i2 = Intersection((-b + sqrt(discriminant)) / (2f * a), this)

        return if (i1.t > i2.t) intersections(i2, i1) else intersections(i1, i2)
    }

    fun normalAt(p: Point): Vector {
        val inversed = transform.inverse()
        val objectPoint = inversed * p
        val objectNormal = objectPoint - point(0f, 0f, 0f)
        val worldNormal = inversed.transpose() * objectNormal
        return worldNormal.normalize()
    }
}
