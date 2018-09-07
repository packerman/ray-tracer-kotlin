package raytracer.renderer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class LightTest {

    @Test
    fun createPointLight() {
        val intensity = color(1f, 1f, 1f)
        val position = point(0f, 0f, 0f)

        val light = PointLight(position, intensity)

        assertEquals(position, light.position)
        assertEquals(intensity, light.intensity)
    }
}
