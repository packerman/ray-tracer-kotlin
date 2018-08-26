package raytracer.renderer

import raytracer.math.*
import kotlin.math.pow

class PointLight(val position: Point, val intensity: Color)

fun lighting(material: Material, light: PointLight, position: Point, eyeVector: Vector, normalVector: Vector): Color {
    val effectiveColor = material.color * light.intensity
    val lightVector = (light.position - position).normalize()

    val ambient = effectiveColor * material.ambient

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
