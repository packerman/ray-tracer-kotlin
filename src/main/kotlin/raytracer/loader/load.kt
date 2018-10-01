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
class Loader {

    private val defined = HashMap<String, Any>()
    private val materials = HashMap<String, Material>()
    private val builder = SceneBuilder()

    fun loadScene(loaded: Any): Scene {
        loaded as List<Map<String, *>>

        for (elem in loaded) {
            when {
                "add" in elem -> addElem(elem)
                "define" in elem -> defineElem(elem)
            }
        }
        return builder.build()
    }

    private fun addElem(elem: Map<String, *>) = when {
        elem["add"] == "camera" -> {
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
        elem["add"] == "light" -> {
            val position = readPoint(elem["at"])
            val intensity = readColor(elem["intensity"])
            builder.lights.add(PointLight(position, intensity))
        }
        elem["add"] == "plane" -> {
            val shape = Plane()
            setObjectProperties(shape, elem)
            builder.shapes.add(shape)
        }
        elem["add"] == "sphere" -> {
            val shape = Sphere()
            setObjectProperties(shape, elem)
            builder.shapes.add(shape)
        }
        elem["add"] == "cube" -> {
            val shape = Cube()
            setObjectProperties(shape, elem)
            builder.shapes.add(shape)
        }
        else -> error("Unknown element: ${elem["add"]}")
    }

    private fun defineElem(elem: Map<String, *>) {
        val name = elem["define"] as String
        val value = requireNotNull(elem["value"])
        defined[name] = value
    }

    private fun setObjectProperties(shape: Shape, elem: Map<String, *>) {
        when (elem["material"]) {
            is String -> {
                val name = elem["material"] as String
                shape.material = materials.getOrPut(name) { readMaterial(defined[name]) }
            }
            is Map<*, *> -> {
                shape.material = readMaterial(elem["material"])
            }
        }
        shape.transform = readTransforms(elem["transform"])
    }

    private fun readInt(elem: Any?) = (elem as Number).toInt()

    private fun readFloat(elem: Any?) = (elem as Number).toFloat()

    private fun readPoint(elem: Any?): Point {
        val list = elem as List<*>
        require(list.size == 3)
        return point(readFloat(list[0]), readFloat(list[1]), readFloat(list[2]))
    }

    private fun readVector(elem: Any?): Vector {
        val list = elem as List<*>
        require(list.size == 3)
        return vector(readFloat(list[0]), readFloat(list[1]), readFloat(list[2]))
    }

    private fun readColor(elem: Any?): Color {
        val list = elem as List<*>
        require(list.size == 3)
        return color(readFloat(list[0]), readFloat(list[1]), readFloat(list[2]))
    }

    private fun readTransform(elem: Any?): Matrix4 {
        val list = elem as List<*>
        val name = list[0] as String
        if (name == "rotate-x") {
            return rotationX(toRadians(readFloat(list[1]).toDouble()).toFloat())
        } else if (name == "translate") {
            return translation(readFloat(list[1]), readFloat(list[2]), readFloat(list[3]))
        }
        TODO()
    }

    private fun readTransforms(elem: Any?): Matrix4 {
        var transform = Matrix4.identity
        val transforms = (elem as? List<*>) ?: emptyList<Any>()

        transforms.map(::readTransform).forEach {
            transform = it * transform
        }
        return transform
    }

    private fun readMaterial(elem: Any?): Material {
        val material = elem as Map<String, *>
        return Material(
                color = material["color"]?.let(::readColor) ?: color(1f, 1f, 1f),
                ambient = material["ambient"]?.let(::readFloat) ?: MaterialDefaults.ambient,
                diffuse = material["diffuse"]?.let(::readFloat) ?: MaterialDefaults.diffuse,
                specular = material["specular"]?.let(::readFloat) ?: MaterialDefaults.specular,
                reflective = material["reflective"]?.let(::readFloat) ?: MaterialDefaults.reflective
        )
    }
}
