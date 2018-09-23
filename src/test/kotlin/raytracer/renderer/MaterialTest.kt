package raytracer.renderer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import raytracer.utils.assertTupleEquals
import kotlin.math.sqrt

internal class MaterialTest {

    private val material = Material()
    private val position = point(0f, 0f, 0f)

    @Test
    fun defaultMaterial() {
        val m = Material()

        assertEquals(SolidPattern(color(1f, 1f, 1f)), m.pattern)
        assertEquals(0.1f, m.ambient)
        assertEquals(0.9f, m.diffuse)
        assertEquals(0.9f, m.specular)
        assertEquals(200f, m.shininess)
    }

    @Test
    fun lightingWithEyeBetweenLightAndSurface() {
        val eyeVector = vector(0f, 0f, -1f)
        val normalVector = vector(0f, 0f, -1f)
        val light = PointLight(point(0f, 0f, -10f), color(1f, 1f, 1f))
        val shape = Sphere()

        val result = material.lighting(shape, light, position, eyeVector, normalVector)

        assertTupleEquals(color(1.9f, 1.9f, 1.9f), result, epsilon)
    }

    @Test
    fun lightWithEyeBetweenLightAndSurfaceEyeOffset45Degrees() {
        val eyeVector = vector(0f, sqrt(2f) / 2, -sqrt(2f) / 2)
        val normalVector = vector(0f, 0f, -1f)
        val light = PointLight(point(0f, 0f, -10f), color(1f, 1f, 1f))
        val shape = Sphere()

        val result = material.lighting(shape, light, position, eyeVector, normalVector)

        assertTupleEquals(color(1f, 1f, 1f), result, epsilon)
    }

    @Test
    fun lightWithEyeOppositeSurfaceLightOffset45Degrees() {
        val eyeVector = vector(0f, 0f, -1f)
        val normalVector = vector(0f, 0f, -1f)
        val light = PointLight(point(0f, 10f, -10f), color(1f, 1f, 1f))
        val shape = Sphere()

        val result = material.lighting(shape, light, position, eyeVector, normalVector)

        assertTupleEquals(color(0.7364f, 0.7364f, 0.7364f), result, epsilon)
    }

    @Test
    fun lightingWithEyeInPathOfReflectionVector() {
        val eyeVector = vector(0f, -sqrt(2f) / 2, -sqrt(2f) / 2)
        val normalVector = vector(0f, 0f, -1f)
        val light = PointLight(point(0f, 10f, -10f), color(1f, 1f, 1f))
        val shape = Sphere()

        val result = material.lighting(shape, light, position, eyeVector, normalVector)

        assertTupleEquals(color(1.6364f, 1.6364f, 1.6364f), result, epsilon)
    }

    @Test
    fun lightingWithLightBehindTheSurface() {
        val eyeVector = vector(0f, 1f, -1f)
        val normalVector = vector(0f, 0f, -1f)
        val light = PointLight(point(0f, 0f, 10f), color(1f, 1f, 1f))
        val shape = Sphere()

        val result = material.lighting(shape, light, position, eyeVector, normalVector)

        assertTupleEquals(color(0.1f, 0.1f, 0.1f), result, epsilon)
    }

    @Test
    fun lightWithSurfaceInShadow() {
        val eyeVector = vector(0f, 0f, -1f)
        val normalVector = vector(0f, 0f, -1f)
        val light = PointLight(point(0f, 0f, -10f), color(1f, 1f, 1f))
        val inShadow = true
        val shape = Sphere()

        val result = material.lighting(shape, light, position, eyeVector, normalVector, inShadow)

        assertEquals(color(0.1f, 0.1f, 0.1f), result)
    }

    @Test
    fun lightingWithPatternApplied() {
        val m = Material(
                pattern = StripePattern(white, black),
                ambient = 1f,
                diffuse = 0f,
                specular = 0f)
        val eyeVector = vector(0f, 0f, -1f)
        val normalVector = vector(0f, 0f, -1f)
        val light = PointLight(point(0f, 0f, -10f), color(1f, 1f, 1f))
        val shape = Sphere()

        val c1 = m.lighting(shape, light, point(0.9f, 0f, 0f), eyeVector, normalVector, false)
        val c2 = m.lighting(shape, light, point(1.1f, 0f, 0f), eyeVector, normalVector, false)

        assertEquals(white, c1)
        assertEquals(black, c2)
    }

    @Test
    fun reflectivityForDefaultMaterial() {
        val m = Material()
        assertEquals(0f, m.reflective)
    }

    @Test
    internal fun transparencyAndRefractiveIndexForDefaultMaterial() {
        val m = Material()
        assertEquals(0f, m.transparency)
        assertEquals(1f, m.refractiveIndex)
    }

    private companion object {
        val epsilon = 0.0001f
    }
}
