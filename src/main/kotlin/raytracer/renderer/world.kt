package raytracer.renderer

import raytracer.math.*

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

fun shadeHit(world: World, hit: Hit): Color =
        lighting(hit.obj.material,
                requireNotNull(world.light),
                hit.point,
                hit.eye,
                hit.normal,
                world.isShadowed(hit.point))

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

fun render(camera: Camera, world: World): Canvas {
    val image = Canvas(camera.hSize, camera.vSize)

    for (y in 0 until camera.vSize) {
        for (x in 0 until camera.hSize) {
            val ray = camera.rayForPixel(x, y)
            val color = world.colorAt(ray)
            image.writePixel(x, y, color)
        }
    }
    return image
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
