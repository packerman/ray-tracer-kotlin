package raytracer.renderer

import kotlin.math.pow

data class Material(val pattern: Pattern,
                    val ambient: Float = defaultAmbient,
                    val diffuse: Float = defaultDiffuse,
                    val specular: Float = defaultSpecular,
                    val shininess: Float = defaultShininess,
                    val reflective: Float = defaultReflective,
                    val transparency: Float = defaultTransparency,
                    val refractiveIndex: Float = defaultRefractiveIndex) {

    constructor(color: Color = color(1f, 1f, 1f),
                ambient: Float = defaultAmbient,
                diffuse: Float = defaultDiffuse,
                specular: Float = defaultSpecular,
                shininess: Float = defaultShininess,
                reflective: Float = defaultReflective,
                transparency: Float = defaultTransparency,
                refractiveIndex: Float = defaultRefractiveIndex) : this(SolidPattern(color), ambient, diffuse, specular, shininess, reflective, transparency, refractiveIndex)

    fun colorAt(shape: Shape, point: Point): Color = pattern.patternAtShape(shape, point)

    companion object {
        const val defaultAmbient = 0.1f
        const val defaultDiffuse = 0.9f
        const val defaultSpecular = 0.9f
        const val defaultShininess = 200f
        const val defaultReflective = 0f
        const val defaultTransparency = 0f
        const val defaultRefractiveIndex = 1f
    }
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
