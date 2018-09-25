package raytracer.loader

import raytracer.renderer.*

data class Scene(val cameras: List<Camera>, val world: World)

class SceneBuilder {
    val cameras = ArrayList<Camera>()
    val lights = ArrayList<PointLight>()
    val shapes = ArrayList<Shape>()

    fun build(): Scene = Scene(
            cameras = cameras.toList(),
            world = World(lights = lights, objects = shapes))
}

@Suppress("UNCHECKED_CAST")
fun loadScene(loaded: Any): Scene {
    loaded as List<Map<String, *>>

    val builder = SceneBuilder()

    for (elem in loaded) {
        if (elem["add"] == "camera") {
            val hSize = readInt(elem["width"])
            val vSize = (elem["height"] as Number).toInt()
            val fieldOfView = (elem["field-of-view"] as Number).toFloat()
            val camera = Camera(hSize, vSize, fieldOfView)

            val from = readPoint(elem["from"])
            val to = readPoint(elem["to"])
            val up = readVector(elem["up"])

            camera.transform = viewTransform(from, to, up)

            builder.cameras.add(camera)
        }
    }
    return builder.build()
}

fun readInt(elem: Any?) = (elem as Number).toInt()

fun readFloat(elem: Any?) = (elem as Number).toFloat()

fun readPoint(elem: Any?): Point {
    val list = elem as List<*>
    return point(readFloat(elem[0]), readFloat(elem[1]), readFloat(elem[2]))
}

fun readVector(elem: Any?): Vector {
    val list = elem as List<*>
    return vector(readFloat(elem[0]), readFloat(elem[1]), readFloat(elem[2]))
}

fun readColor(elem: Any?): Vector {
    val list = elem as List<*>
    return color(readFloat(elem[0]), readFloat(elem[1]), readFloat(elem[2]))
}
