package raytracer.renderer

import java.util.*

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
               val n1: Float, val n2: Float, val underPoint: Point)

fun Intersection.prepareHit(ray: Ray, xs: List<Intersection> = listOf(this)): Hit {
    val point = ray.position(t)
    val normal = shape.normalAt(point)
    val eye = -ray.direction
    val inside = normal.dot(eye) < 0f

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

    val n = if (inside) -normal else normal

    return Hit(t = t, shape = shape,
            point = point + n * 0.0005f,
            eye = eye,
            normal = n,
            inside = inside,
            reflect = ray.direction.reflect(normal),
            n1 = n1,
            n2 = n2,
            underPoint = point - n * 0.0005f)
}
