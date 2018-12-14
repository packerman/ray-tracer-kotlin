package raytracer.renderer

import kotlin.math.abs

data class Triangle(val p1: Point, val p2: Point, val p3: Point) : Shape() {

    val e1 = p2 - p1
    val e2 = p3 - p1
    val normal = (e2 cross e1).normalize()

    override fun localIntersect(ray: Ray): List<Intersection> {
        return intersection(this, ray, p1, e1, e2)
    }

    override fun localNormalAt(point: Point): Vector = normal

    companion object {
        private const val epsilon = 0.00001f

        fun intersection(shape: Shape, ray: Ray, p1: Point, e1: Vector, e2: Vector): List<Intersection> {
            val dirCrossE2 = ray.direction cross e2
            val det = e1 dot dirCrossE2
            if (abs(det) < epsilon) {
                return emptyList()
            }
            val f = 1f / det
            val p1ToOrigin = ray.origin - p1
            val u = f * (p1ToOrigin dot dirCrossE2)
            if (u !in 0f..1f) {
                return emptyList()
            }
            val originCrossE1 = p1ToOrigin cross e1
            val v = f * (ray.direction dot originCrossE1)
            if (v < 0 || (u + v) > 1) {
                return emptyList()
            }
            val t = f * (e2 dot originCrossE1)
            return listOf(Intersection(t, shape, u, v))
        }
    }
}

data class SmoothTriangle(val p1: Point, val p2: Point, val p3: Point,
                          val n1: Vector, val n2: Vector, val n3: Vector) : Shape() {

    private val e1 = p2 - p1
    private val e2 = p3 - p1

    override fun localIntersect(ray: Ray): List<Intersection> {
        return Triangle.intersection(this, ray, p1, e1, e2)
    }

    override fun localNormalAt(point: Point): Vector {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
