package raytracer.renderer

class Triangle(val p1: Point, val p2: Point, val p3: Point) : Shape() {

    val e1 = p2 - p1
    val e2 = p3 - p1
    val normal = (e2 cross e1).normalize()

    override fun localIntersect(ray: Ray): List<Intersection> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun localNormalAt(point: Point): Vector = normal
}
