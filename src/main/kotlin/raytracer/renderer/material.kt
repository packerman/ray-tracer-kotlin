package raytracer.renderer

import kotlin.math.pow

interface Material {
    val ambient: Float
    val diffuse: Float
    val specular: Float
    val shininess: Float

    fun colorAt(shape: Shape, point: Point): Color
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

data class ColorMaterial(val color: Color = color(1f, 1f, 1f),
                         override val ambient: Float = 0.1f,
                         override val diffuse: Float = 0.9f,
                         override val specular: Float = 0.9f,
                         override val shininess: Float = 200f) : Material {

    override fun colorAt(shape: Shape, point: Point): Color = color
}

data class PatternMaterial(val pattern: Pattern,
                           override val ambient: Float = 0.1f,
                           override val diffuse: Float = 0.9f,
                           override val specular: Float = 0.9f,
                           override val shininess: Float = 200f) : Material {

    override fun colorAt(shape: Shape, point: Point): Color = pattern.patternAtShape(shape, point)
}
