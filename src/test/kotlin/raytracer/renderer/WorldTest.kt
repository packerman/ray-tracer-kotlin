package raytracer.renderer

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import raytracer.renderer.PatternTest.Companion.testPattern
import raytracer.utils.assertTupleEquals
import java.time.Duration
import kotlin.math.sqrt

internal class WorldTest {

    @Test
    fun createWorld() {
        val w = World()

        assertTrue(w.isEmpty())
        assertTrue(w.lights.isEmpty())
    }

    @Test
    fun createDefaultWorld() {
        val light = PointLight(point(-10f, 10f, -10f),
                color(1f, 1f, 1f))
        val s1 = Sphere().apply {
            material = Material(color(0.8f, 1.0f, 0.6f), diffuse = 0.7f, specular = 0.2f)
        }
        val s2 = Sphere().apply {
            transform = scaling(0.5f, 0.5f, 0.5f)
        }

        val w = defaultWorld()

        assertEquals(listOf(light), w.lights)
        assertNotNull(w.contains(s1))
        assertNotNull(w.contains(s2))
    }

    @Test
    fun intersectWorldWithRay() {
        val world = defaultWorld()

        val ray = Ray(point(0f, 0f, -5f), vector(0f, 0f, 1f))

        val xs = world.intersect(ray)

        assertEquals(4, xs.size)
        assertEquals(4f, xs[0].t)
        assertEquals(4.5f, xs[1].t)
        assertEquals(5.5f, xs[2].t)
        assertEquals(6f, xs[3].t)
    }

    @Test
    fun shadeIntersection() {
        val world = defaultWorld()
        val ray = Ray(point(0f, 0f, -5f), vector(0f, 0f, 1f))
        val shape = world.first()
        val intersection = Intersection(4f, shape)

        val hit = intersection.prepareHit(ray)

        val c = world.shadeHit(hit)

        assertTupleEquals(color(0.38066f, 0.47583f, 0.2855f), c, epsilon)
    }

    @Test
    fun shadeIntersectionFromTheInside() {
        val world = defaultWorld().apply {
            lights = listOf(PointLight(point(0f, 0.25f, 0f), color(1f, 1f, 1f)))
        }

        val ray = Ray(point(0f, 0f, 0f), vector(0f, 0f, 1f))
        val shape = world.toList()[1]
        val intersection = Intersection(0.5f, shape)

        val hit = intersection.prepareHit(ray)

        val c = world.shadeHit(hit)

        assertTupleEquals(color(0.90498f, 0.90498f, 0.90498f), c, epsilon)
    }

    @Test
    fun colorWhenRayMisses() {
        val world = defaultWorld()
        val ray = Ray(point(0f, 0f, -5f), vector(0f, 1f, 0f))

        val c = world.colorAt(ray)

        assertEquals(color(0f, 0f, 0f), c)
    }

    @Test
    fun colorWhenARayHits() {
        val world = defaultWorld()
        val ray = Ray(point(0f, 0f, -5f), vector(0f, 0f, 1f))

        val c = world.colorAt(ray)

        assertTupleEquals(color(0.38066f, 0.47583f, 0.2855f), c, epsilon)
    }

    @Test
    fun noShadowWhenNothingIsCollinearWithPointAndLight() {
        val world = defaultWorld()
        val p = point(0f, 10f, 0f)

        assertFalse(world.isShadowed(p, world.lights[0]))
    }

    @Test
    fun shadowWhenObjectIsBetweenPointAndLight() {
        val world = defaultWorld()
        val p = point(10f, -10f, 10f)

        assertTrue(world.isShadowed(p, world.lights[0]))
    }

    @Test
    fun noShadowWhenObjectIsBehindLight() {
        val world = defaultWorld()
        val p = point(-20f, 20f, -20f)

        assertFalse(world.isShadowed(p, world.lights[0]))
    }

    @Test
    fun noShadowWhenObjectIsBehindPoint() {
        val world = defaultWorld()
        val p = point(-2f, 2f, -2f)

        assertFalse(world.isShadowed(p, world.lights[0]))
    }

