package raytracer.renderer

import raytracer.math.Color
import raytracer.math.Point
import raytracer.math.color

interface Material {
    val ambient: Float
    val diffuse: Float
    val specular: Float
    val shininess: Float

    fun colorAt(shape: Shape, point: Point): Color
}

data class ColorMaterial(val color: Color = color(1f, 1f, 1f),
                         override val ambient: Float = 0.1f,
                         override val diffuse: Float = 0.9f,
                         override val specular: Float = 0.9f,
                         override val shininess: Float = 200f) : Material {

    override fun colorAt(shape: Shape, point: Point): Color = color
}

data class PatternMaterial(val pattern: StripePattern,
                           override val ambient: Float = 0.1f,
                           override val diffuse: Float = 0.9f,
                           override val specular: Float = 0.9f,
                           override val shininess: Float = 200f) : Material {

    override fun colorAt(shape: Shape, point: Point): Color = pattern.stripeAtObject(shape, point)
}
