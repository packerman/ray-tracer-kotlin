package raytracer.renderer

import kotlin.math.pow

data class Material(val pattern: Pattern,
                    val ambient: Float = 0.1f,
                    val diffuse: Float = 0.9f,
                    val specular: Float = 0.9f,
                    val shininess: Float = 200f,
                    val reflective: Float = 0f) {


    constructor(color: Color = color(1f, 1f, 1f),
                ambient: Float = 0.1f,
                diffuse: Float = 0.9f,
                specular: Float = 0.9f,
                shininess: Float = 200f,
                reflective: Float = 0f) : this(SolidPattern(color), ambient, diffuse, specular, shininess, reflective)

    fun colorAt(shape: Shape, point: Point): Color = pattern.patternAtShape(shape, point)
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