    @Test
    fun shadeIntersectionInShadow() {
        val s1 = Sphere()
        val s2 = Sphere().apply {
            transform = translation(0f, 0f, 10f)
        }
        val w = World(
                light = PointLight(point(0f, 0f, -10f), color(1f, 1f, 1f)),
                objects = listOf(s1, s2))
        val r = Ray(point(0f, 0f, 5f), vector(0f, 0f, 1f))
        val i = Intersection(4f, s2)

        val h = i.prepareHit(r)
        val c = w.shadeHit(h)

        assertEquals(color(0.1f, 0.1f, 0.1f), c)
    }

    @Test
    fun reflectedColorForNonReflectiveMaterial() {
        val world = defaultWorld()
        val ray = Ray(point(0f, 0f, 0f), vector(0f, 0f, 1f))
        val shape = world[1]
        shape.material = shape.material.copy(ambient = 1f)
        val intersection = Intersection(1f, shape)

        val hit = intersection.prepareHit(ray)
        val col = world.reflectedColor(hit)

        assertEquals(color(0f, 0f, 0f), col)
    }

    @Test
    fun reflectiveColorForReflectiveMaterial() {
        val shape = Plane().apply {
            material = material.copy(reflective = 0.5f)
            transform = translation(0f, -1f, 0f)
        }
        val world = with(defaultWorld()) {
            World(lights = lights,
                    objects = this + shape)
        }

        val ray = Ray(point(0f, 0f, -3f), vector(0f, -sqrt(2f) / 2, sqrt(2f) / 2))
        val intersection = Intersection(sqrt(2f), shape)

        val hit = intersection.prepareHit(ray)
        val col = world.reflectedColor(hit)

        assertTupleEquals(color(0.19032f, 0.2379f, 0.14274f), col, epsilon)
    }

    @Test
    fun shadeHitWithReflectiveMaterial() {
        val shape = Plane().apply {
            material = material.copy(reflective = 0.5f)
            transform = translation(0f, -1f, 0f)
        }
        val world = with(defaultWorld()) {
            World(lights = lights,
                    objects = this + shape)
        }

        val ray = Ray(point(0f, 0f, -3f), vector(0f, -sqrt(2f) / 2, sqrt(2f) / 2))
        val intersection = Intersection(sqrt(2f), shape)

        val hit = intersection.prepareHit(ray)
        val col = world.shadeHit(hit)

        assertTupleEquals(color(0.87677f, 0.92436f, 0.82918f), col, epsilon)
    }

    @Test
    fun colorAtWithMutuallyReflectiveSurfaces() {
        val lower = Plane().apply {
            material = material.copy(reflective = 1f)
            transform = translation(0f, -1f, 0f)
        }

        val upper = Plane().apply {
            material = material.copy(reflective = 1f)
            transform = translation(0f, 1f, 0f)
        }
        val world = World(
                light = PointLight(position = point(-10f, 10f, -10f), intensity = color(1f, 1f, 1f)),
                objects = listOf(lower, upper))
        val ray = Ray(point(0f, 0f, 0f), vector(0f, 1f, 0f))

        assertTimeout(Duration.ofSeconds(1)) {
            world.colorAt(ray)
        }
    }

    @Test
    fun reflectedColorAtMaximumRecursiveDepth() {
        val shape = Plane().apply {
            material = material.copy(reflective = 0.5f)
            transform = translation(0f, -1f, 0f)
        }
        val world = with(defaultWorld()) {
            World(lights = lights,
                    objects = this + shape)
        }

        val ray = Ray(point(0f, 0f, -3f), vector(0f, -sqrt(2f) / 2, sqrt(2f) / 2))
        val intersection = Intersection(sqrt(2f), shape)

        val hit = intersection.prepareHit(ray)
        val col = world.reflectedColor(hit, 0)

        assertEquals(color(0f, 0f, 0f), col)
    }

    @Test
    fun refractedColorWithOpaqueSurface() {
        val world = defaultWorld()
        val shape = world[0]
        val ray = Ray(point(0f, 0f, -5f), vector(0f, 0f, 1f))
        val xs = intersections(Intersection(4f, shape), Intersection(6f, shape))

        val hit = xs[0].prepareHit(ray, xs)

        val c = world.refractedColor(hit, 5)

        assertEquals(color(0f, 0f, 0f), c)
    }

