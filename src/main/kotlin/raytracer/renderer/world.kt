package raytracer.renderer

import kotlin.math.sqrt

class World(var light: PointLight? = null, objects: List<Shape> = emptyList()) : List<Shape> by objects {

    fun intersect(ray: Ray): List<Intersection> =
            asSequence()
                    .flatMap { s -> s.intersect(ray).asSequence() }
                    .sortedBy { i -> i.t }
                    .toList()
}

fun defaultWorld() = World(light = PointLight(position = point(-10f, 10f, -10f), intensity = color(1f, 1f, 1f)),
        objects = listOf(
                Sphere().apply {
                    material = Material(color = color(0.8f, 1.0f, 0.6f), diffuse = 0.7f, specular = 0.2f)
                },
                Sphere().apply {
                    transform = scaling(0.5f, 0.5f, 0.5f)
                }))

private const val maxRecursiveDepth = 5

fun World.shadeHit(hit: Hit, remaining: Int = maxRecursiveDepth): Color {
    val shadowed = isShadowed(hit.point)

    val surface = hit.shape.material.lighting(hit.shape,
            requireNotNull(light),
            hit.point,
            hit.eye,
            hit.normal,
            shadowed)

    val reflected = reflectedColor(hit, remaining)

    val refracted = refractedColor(hit, remaining)

    return surface + reflected + refracted
}

fun World.colorAt(ray: Ray, remaining: Int = maxRecursiveDepth): Color {
    val intersections = intersect(ray)
    val i = intersections.hit()
    return if (i == null) {
        black
    } else {
        val hit = i.prepareHit(ray, intersections)
        this.shadeHit(hit, remaining)
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

fun World.reflectedColor(hit: Hit, remaining: Int = maxRecursiveDepth): Color {
    if (remaining < 1) {
        return black
    }
    if (hit.shape.material.reflective == 0f) {
        return black
    }
    val reflectRay = Ray(hit.point, hit.reflect)
    val color = colorAt(reflectRay, remaining - 1)
    return color * hit.shape.material.reflective
}

fun World.refractedColor(hit: Hit, remaining: Int): Color {
    if (remaining < 1) {
        return black
    }
    if (hit.shape.material.transparency == 0f) {
        return black
    }
    val nRatio = hit.n1 / hit.n2
    val cos_i = hit.eye dot hit.normal
    val sin2_t = nRatio * nRatio * (1 - cos_i * cos_i)
    if (sin2_t > 1f) {
        return black
    }
    val cos_t = sqrt(1f - sin2_t)
    val direction = hit.normal * (nRatio * cos_i - cos_t) - hit.eye * nRatio
    val refractedRay = Ray(hit.underPoint, direction)
    return colorAt(refractedRay, remaining - 1) * hit.shape.material.transparency
}
