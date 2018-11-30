package raytracer.loader

import org.snakeyaml.engine.v1.api.Load
import org.snakeyaml.engine.v1.api.LoadSettingsBuilder
import raytracer.renderer.*
import java.io.InputStream
import java.io.Reader
import java.lang.Math.toRadians

data class Scene(val cameras: List<Camera>, val world: World)

interface AngleUnit {
    fun toRadians(a: Float): Float
}

object Radians : AngleUnit {
    override fun toRadians(a: Float): Float = a
}

object Degrees : AngleUnit {
    override fun toRadians(a: Float): Float = toRadians(a.toDouble()).toFloat()
}


@Suppress("UNCHECKED_CAST")
class SceneLoader(val angleUnit: AngleUnit = Degrees) {

    private val defined = HashMap<String, Definition>()
    private val materials = HashMap<String, Material>()
    private val transforms = HashMap<String, Matrix4>()
    private val builder = SceneBuilder()

    fun load(loaded: Any): Scene {
        loaded as List<Map<String, *>>

        for (elem in loaded) {
            when {
                "add" in elem -> addElem(elem)
                "define" in elem -> defineElem(elem)
            }
        }
        return builder.build()
    }

    private fun addElem(elem: Map<String, *>) = when (elem["add"]) {
        "camera" -> {
            val hSize = readInt(elem["width"])
            val vSize = (elem["height"] as Number).toInt()
            val fieldOfView = toRadians((elem["field-of-view"] as Number).toDouble()).toFloat()
            val camera = Camera(hSize, vSize, fieldOfView)

            val from = readPoint(elem["from"])
            val to = readPoint(elem["to"])
            val up = readVector(elem["up"])

            camera.transform = viewTransform(from, to, up)

            builder.cameras.add(camera)
        }
        "light" -> {
            val position = readPoint(elem["at"])
            val intensity = readColor(elem["intensity"])
            builder.lights.add(PointLight(position, intensity))
        }
        "plane" -> {
            val shape = Plane()
            setObjectProperties(shape, elem)
            builder.shapes.add(shape)
        }
        "sphere" -> {
            val shape = Sphere()
            setObjectProperties(shape, elem)
            builder.shapes.add(shape)
        }
        "cube" -> {
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
            else -> if (elem["material"] != null) error("Material")
        }
        shape.transform = readTransformList(elem["transform"])
    }

    private val materialProperties = setOf("color", "pattern", "ambient", "diffuse", "specular", "reflective", "shininess", "transparency", "refractive-index")

    private fun readMaterial(elem: Any?, baseMaterial: Material? = null): Material {
        val material = elem as Map<String, *>
        check(materialProperties.containsAll(material.keys))
        return Material(
                pattern = readColorOrPattern(material, baseMaterial),
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

    private fun readColorOrPattern(material: Map<String, *>, baseMaterial: Material?): Pattern =
            material["color"]?.let { SolidPattern(readColor(it)) }
                    ?: material["pattern"]?.let { readPattern(it) }
                    ?: baseMaterial?.pattern
                    ?: SolidPattern(color(1f, 1f, 1f))

    private fun readPattern(elem: Any): Pattern {
        val map = elem as Map<String, *>
        val type = map["type"] as String
        val pattern = when (type) {
            "stripes" -> {
                val colors = map["colors"] as List<*>
                StripePattern(readColor(colors[0]), readColor(colors[1]))
            }
            "checkers" -> {
                val colors = map["colors"] as List<*>
                CheckerPattern(readColor(colors[0]), readColor(colors[1]))
            }
            else -> error("Unknown pattern type: $type")
        }
        pattern.transform = readTransformList(map["transform"])
        return pattern
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
            "rotate-x" -> rotationX(angleUnit.toRadians(readFloat(list[1])))
            "rotate-y" -> rotationY(angleUnit.toRadians(readFloat(list[1])))
            "rotate-z" -> rotationZ(angleUnit.toRadians(readFloat(list[1])))
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

    companion object {
        fun loadFromString(string: String,
                           angleUnit: AngleUnit = Degrees): Scene {
            val load = Load(settings)
            val loaded = load.loadFromString(string)
            return SceneLoader(angleUnit).load(loaded)
        }

        fun loadFromInputStream(inputStream: InputStream): Scene {
            val load = Load(settings)
            val loaded = load.loadFromInputStream(inputStream)
            return SceneLoader().load(loaded)
        }

        fun loadFromReader(reader: Reader): Scene {
            val load = Load(settings)
            val loaded = load.loadFromReader(reader)
            return SceneLoader().load(loaded)
        }

        private val settings = LoadSettingsBuilder().build()
    }
}

private class SceneBuilder {
    val cameras = ArrayList<Camera>()
    val lights = ArrayList<PointLight>()
    val shapes = ArrayList<Shape>()

    fun build(): Scene = Scene(
            cameras = cameras.toList(),
            world = World(lights = lights, objects = shapes))
}

private data class Definition(val value: Any, val extend: String?)
