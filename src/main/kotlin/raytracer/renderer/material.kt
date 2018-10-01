package raytracer.renderer

import kotlin.math.pow

data class Material(val pattern: Pattern,
                    val ambient: Float = MaterialDefaults.ambient,
                    val diffuse: Float = MaterialDefaults.diffuse,
                    val specular: Float = MaterialDefaults.specular,
                    val shininess: Float = MaterialDefaults.shininess,
                    val reflective: Float = MaterialDefaults.reflective,
                    val transparency: Float = MaterialDefaults.transparency,
                    val refractiveIndex: Float = MaterialDefaults.refractiveIndex) {

    constructor(color: Color = color(1f, 1f, 1f),
                ambient: Float = MaterialDefaults.ambient,
                diffuse: Float = MaterialDefaults.diffuse,
                specular: Float = MaterialDefaults.specular,
                shininess: Float = MaterialDefaults.shininess,
                reflective: Float = MaterialDefaults.reflective,
                transparency: Float = MaterialDefaults.transparency,
                refractiveIndex: Float = MaterialDefaults.refractiveIndex) : this(SolidPattern(color), ambient, diffuse, specular, shininess, reflective, transparency, refractiveIndex)

    fun colorAt(shape: Shape, point: Point): Color = pattern.patternAtShape(shape, point)
}

object MaterialDefaults {
    const val ambient = 0.1f
    const val diffuse = 0.9f
    const val specular = 0.9f
    const val shininess = 200f
    const val reflective = 0f
    const val transparency = 0f
    const val refractiveIndex = 1f
}

fun Material.lighting(shape: Shape,
                      light: PointLight,
                      position: Point,
                      eyeVector: Vector,
                      normalVector: Vector,
                      inShadow: Boolean = false): Color {
    val effectiveColor = colorAt(shape, position) * light.intensity

    val ambient = effectiveColor * this.ambient
    if (inShadow) {
        return ambient
    }

    val lightVector = (light.position - position).normalize()
    val lightDotNormal = lightVector.dot(normalVector)
    val diffuse = if (lightDotNormal < 0f) black else effectiveColor * this.diffuse * lightDotNormal

    fun specularLightning(): Color {
        val reflectVector = (-lightVector).reflect(normalVector)
        val reflectDotEye = reflectVector.dot(eyeVector).pow(this.shininess)
        if (reflectDotEye < 0f) return black
        return light.intensity * this.specular * reflectDotEye
    }

    val specular = if (lightDotNormal < 0f) black else specularLightning()

    return ambient + diffuse + specular
}

object RefractiveIndex {
    const val vacuum = 1f
    const val air = 1.00029f
    const val water = 1.333f
    const val glass = 1.52f
    const val diamond = 2.417f
}
