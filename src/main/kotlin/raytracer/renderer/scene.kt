package raytracer.renderer

import raytracer.math.*

class World(var light: PointLight? = null, objects: Collection<Sphere> = emptySet()) : Collection<Sphere> by objects {

    fun intersect(ray: Ray): List<Intersection> =
            asSequence()
                    .flatMap { s -> s.intersect(ray).asSequence() }
                    .sortedBy { i -> i.t }
                    .toList()
}

fun defaultWorld() = World(light = PointLight(position = point(-10f, 10f, -10f), intensity = color(1f, 1f, 1f)),
        objects = setOf(
                Sphere().apply {
                    material = Material(color = color(0.8f, 1.0f, 0.6f), diffuse = 0.7f, specular = 0.2f)
                },
                Sphere().apply {
                    transform = scaling(0.5f, 0.5f, 0.5f)
                }))

fun shadeHit(world: World, hit: Hit): Color =
        lighting(hit.obj.material,
                requireNotNull(world.light),
                hit.point,
                hit.eye,
                hit.normal)

fun World.colorAt(ray: Ray): Color {
    val intersections = intersect(ray)
    val i = intersections.hit()
    return if (i == null) {
        black
    } else {
        val hit = prepareHit(i, ray)
        shadeHit(this, hit)
    }
}
