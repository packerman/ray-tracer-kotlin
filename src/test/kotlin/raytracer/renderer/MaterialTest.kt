package raytracer.renderer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import raytracer.math.*

internal class MaterialTest {

    @Test
    fun defaultMaterial() {
        val m = ColorMaterial()

        assertEquals(color(1f, 1f, 1f), m.color)
        assertEquals(0.1f, m.ambient)
        assertEquals(0.9f, m.diffuse)
        assertEquals(0.9f, m.specular)
        assertEquals(200f, m.shininess)
    }

    @Test
    fun lightingWithPatternApplied() {
        val m = PatternMaterial(
                pattern = StripePattern(white, black),
                ambient = 1f,
                diffuse = 0f,
                specular = 0f)
        val eyeVector = vector(0f, 0f, -1f)
        val normalVector = vector(0f, 0f, -1f)
        val light = PointLight(point(0f, 0f, -10f), color(1f, 1f, 1f))

        val c1 = lighting(m, Sphere(), light, point(0.9f, 0f, 0f), eyeVector, normalVector, false)
        val c2 = lighting(m, Sphere(), light, point(1.1f, 0f, 0f), eyeVector, normalVector, false)

        assertEquals(white, c1)
        assertEquals(black, c2)
    }
}
