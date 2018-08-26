package raytracer.renderer

import raytracer.math.Color
import raytracer.math.color

data class Material(val color: Color = color(1f, 1f, 1f),
                    val ambient: Float = 0.1f,
                    val diffuse: Float = 0.9f,
                    val specular: Float = 0.9f,
                    val shininess: Float = 200f)
