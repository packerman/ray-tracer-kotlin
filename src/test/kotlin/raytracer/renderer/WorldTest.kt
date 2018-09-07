package raytracer.renderer

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import raytracer.utils.assertTupleEquals

internal class WorldTest {

    @Test
    fun createWorld() {
        val w = World()

        assertTrue(w.isEmpty())
        assertNull(w.light)
    }

    @Test
    fun createDefaultWorld() {
        val light = PointLight(point(-10f, 10f, -10f),
                color(1f, 1f, 1f))
        val s1 = Sphere().apply {
            material = ColorMaterial(color(0.8f, 1.0f, 0.6f), diffuse = 0.7f, specular = 0.2f)
        }
        val s2 = Sphere().apply {
            transform = scaling(0.5f, 0.5f, 0.5f)
        }

        val w = defaultWorld()

        assertEquals(light, w.light)
        assertNotNull(w.find { s1.isSameSphere(it) })
        assertNotNull(w.find { s2.isSameSphere(it) })
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
            light = PointLight(point(0f, 0.25f, 0f), color(1f, 1f, 1f))
        }

        val ray = Ray(point(0f, 0f, 0f), vector(0f, 0f, 1f))
        val shape = world.toList()[1]
        val intersection = Intersection(0.5f, shape)

        val hit = intersection.prepareHit(ray)

        val c = world.shadeHit(hit)

        assertTupleEquals(color(0.1f, 0.1f, 0.1f), c, epsilon)
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

        assertFalse(world.isShadowed(p))
    }

    @Test
    fun shadowWhenObjectIsBetweenPointAndLight() {
        val world = defaultWorld()
        val p = point(10f, -10f, 10f)

        assertTrue(world.isShadowed(p))
    }

    @Test
    fun noShadowWhenObjectIsBehindLight() {
        val world = defaultWorld()
        val p = point(-20f, 20f, -20f)

        assertFalse(world.isShadowed(p))
    }

    @Test
    fun noShadowWhenObjectIsBehindPoint() {
        val world = defaultWorld()
        val p = point(-2f, 2f, -2f)

        assertFalse(world.isShadowed(p))
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

    private companion object {
        const val epsilon = 0.0001f

        fun Sphere.isSameSphere(other: Shape): Boolean =
                material == other.material && transform == other.transform
    }
}
