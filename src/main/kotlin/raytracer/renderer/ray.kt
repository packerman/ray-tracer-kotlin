package raytracer.renderer

import raytracer.math.Matrix4
import raytracer.math.Point
import raytracer.math.Vector
import raytracer.math.times

data class Ray(val origin: Point, val direction: Vector) {
    fun position(t: Float): Point = origin + direction * t
}

fun Ray.transform(m: Matrix4): Ray = Ray(m * origin, m * direction)

data class Intersection(val t: Float, val obj: Sphere)

fun intersections(vararg i: Intersection) = listOf(*i)

fun List<Intersection>.hit(): Intersection? {
    return this.asSequence()
            .filter { i -> i.t > 0f }
            .minBy(Intersection::t)
}

data class Hit(val t: Float, val obj: Sphere,
               val point: Point, val eye: Vector, val normal: Vector,
               val inside: Boolean)

fun prepareHit(i: Intersection, r: Ray): Hit {
    val point = r.position(i.t)
    val normal = i.obj.normalAt(point)
    val offsetPoint = point + normal * 0.0005f
    val eye = -r.direction
    val inside = normal.dot(eye) < 0f
    return Hit(t = i.t, obj = i.obj,
            point = offsetPoint,
            eye = eye,
            normal = if (inside) -normal else normal,
            inside = inside)
}
