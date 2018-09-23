package raytracer.renderer

import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

data class Ray(val origin: Point, val direction: Vector) {
    fun position(t: Float): Point = origin + direction * t
}

fun Ray.transform(m: Matrix4): Ray = Ray(m * origin, m * direction)

data class Intersection(val t: Float, val shape: Shape)

fun intersections(vararg i: Intersection) = listOf(*i)

fun List<Intersection>.hit(): Intersection? {
    return this.asSequence()
            .filter { i -> i.t > 0f }
            .minBy(Intersection::t)
}

data class Hit(val t: Float, val shape: Shape,
               val point: Point, val eye: Vector, val normal: Vector,
               val inside: Boolean,
               val reflect: Vector,
               val n1: Float, val n2: Float, val underPoint: Point) {
}

fun Intersection.prepareHit(ray: Ray, xs: List<Intersection> = listOf(this)): Hit {
    val point = ray.position(t)
    val normalAtPoint = shape.normalAt(point)
    val eye = -ray.direction
    val inside = normalAtPoint.dot(eye) < 0f
    val normal = if (inside) -normalAtPoint else normalAtPoint

    val (n1, n2) = findRefractiveIndex(xs)

    return Hit(t = t, shape = shape,
            point = point + normal * 0.0005f,
            eye = eye,
            normal = normal,
            inside = inside,
            reflect = ray.direction.reflect(normalAtPoint),
            n1 = n1,
            n2 = n2,
            underPoint = point - normal * 0.0005f)
}

private fun Intersection.findRefractiveIndex(xs: List<Intersection>): Pair<Float, Float> {
    var n1 = 1f
    var n2 = 1f
    val containers = LinkedList<Shape>()

    for (i in xs) {
        if (this === i) {
            if (!containers.isEmpty()) {
                n1 = containers.last.material.refractiveIndex
            }
        }

        if (!containers.removeLastOccurrence(i.shape)) {
            containers.add(i.shape)
        }

        if (this === i) {
            if (!containers.isEmpty()) {
                n2 = containers.last.material.refractiveIndex
            }
            break
        }
    }
    return Pair(n1, n2)
}

fun Hit.schlick(): Float {
    var cos = eye dot normal
    if (n1 > n2) {
        val n = n1 / n2
        val sin2_t = n * n * (1f - cos * cos)
        if (sin2_t > 1f) return 1f
        val cos_t = sqrt(1f - sin2_t)
        cos = cos_t
    }
    val r2 = (n1 - n2) / (n1 + n2)
    val r0 = r2 * r2
    return r0 + (1 - r0) * (1 - cos).pow(5)
}
