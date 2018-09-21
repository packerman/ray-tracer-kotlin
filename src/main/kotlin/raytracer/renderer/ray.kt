package raytracer.renderer

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
               val reflect: Vector)

fun Intersection.prepareHit(ray: Ray): Hit {
    val point = ray.position(t)
    val normal = shape.normalAt(point)
    val offsetPoint = point + normal * 0.0005f
    val eye = -ray.direction
    val inside = normal.dot(eye) < 0f
    return Hit(t = t, shape = shape,
            point = offsetPoint,
            eye = eye,
            normal = if (inside) -normal else normal,
            inside = inside,
            reflect = ray.direction.reflect(normal))
}
