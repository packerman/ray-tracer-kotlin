package raytracer.renderer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import raytracer.math.color

internal class MaterialTest {

    @Test
    fun defaultMaterial() {
        val m = Material()

        assertEquals(color(1f, 1f, 1f), m.color)
        assertEquals(0.1f, m.ambient)
        assertEquals(0.9f, m.diffuse)
        assertEquals(0.9f, m.specular)
        assertEquals(200f, m.shininess)
    }
}
