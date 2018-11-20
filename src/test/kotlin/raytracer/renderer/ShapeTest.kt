package raytracer.renderer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import raytracer.utils.assertTupleEquals
import kotlin.math.PI
import kotlin.math.sqrt

internal class ShapeTest {

    private var savedRay: Ray? = null

    @Test
    fun defaultTransformation() {
        val s = testShape()
        assertEquals(Matrix4.identity, s.transform)
    }

    @Test
    fun assignTransformation() {
        val s = testShape()
        val t = translation(2f, 3f, 4f)
        s.transform = t
        assertEquals(t, s.transform)
    }

    @Test
    fun defaultMaterial() {
        val s = testShape()
        val m = s.material

        assertEquals(Material(), m)
    }

    @Test
    fun assignMaterial() {
        val s = testShape()
        val m = Material(ambient = 1f)

        s.material = m

        assertEquals(m, s.material)
    }

    @Test
    fun intersectScaledShape() {
        val r = Ray(point(0f, 0f, -5f), vector(0f, 0f, 1f))
        val s = testShape()
        s.transform = scaling(2f, 2f, 2f)

        s.intersect(r)

        assertEquals(point(0f, 0f, -2.5f), savedRay?.origin)
        assertEquals(vector(0f, 0f, 0.5f), savedRay?.direction)
    }

    @Test
    fun intersectTranslatedShape() {
        val r = Ray(point(0f, 0f, -5f), vector(0f, 0f, 1f))
        val s = testShape()
        s.transform = translation(5f, 0f, 0f)

        s.intersect(r)

        assertEquals(point(-5f, 0f, -5f), savedRay?.origin)
        assertEquals(vector(0f, 0f, 1f), savedRay?.direction)
    }

    @Test
    fun normalOnTranslatedShape() {
        val s = testShape()
        s.transform = translation(0f, 5f, 0f)

        val n = s.normalAt(point(1f, 5f, 0f))

        assertTupleEquals(vector(1f, 0f, 0f), n, epsilon)
    }

    @Test
    fun normalOnScaledShape() {
        val s = testShape()
        s.transform = scaling(1f, 0.5f, 1f)

        val n = s.normalAt(point(0f, sqrt(2f) / 2, -sqrt(2f) / 2))

        assertTupleEquals(vector(0f, 0.97014f, -0.24254f), n, epsilon)
    }

    @Test
    fun shapeHasParentAttribute() {
        val s = testShape()
        assertNull(s.parent)
    }

    @Test
    fun convertPointFromWorldToObjectSpace() {
        val g1 = Group()
        g1.transform = rotationY((PI / 2).toFloat())
        val g2 = Group()
        g2.transform = scaling(2f, 2f, 2f)
        g1.addChild(g2)
        val s = Sphere()
        s.transform = translation(5f, 0f, 0f)
        g2.addChild(s)

        val p = s.worldToObject(point(-2f, 0f, -10f))

        assertTupleEquals(point(0f, 0f, -1f), p, epsilon)
    }

    @Test
    fun convertingNormalFromObjectToWorldSpace() {
        val g1 = Group()
        g1.transform = rotationY((PI / 2).toFloat())
        val g2 = Group()
        g2.transform = scaling(1f, 2f, 3f)
        g1.addChild(g2)
        val s = Sphere()
        s.transform = translation(5f, 0f, 0f)
        g2.addChild(s)

        val n = s.normalToWorld(vector(sqrt(3f) / 3, sqrt(3f) / 3, sqrt(3f) / 3))

        assertTupleEquals(vector(0.2857f, 0.4286f, -0.8571f), n, epsilon)
    }

    @Test
    fun findingNormalOnChildObject() {
        val g1 = Group()
        g1.transform = rotationY((PI / 2).toFloat())
        val g2 = Group()
        g2.transform = scaling(1f, 2f, 3f)
        g1.addChild(g2)
        val s = Sphere()
        s.transform = translation(5f, 0f, 0f)
        g2.addChild(s)

        val n = s.normalAt(point(1.7321f, 1.1547f, -5.5774f))

        assertTupleEquals(vector(0.2857f, 0.4286f, -0.8571f), n, epsilon)
    }

    @Test
    fun boundsOfTransformedObjects() {
        val s = Sphere().apply {
            transform = translation(1f, 2f, 3f)
        }

        val expectedBounds = Bounds(
                point(0f, 1f, 2f),
                point(2f, 3f, 4f)
        )
        assertEquals(expectedBounds, s.bounds())
    }

    private fun testShape() = object : Shape() {

        override fun localIntersect(ray: Ray): List<Intersection> {
            savedRay = ray
            return emptyList()
        }

        override fun localNormalAt(point: Point): Vector =
                point - point(0f, 0f, 0f)

        override fun localBounds(): Bounds =
                Bounds(point(-1f, -1f, -1f), point(1f, 1f, 1f))
    }

    private companion object {

        const val epsilon = 0.0001f
    }
}
