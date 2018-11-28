package raytracer.renderer

import kotlin.math.abs

data class Triangle(val p1: Point, val p2: Point, val p3: Point) : Shape() {

    val e1 = p2 - p1
    val e2 = p3 - p1
    val normal = (e2 cross e1).normalize()

    override fun localIntersect(ray: Ray): List<Intersection> {
        val dirCrossE2 = ray.direction cross this.e2
        val det = this.e1 dot dirCrossE2
        if (abs(det) < epsilon) {
            return emptyList()
        }
        val f = 1f / det
        val p1ToOrigin = ray.origin - this.p1
        val u = f * (p1ToOrigin dot dirCrossE2)
        if (u !in 0f..1f) {
            return emptyList()
        }
        val originCrossE1 = p1ToOrigin cross this.e1
        val v = f * (ray.direction dot originCrossE1)
        if (v < 0 || (u + v) > 1) {
            return emptyList()
        }
        val t = f * (this.e2 dot originCrossE1)
        return listOf(Intersection(t, this))
    }

    override fun localNormalAt(point: Point): Vector = normal

    private companion object {
        const val epsilon = 0.00001f
    }
}
