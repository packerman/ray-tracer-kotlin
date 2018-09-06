package raytracer.renderer

import raytracer.math.*
import kotlin.math.pow

data class PointLight(val position: Point, val intensity: Color)

fun lighting(material: Material,
             light: PointLight,
             position: Point,
             eyeVector: Vector,
             normalVector: Vector,
             inShadow: Boolean = false): Color {
    val effectiveColor = material.colorAt(position) * light.intensity

    val ambient = effectiveColor * material.ambient
    if (inShadow) {
        return ambient
    }

    val lightVector = (light.position - position).normalize()
    val lightDotNormal = lightVector.dot(normalVector)
    val diffuse = if (lightDotNormal < 0f) black else effectiveColor * material.diffuse * lightDotNormal

    fun specularLightning(): Color {
        val reflectVector = (-lightVector).reflect(normalVector)
        val reflectDotEye = reflectVector.dot(eyeVector).pow(material.shininess)
        if (reflectDotEye < 0f) return black
        return light.intensity * material.specular * reflectDotEye
    }

    val specular = if (lightDotNormal < 0f) black else specularLightning()

    return ambient + diffuse + specular
}
