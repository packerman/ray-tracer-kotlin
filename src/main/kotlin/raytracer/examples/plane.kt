package raytracer.examples

import raytracer.renderer.*
import kotlin.math.PI

fun main(args: Array<String>) {

    val floor = Plane().apply {
        transform = scaling(10f, 0.01f, 10f)
        material = ColorMaterial(
                color = color(1f, 0.9f, 0.9f),
                specular = 0f)
    }

    val wall = Plane().apply {
        transform = translation(0f, 0f, 20f) *
                rotationX((PI / 2).toFloat())
        material = floor.material
    }

    val middle = Sphere().apply {
        transform = translation(-0.5f, 1f, 0.5f)
        material = ColorMaterial(
                color = color(0.1f, 1f, 0.5f),
                diffuse = 0.7f,
                specular = 0.3f
        )
    }

    val right = Sphere().apply {
        transform = translation(1.5f, 0.5f, -0.5f) * scaling(0.5f, 0.5f, 0.5f)
        material = ColorMaterial(
                color = color(0.5f, 1f, 0.1f),
                diffuse = 0.7f,
                specular = 0.3f
        )
    }

    val left = Sphere().apply {
        transform = translation(-1.5f, 0.33f, -0.75f) * scaling(0.33f, 0.33f, 0.33f)
        material = ColorMaterial(
                color = color(1f, 0.8f, 0.1f),
                diffuse = 0.7f,
                specular = 0.3f
        )
    }

    val world = World(
            light = PointLight(
                    point(-10f, 10f, -10f), color(1f, 1f, 1f)
            ),
            objects = setOf(floor, wall,
                    middle, right, left))

    val camera = Camera(100, 50, (PI / 3).toFloat())
    camera.transform = viewTransform(point(0f, 1.5f, -5f), point(0f, 1f, 0f), vector(0f, 1f, 0f))

    val image = camera.render(world)

    image.saveToFile("plane.ppm")
}
