package raytracer.renderer

class Group(private val shapes: MutableList<Shape> = mutableListOf()) : Shape(), Collection<Shape> by shapes {

    val children: List<Shape> = shapes

    override fun localIntersect(ray: Ray): List<Intersection> =
            shapes.flatMap { it.intersect(ray) }.sortedBy(Intersection::t)

    override fun localNormalAt(point: Point, hit: Intersection?): Vector {
        error("Not implemented")
    }

    fun addChild(shape: Shape) {
        shapes.add(shape)
        shape.parent = this
    }
}
