package raytracer.renderer


data class Bounds(val minimum: Point, val maximum: Point) {

    fun transform(matrix: Matrix4): Bounds {

        val p0 = matrix.timesSpecial(minimum)
        val p1 = matrix.timesSpecial(raytracer.renderer.point(maximum.x, minimum.y, minimum.z))
        val p2 = matrix.timesSpecial(raytracer.renderer.point(minimum.x, maximum.y, maximum.z))

        val p3 = matrix.timesSpecial(raytracer.renderer.point(minimum.x, maximum.y, minimum.z))
        val p4 = matrix.timesSpecial(raytracer.renderer.point(maximum.x, minimum.y, maximum.z))

        val p5 = matrix.timesSpecial(raytracer.renderer.point(minimum.x, minimum.y, maximum.z))
        val p6 = matrix.timesSpecial(raytracer.renderer.point(maximum.x, maximum.y, minimum.z))

        val p7 = matrix.timesSpecial(maximum)

        return raytracer.renderer.Bounds(
                raytracer.renderer.point(minOf(p0.x, p1.x, p2.x, p3.x, p4.x, p5.x, p6.x, p7.x),
                        minOf(p0.y, p1.y, p2.y, p3.y, p4.y, p5.y, p6.y, p7.y),
                        minOf(p0.z, p1.z, p2.z, p3.z, p4.z, p5.z, p6.z, p7.z)),
                raytracer.renderer.point(maxOf(p0.x, p1.x, p2.x, p3.x, p4.x, p5.x, p6.x, p7.x),
                        maxOf(p0.y, p1.y, p2.y, p3.y, p4.y, p5.y, p6.y, p7.y),
                        maxOf(p0.z, p1.z, p2.z, p3.z, p4.z, p5.z, p6.z, p7.z)))
    }

    private companion object {
        fun min2(a: Float, b: Float) = when {
            a.isNaN() || b.isNaN() -> Float.NEGATIVE_INFINITY
            else -> minOf(a, b)
        }

        fun max2(a: Float, b: Float) = when {
            a.isNaN() || b.isNaN() -> Float.POSITIVE_INFINITY
            else -> maxOf(a, b)
        }

        fun minOf(a: Float, b: Float, c: Float, d: Float, e: Float, f: Float, g: Float, h: Float) =
                min2(a, min2(b, min2(c, min2(d, min2(e, min2(f, min2(g, h)))))))

        fun maxOf(a: Float, b: Float, c: Float, d: Float, e: Float, f: Float, g: Float, h: Float) =
                max2(a, max2(b, max2(c, max2(d, max2(e, max2(f, max2(g, h)))))))
    }
}

