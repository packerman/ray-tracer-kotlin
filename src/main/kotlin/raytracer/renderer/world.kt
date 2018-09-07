package raytracer.renderer

class World(var light: PointLight? = null, objects: Collection<Shape> = emptySet()) : Collection<Shape> by objects {

    fun intersect(ray: Ray): List<Intersection> =
            asSequence()
                    .flatMap { s -> s.intersect(ray).asSequence() }
                    .sortedBy { i -> i.t }
                    .toList()
}

fun defaultWorld() = World(light = PointLight(position = point(-10f, 10f, -10f), intensity = color(1f, 1f, 1f)),
        objects = setOf(
                Sphere().apply {
                    material = ColorMaterial(color = color(0.8f, 1.0f, 0.6f), diffuse = 0.7f, specular = 0.2f)
                },
                Sphere().apply {
                    transform = scaling(0.5f, 0.5f, 0.5f)
                }))

fun World.shadeHit(hit: Hit): Color =
        hit.shape.material.lighting(hit.shape,
                requireNotNull(light),
                hit.point,
                hit.eye,
                hit.normal,
                isShadowed(hit.point))

fun World.colorAt(ray: Ray): Color {
    val intersections = intersect(ray)
    val i = intersections.hit()
    return if (i == null) {
        black
    } else {
        val hit = i.prepareHit(ray)
        this.shadeHit(hit)
    }
}

fun World.isShadowed(point: Tuple): Boolean {
    val lightPosition = light?.position ?: return false
    val v = lightPosition - point
    val distance = v.length
    val ray = Ray(point, v.normalize())
    val ise = intersect(ray)
    val hit = ise.hit()
    return hit != null && hit.t < distance
}
