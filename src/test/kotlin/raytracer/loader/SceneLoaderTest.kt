package raytracer.loader

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasSize
import org.hamcrest.Matchers.instanceOf
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.snakeyaml.engine.v1.api.Load
import org.snakeyaml.engine.v1.api.LoadSettingsBuilder
import raytracer.renderer.*
import java.lang.Math.toRadians

internal class SceneLoaderTest {

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

        val scene = SceneLoader(Degrees).load(loaded)

        assertThat(scene.cameras, hasSize(1))
        val camera = scene.cameras[0]

        assertEquals(100, camera.vSize)
        assertEquals(100, camera.hSize)
        assertEquals(0.7853982f, camera.fieldOfView)

        val expectedTransform = viewTransform(point(-6f, 6f, -10f),
                point(6f, 0f, 6f),
                vector(-0.45f, 1f, 0f))

        assertEquals(expectedTransform, camera.transform)
    }

    @Test
    fun canAddLight() {
        val yamlString = """
            |- add: light
            |  at: [50, 100, -50]
            |  intensity: [1, 1, 1]
            |
            |- add: light
            |  at: [-400, 50, -10]
            |  intensity: [0.2, 0.2, 0.2]
        """.trimMargin()

        val settings = LoadSettingsBuilder()
                .build()
        val load = Load(settings)

        val loaded = load.loadFromString(yamlString)

        val scene = SceneLoader().load(loaded)

        assertThat(scene.world.lights, hasSize(2))

        val expectedLight0 = PointLight(
                position = point(50f, 100f, -50f),
                intensity = color(1f, 1f, 1f)
        )
        val expectedLight1 = PointLight(
                position = point(-400f, 50f, -10f),
                intensity = color(0.2f, 0.2f, 0.2f)
        )

        assertEquals(expectedLight0, scene.world.lights[0])
        assertEquals(expectedLight1, scene.world.lights[1])
    }

    @Test
    fun canAddPlane() {
        val yamlString = """
            |- add: plane
            |  material:
            |    color: [1, 1, 1]
            |    ambient: 1
            |    diffuse: 0
            |    specular: 0
            |  transform:
            |    - [ rotate-x, 90]
            |    - [ translate, 0, 0, -500]
            |
        """.trimMargin()

        val settings = LoadSettingsBuilder()
                .build()
        val load = Load(settings)
        val loaded = load.loadFromString(yamlString)
        val scene = SceneLoader(Degrees).load(loaded)

        assertThat(scene.world, hasSize(1))

        assertThat(scene.world[0], instanceOf(Plane::class.java))

        val plane = scene.world[0] as Plane

        val expectedMaterial = Material(
                color = color(1f, 1f, 1f),
                ambient = 1f,
                diffuse = 0f,
                specular = 0f
        )

        val expectedTransform = rotationX(toRadians(90.0).toFloat())
                .translate(0f, 0f, -500f)

        assertEquals(expectedMaterial, plane.material)
        assertEquals(expectedTransform, plane.transform)
    }

    @Test
    fun canAddSphere() {
        val yamlString = """
            |- add: sphere
            |  material:
            |    color: [1, 1, 1]
            |    ambient: 1
            |    diffuse: 0
            |    specular: 0
            |  transform:
            |    - [ rotate-x, 90]
            |    - [ translate, 0, 0, -500]
            |
        """.trimMargin()

        val settings = LoadSettingsBuilder()
                .build()
        val load = Load(settings)
        val loaded = load.loadFromString(yamlString)
        val scene = SceneLoader(Degrees).load(loaded)

        assertThat(scene.world, hasSize(1))

        assertThat(scene.world[0], instanceOf(Sphere::class.java))

        val plane = scene.world[0] as Sphere

        val expectedMaterial = Material(
                color = color(1f, 1f, 1f),
                ambient = 1f,
                diffuse = 0f,
                specular = 0f
        )

        val expectedTransform = rotationX(toRadians(90.0).toFloat())
                .translate(0f, 0f, -500f)

        assertEquals(expectedMaterial, plane.material)
        assertEquals(expectedTransform, plane.transform)
    }

    @Test
    fun canAddCube() {
        val yamlString = """
            |- add: cube
            |  material:
            |    color: [1, 1, 1]
            |    ambient: 1
            |    diffuse: 0
            |    specular: 0
            |  transform:
            |    - [ rotate-x, 90]
            |    - [ translate, 0, 0, -500]
            |
        """.trimMargin()

        val settings = LoadSettingsBuilder()
                .build()
        val load = Load(settings)
        val loaded = load.loadFromString(yamlString)
        val scene = SceneLoader(Degrees).load(loaded)

        assertThat(scene.world, hasSize(1))

        assertThat(scene.world[0], instanceOf(Cube::class.java))

        val plane = scene.world[0] as Cube

        val expectedMaterial = Material(
                color = color(1f, 1f, 1f),
                ambient = 1f,
                diffuse = 0f,
                specular = 0f
        )

        val expectedTransform = rotationX(toRadians(90.0).toFloat())
                .translate(0f, 0f, -500f)

        assertEquals(expectedMaterial, plane.material)
        assertEquals(expectedTransform, plane.transform)
    }

    @Test
    fun canDefineMaterials() {
        val yamlString = """
            |- define: white-material
            |  value:
            |    color: [1, 1, 1]
            |    diffuse: 0.7
            |    ambient: 0.1
            |    specular: 0.0
            |    reflective: 0.1
            |
            |- add: cube
            |  material: white-material
        """.trimMargin()

        val settings = LoadSettingsBuilder()
                .build()
        val load = Load(settings)
        val loaded = load.loadFromString(yamlString)
        val scene = SceneLoader().load(loaded)

        assertThat(scene.world, hasSize(1))

        assertThat(scene.world[0], instanceOf(Cube::class.java))

        val plane = scene.world[0] as Cube

        val expectedMaterial = Material(
                color = color(1f, 1f, 1f),
                ambient = 0.1f,
                diffuse = 0.7f,
                specular = 0f,
                reflective = 0.1f
        )

        assertEquals(expectedMaterial, plane.material)
        assertEquals(Matrix4.identity, plane.transform)
    }

    @Test
    fun canExtendMaterialDefinition() {
        val yamlString = """
            |- define: white-material
            |  value:
            |    color: [1, 1, 1]
            |    diffuse: 0.7
            |    ambient: 0.1
            |    specular: 0.0
            |    reflective: 0.1
            |
            |- define: blue-material
            |  extend: white-material
            |  value:
            |    color: [0.537, 0.831, 0.914]
            |
            |- add: cube
            |  material: blue-material
        """.trimMargin()

        val settings = LoadSettingsBuilder()
                .build()
        val load = Load(settings)
        val loaded = load.loadFromString(yamlString)
        val scene = SceneLoader().load(loaded)

        assertThat(scene.world, hasSize(1))

        assertThat(scene.world[0], instanceOf(Cube::class.java))

        val plane = scene.world[0] as Cube

        val expectedMaterial = Material(
                color = color(0.537f, 0.831f, 0.914f),
                ambient = 0.1f,
                diffuse = 0.7f,
                specular = 0f,
                reflective = 0.1f
        )

        assertEquals(expectedMaterial, plane.material)
        assertEquals(Matrix4.identity, plane.transform)
    }

    @Test
    fun canDefineTransform() {
        val yamlString = """
            |- define: standard-transform
            |  value:
            |    - [translate, 1, -1, 1]
            |    - [scale, 0.5, 0.5, 0.5]
            |
            |- add: cube
            |  transform:
            |    - standard-transform
        """.trimMargin()

        val settings = LoadSettingsBuilder()
                .build()
        val load = Load(settings)
        val loaded = load.loadFromString(yamlString)
        val scene = SceneLoader().load(loaded)

        assertThat(scene.world, hasSize(1))

        assertThat(scene.world[0], instanceOf(Cube::class.java))

        val plane = scene.world[0] as Cube

        val expectedTransform = translation(1f, -1f, 1f)
                .scale(0.5f, 0.5f, 0.5f)

        assertEquals(expectedTransform, plane.transform)
    }

    @Test
    fun canExtendTransform() {
        val yamlString = """
            |- define: standard-transform
            |  value:
            |    - [translate, 1, -1, 1]
            |    - [scale, 0.5, 0.5, 0.5]
            |
            |- define: large-object
            |  value:
            |    - standard-transform
            |    - [scale, 3.5, 3.5, 3.5]
            |
            |- add: cube
            |  transform:
            |    - large-object
        """.trimMargin()

        val settings = LoadSettingsBuilder()
                .build()
        val load = Load(settings)
        val loaded = load.loadFromString(yamlString)
        val scene = SceneLoader().load(loaded)

        assertThat(scene.world, hasSize(1))

        assertThat(scene.world[0], instanceOf(Cube::class.java))

        val plane = scene.world[0] as Cube

        val expectedTransform = translation(1f, -1f, 1f)
                .scale(0.5f, 0.5f, 0.5f)
                .scale(3.5f, 3.5f, 3.5f)

        assertEquals(expectedTransform, plane.transform)
    }

    @Test
    fun canDefineStripePatternMaterials() {
        val yamlString = """
            |- define: test-material
            |  value:
            |    pattern:
            |      type: stripes
            |      colors:
            |        - [0.45, 0.45, 0.45]
            |        - [0.55, 0.55, 0.55]
            |      transform:
            |        - [ scale, 0.25, 0.25, 0.25 ]
            |        - [ rotate-y, 1.5708 ]
            |    diffuse: 0.7
            |    ambient: 0.1
            |    specular: 0.0
            |    reflective: 0.1
            |
            |- add: cube
            |  material: test-material
        """.trimMargin()

        val settings = LoadSettingsBuilder()
                .build()
        val load = Load(settings)
        val loaded = load.loadFromString(yamlString)
        val scene = SceneLoader().load(loaded)

        assertThat(scene.world, hasSize(1))

        assertThat(scene.world[0], instanceOf(Cube::class.java))

        val cube = scene.world[0] as Cube

        val expectedMaterial = Material(
                pattern = StripePattern(color(0.45f, 0.45f, 0.45f),
                        color(0.55f, 0.55f, 0.55f)).apply {
                    transform = scaling(0.25f, 0.25f, 0.25f)
                            .rotateY(1.5708f)
                },
                ambient = 0.1f,
                diffuse = 0.7f,
                specular = 0f,
                reflective = 0.1f
        )

        assertEquals(expectedMaterial, cube.material)
    }

    @Test
    fun canSpecifyAngleInRadians() {
        val yamlString = """
            - add: cube
              transform:
                - [ rotate-y, 0.4 ]
        """.trimIndent()

        val settings = LoadSettingsBuilder()
                .build()
        val load = Load(settings)
        val loaded = load.loadFromString(yamlString)
        val scene = SceneLoader(angleUnit = Radians).load(loaded)

        assertThat(scene.world, hasSize(1))

        assertThat(scene.world[0], instanceOf(Cube::class.java))

        val cube = scene.world[0] as Cube

        assertEquals(rotationY(0.4f), cube.transform)
    }

    @Test
    fun canAddCylinder() {
        val yamlString = """
            |- add: cylinder
            |  min: 0
            |  max: 0.3
            |  closed: false
            |
        """.trimMargin()

        val settings = LoadSettingsBuilder()
                .build()
        val load = Load(settings)
        val loaded = load.loadFromString(yamlString)
        val scene = SceneLoader(Degrees).load(loaded)

        assertThat(scene.world, hasSize(1))

        assertThat(scene.world[0], instanceOf(Cylinder::class.java))

        val cylinder = scene.world[0] as Cylinder

        assertEquals(0f, cylinder.minimum)
        assertEquals(0.3f, cylinder.maximum)
        assertFalse(cylinder.closed)
    }
}
