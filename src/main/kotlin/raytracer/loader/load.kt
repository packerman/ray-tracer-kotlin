package raytracer.loader

import raytracer.renderer.*
import java.lang.Math.toRadians

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
        } else if (elem["add"] == "light") {
            val position = readPoint(elem["at"])
            val intensity = readColor(elem["intensity"])
            builder.lights.add(PointLight(position, intensity))
        } else if (elem["add"] == "plane") {
            val shape = Plane()
            setObjectProperties(shape, elem)
            builder.shapes.add(shape)
        } else if (elem["add"] == "sphere") {
            val shape = Sphere()
            setObjectProperties(shape, elem)
            builder.shapes.add(shape)
        } else if (elem["add"] == "cube") {
            val shape = Cube()
            setObjectProperties(shape, elem)
            builder.shapes.add(shape)
        }
    }
    return builder.build()
}

fun setObjectProperties(shape: Shape, elem: Map<String, *>) {
    @Suppress("UNCHECKED_CAST") val material = elem["material"] as Map<String, *>
    shape.material = Material(
            color = readColor(material["color"]),
            ambient = readFloat(material["ambient"]),
            diffuse = readFloat(material["diffuse"]),
            specular = readFloat(material["specular"])
    )
    shape.transform = readTransforms(elem["transform"])
}

fun readInt(elem: Any?) = (elem as Number).toInt()

fun readFloat(elem: Any?) = (elem as Number).toFloat()

fun readPoint(elem: Any?): Point {
    val list = elem as List<*>
    require(list.size == 3)
    return point(readFloat(list[0]), readFloat(list[1]), readFloat(list[2]))
}

fun readVector(elem: Any?): Vector {
    val list = elem as List<*>
    require(list.size == 3)
    return vector(readFloat(list[0]), readFloat(list[1]), readFloat(list[2]))
}

fun readColor(elem: Any?): Vector {
    val list = elem as List<*>
    require(list.size == 3)
    return color(readFloat(list[0]), readFloat(list[1]), readFloat(list[2]))
}

fun readTransform(elem: Any?): Matrix4 {
    val list = elem as List<*>
    val name = list[0] as String
    if (name == "rotate-x") {
        return rotationX(toRadians(readFloat(list[1]).toDouble()).toFloat())
    } else if (name == "translate") {
        return translation(readFloat(list[1]), readFloat(list[2]), readFloat(list[3]))
    }
    TODO()
}

fun readTransforms(elem: Any?): Matrix4 {
    var transform = Matrix4.identity
    val transforms = elem as List<*>

    transforms.map(::readTransform).forEach {
        transform = it * transform
    }
    return transform
}
