package raytracer.loader

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.snakeyaml.engine.v1.api.Load
import org.snakeyaml.engine.v1.api.LoadSettingsBuilder
import raytracer.renderer.point
import raytracer.renderer.vector
import raytracer.renderer.viewTransform

internal class LoaderTest {

    @Test
    fun canAddCamera() {
        val yamlString = """
            |- add: camera
            |  width: 100
            |  height: 100
            |  field-of-view: 45
            |  from: [-6, 6, -10]
            |  to: [6, 0, 6]
            |  up: [-0.45, 1, 0]
        """.trimMargin()

        val settings = LoadSettingsBuilder()
                .build()
        val load = Load(settings)

        val loaded = load.loadFromString(yamlString)

        val scene = loadScene(loaded)

        assertThat(scene.cameras, hasSize(1))
        val camera = scene.cameras[0]

        assertEquals(100, camera.vSize)
        assertEquals(100, camera.hSize)
        assertEquals(45f, camera.fieldOfView)

        val expectedTransform = viewTransform(point(-6f, 6f, -10f),
                point(6f, 0f, 6f),
                vector(-0.45f, 1f, 0f))

        assertEquals(expectedTransform, camera.transform)
    }

}
