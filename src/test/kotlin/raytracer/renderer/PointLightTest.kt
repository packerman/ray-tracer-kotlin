package raytracer.renderer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import raytracer.math.assertTupleEquals
import raytracer.math.color
import raytracer.math.point
import raytracer.math.vector
import kotlin.math.sqrt

internal class PointLightTest {

    private val material = ColorMaterial()
    private val position = point(0f, 0f, 0f)

    @Test
    fun createPointLight() {
        val intensity = color(1f, 1f, 1f)
        val position = point(0f, 0f, 0f)

        val light = PointLight(position, intensity)

        assertEquals(position, light.position)
        assertEquals(intensity, light.intensity)
    }

    @Test
    fun lightingWithEyeBetweenLightAndSurface() {
        val eyeVector = vector(0f, 0f, -1f)
        val normalVector = vector(0f, 0f, -1f)
        val light = PointLight(point(0f, 0f, -10f), color(1f, 1f, 1f))
        val shape = Sphere()

        val result = lighting(material, shape, light, position, eyeVector, normalVector)

        assertTupleEquals(color(1.9f, 1.9f, 1.9f), result, epsilon)
    }

    @Test
    fun lightWithEyeBetweenLightAndSurfaceEyeOffset45Degrees() {
        val eyeVector = vector(0f, sqrt(2f) / 2, -sqrt(2f) / 2)
        val normalVector = vector(0f, 0f, -1f)
        val light = PointLight(point(0f, 0f, -10f), color(1f, 1f, 1f))
        val shape = Sphere()

        val result = lighting(material, shape, light, position, eyeVector, normalVector)

        assertTupleEquals(color(1f, 1f, 1f), result, epsilon)
    }

    @Test
    fun lightWithEyeOppositeSurfaceLightOffset45Degrees() {
        val eyeVector = vector(0f, 0f, -1f)
        val normalVector = vector(0f, 0f, -1f)
        val light = PointLight(point(0f, 10f, -10f), color(1f, 1f, 1f))
        val shape = Sphere()

        val result = lighting(material, shape, light, position, eyeVector, normalVector)

        assertTupleEquals(color(0.7364f, 0.7364f, 0.7364f), result, epsilon)
    }

    @Test
    fun lightingWithEyeInPathOfReflectionVector() {
        val eyeVector = vector(0f, -sqrt(2f) / 2, -sqrt(2f) / 2)
        val normalVector = vector(0f, 0f, -1f)
        val light = PointLight(point(0f, 10f, -10f), color(1f, 1f, 1f))
        val shape = Sphere()

        val result = lighting(material, shape, light, position, eyeVector, normalVector)

        assertTupleEquals(color(1.6364f, 1.6364f, 1.6364f), result, epsilon)
    }

    @Test
    fun lightingWithLightBehindTheSurface() {
        val eyeVector = vector(0f, 1f, -1f)
        val normalVector = vector(0f, 0f, -1f)
        val light = PointLight(point(0f, 0f, 10f), color(1f, 1f, 1f))
        val shape = Sphere()

        val result = lighting(material, shape, light, position, eyeVector, normalVector)

        assertTupleEquals(color(0.1f, 0.1f, 0.1f), result, epsilon)
    }

    @Test
    fun lightWithSurfaceInShadow() {
        val eyeVector = vector(0f, 0f, -1f)
        val normalVector = vector(0f, 0f, -1f)
        val light = PointLight(point(0f, 0f, -10f), color(1f, 1f, 1f))
        val inShadow = true
        val shape = Sphere()

        val result = lighting(material, shape, light, position, eyeVector, normalVector, inShadow)

        assertEquals(color(0.1f, 0.1f, 0.1f), result)
    }

    companion object {
        val epsilon = 0.0001f
    }
}
