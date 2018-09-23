package raytracer.examples

import raytracer.renderer.*
import kotlin.math.PI

fun main(args: Array<String>) {

    val floor = Plane().apply {
        material = Material(
                pattern = CheckerPattern(color(1f, 0.9f, 0.9f), color(0f, 0.1f, 0.1f)),
                reflective = 0.5f,
                specular = 0f)
    }

    val wall = Plane().apply {
        transform = translation(0f, 0f, 10f) *
                rotationX((PI / 2).toFloat())
        material = floor.material
    }

    val middle = Sphere().apply {
        transform = translation(-0.5f, 1f, 0.5f)
        material = Material(
                color = color(0.1f, 1f, 0.5f),
                diffuse = 0.7f,
                specular = 0.3f,
                reflective = 0.5f
        )
    }

    val right = Sphere().apply {
        transform = translation(1.5f, 0.5f, -0.5f) * scaling(0.5f, 0.5f, 0.5f)
        material = Material(
                color = color(0.5f, 1f, 0.1f),
                diffuse = 0.7f,
                specular = 0.3f,
                reflective = 0.75f
        )
    }

    val left = Sphere().apply {
        transform = translation(-1.5f, 0.33f, -0.75f) * scaling(0.33f, 0.33f, 0.33f)
        material = Material(
                color = color(1f, 0.8f, 0.1f),
                diffuse = 0.7f,
                specular = 0.3f,
                reflective = 1f
        )
    }

    val world = World(
            light = PointLight(
                    point(-10f, 10f, -10f), color(1f, 1f, 1f)
            ),
            objects = listOf(floor, wall,
                    middle, right, left))

    val camera = Camera(1200, 600, (PI / 3).toFloat())
    camera.transform = viewTransform(point(0f, 1.5f, -5f), point(0f, 1f, 0f), vector(0f, 1f, 0f))

    val image = camera.render(world)

    image.saveToFile("reflective.ppm")
}