    @Test
    fun refractedColorAtMaximumRecursiveDepth() {
        val world = defaultWorld()
        val shape = world[0].apply {
            material = material.copy(transparency = 1f, refractiveIndex = 1.5f)
        }
        val ray = Ray(point(0f, 0f, -5f), vector(0f, 0f, 1f))
        val xs = intersections(Intersection(4f, shape), Intersection(6f, shape))

        val hit = xs[0].prepareHit(ray, xs)

        val c = world.refractedColor(hit, 0)

        assertEquals(color(0f, 0f, 0f), c)
    }

    @Test
    internal fun refractedColorUnderTotalInternalReflection() {
        val world = defaultWorld()
        val shape = world[0].apply {
            material = material.copy(transparency = 1f, refractiveIndex = 1.5f)
        }
        val ray = Ray(point(0f, 0f, sqrt(2f) / 2), vector(0f, 1f, 0f))
        val xs = intersections(Intersection(-sqrt(2f) / 2, shape), Intersection(sqrt(2f) / 2, shape))

        val hit = xs[1].prepareHit(ray, xs)

        val c = world.refractedColor(hit, 5)

        assertEquals(color(0f, 0f, 0f), c)
    }

    @Test
    fun refractedColorWithRefractedRay() {
        val world = defaultWorld()
        val a = world[0].apply {
            material = material.copy(ambient = 1f,
                    pattern = testPattern())
        }
        val b = world[1].apply {
            material = material.copy(transparency = 1f,
                    refractiveIndex = 1.5f)
        }
        val ray = Ray(point(0f, 0f, 0.1f), vector(0f, 1f, 0f))
        val xs = intersections(Intersection(-0.9899f, a),
                Intersection(-0.4899f, b),
                Intersection(0.4899f, b),
                Intersection(0.9899f, a))

        val hit = xs[2].prepareHit(ray, xs)
        val c = world.refractedColor(hit, 5)

        assertTupleEquals(color(0f, 0.99878f, 0.04724f), c, epsilon)
    }

    @Test
    internal fun shadeHitWithTransparentMaterial() {
        val floor = Plane().apply {
            transform = translation(0f, -1f, 0f)
            material = material.copy(transparency = 0.5f,
                    refractiveIndex = 1.5f)
        }
        val ball = Sphere().apply {
            transform = translation(0f, -3.5f, -0.5f)
            material = material.copy(pattern = SolidPattern(color(1f, 0f, 0f)),
                    ambient = 0.5f)
        }
        val world = with(defaultWorld()) {
            World(lights, this + listOf(floor, ball))
        }
        val ray = Ray(point(0f, 0f, -3f), vector(0f, -sqrt(2f) / 2, sqrt(2f) / 2))
        val xs = listOf(Intersection(sqrt(2f), floor))

        val hit = xs[0].prepareHit(ray, xs)
        val c = world.shadeHit(hit)

        assertTupleEquals(color(0.93642f, 0.68642f, 0.68642f), c, epsilon)
    }

    @Test
    internal fun shadeHitWithReflectiveTransparentMaterial() {
        val floor = Plane().apply {
            transform = translation(0f, -1f, 0f)
            material = material.copy(reflective = 0.5f,
                    transparency = 0.5f,
                    refractiveIndex = 1.5f)
        }
        val ball = Sphere().apply {
            transform = translation(0f, -3.5f, -0.5f)
            material = material.copy(pattern = SolidPattern(color(1f, 0f, 0f)),
                    ambient = 0.5f)
        }
        val world = with(defaultWorld()) {
            World(lights, this + listOf(floor, ball))
        }
        val ray = Ray(point(0f, 0f, -3f), vector(0f, -sqrt(2f) / 2, sqrt(2f) / 2))
        val xs = listOf(Intersection(sqrt(2f), floor))

        val hit = xs[0].prepareHit(ray, xs)
        val c = world.shadeHit(hit)

        assertTupleEquals(color(0.93391f, 0.69643f, 0.69243f), c, epsilon)
    }


    private companion object {

        const val epsilon = 0.001f
    }
}
