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

data class Definition(val value: Any, val extend: String?)

@Suppress("UNCHECKED_CAST")
class Loader {

    private val defined = HashMap<String, Definition>()
    private val materials = HashMap<String, Material>()
    private val transforms = HashMap<String, Matrix4>()
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
        val extend = elem["extend"] as String?
        defined[name] = Definition(value, extend)
    }

    private fun setObjectProperties(shape: Shape, elem: Map<String, *>) {
        when (elem["material"]) {
            is String -> {
                val name = elem["material"] as String
                shape.material = resolveMaterial(name)
            }
            is Map<*, *> -> {
                shape.material = readMaterial(elem["material"])
            }
            else -> error("Material")
        }
        shape.transform = readTransformList(elem["transform"])
    }

    private val materialProperties = setOf("color", "ambient", "diffuse", "specular", "reflective", "shininess", "transparency", "refractive-index")

    private fun readMaterial(elem: Any?, baseMaterial: Material? = null): Material {
        val material = elem as Map<String, *>
        check(materialProperties.containsAll(material.keys))
        return Material(
                pattern = material["color"]?.let { SolidPattern(readColor(it)) } ?: baseMaterial?.pattern
                ?: SolidPattern(color(1f, 1f, 1f)),
                ambient = material["ambient"]?.let(::readFloat) ?: baseMaterial?.ambient ?: MaterialDefaults.ambient,
                diffuse = material["diffuse"]?.let(::readFloat) ?: baseMaterial?.diffuse ?: MaterialDefaults.diffuse,
                specular = material["specular"]?.let(::readFloat) ?: baseMaterial?.specular
                ?: MaterialDefaults.specular,
                reflective = material["reflective"]?.let(::readFloat) ?: baseMaterial?.reflective
                ?: MaterialDefaults.reflective,
                shininess = material["shininess"]?.let(::readFloat) ?: baseMaterial?.shininess
                ?: MaterialDefaults.shininess,
                transparency = material["transparency"]?.let(::readFloat) ?: baseMaterial?.transparency
                ?: MaterialDefaults.transparency,
                refractiveIndex = material["refractive-index"]?.let(::readFloat) ?: baseMaterial?.refractiveIndex
                ?: MaterialDefaults.refractiveIndex
        )
    }

    private fun resolveMaterial(name: String): Material {
        val definedMaterial = materials[name]
        if (definedMaterial != null) return definedMaterial
        val definition = requireNotNull(defined[name]) { "Material '$name' not found" }
        val baseMaterial = definition.extend?.let { extend -> requireNotNull(resolveMaterial(extend)) }
        val createdMaterial = readMaterial(definition.value, baseMaterial)
        materials[name] = createdMaterial
        return createdMaterial
    }

    private fun resolveTransform(name: String): Matrix4 {
        val definedTransform = transforms[name]
        if (definedTransform != null) return definedTransform
        val definition = requireNotNull(defined[name]) { "Transform '$name' not found" }
        val createdTransform = readTransformList(definition.value)
        transforms[name] = createdTransform
        return createdTransform
    }

    private fun readTransformList(elem: Any?): Matrix4 {
        var transform = Matrix4.identity
        val transforms = if (elem == null) emptyList() else elem as List<Any>

        transforms.map(::readTransform).forEach {
            transform = it * transform
        }
        return transform
    }

    private fun readTransform(elem: Any?): Matrix4 {
        if (elem is String) {
            return resolveTransform(elem)
        }
        val list = elem as List<*>
        val name = list[0] as String
        return when (name) {
            "rotate-x" -> rotationX(toRadians(readFloat(list[1]).toDouble()).toFloat())
            "rotate-y" -> rotationY(toRadians(readFloat(list[1]).toDouble()).toFloat())
            "rotate-z" -> rotationZ(toRadians(readFloat(list[1]).toDouble()).toFloat())
            "translate" -> translation(readFloat(list[1]), readFloat(list[2]), readFloat(list[3]))
            "scale" -> scaling(readFloat(list[1]), readFloat(list[2]), readFloat(list[3]))
            else -> error("Unknown transform")
        }
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
}
