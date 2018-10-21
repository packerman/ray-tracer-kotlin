package raytracer.renderer

class Group(private val shapes: MutableList<Shape> = mutableListOf()) : Shape(), Collection<Shape> by shapes {

    override fun localIntersect(ray: Ray): List<Intersection> =
            shapes.flatMap { it.intersect(ray) }.sortedBy(Intersection::t)

    override fun localNormalAt(point: Point): Vector {
        error("Not implemented")
    }

    fun addChild(shape: Shape) {
        shapes.add(shape)
        shape.parent = this
    }

    override fun bounds(): Bounds {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
