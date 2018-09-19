package raytracer.utils

import org.junit.jupiter.api.Assertions.assertEquals
import raytracer.renderer.Matrix4
import raytracer.renderer.Tuple

fun assertTupleEquals(expected: Tuple, actual: Tuple, delta: Float) {
    assertEquals(expected.x, actual.x, delta) { "expected: $expected, actual: $actual" }
    assertEquals(expected.y, actual.y, delta) { "expected: $expected, actual: $actual" }
    assertEquals(expected.z, actual.z, delta) { "expected: $expected, actual: $actual" }
    assertEquals(expected.w, actual.w, delta) { "expected: $expected, actual: $actual" }
}

fun assertTupleEquals(expected: Tuple, actual: Tuple) {
    assertEquals(expected.x, actual.x) { "expected: $expected, actual: $actual" }
    assertEquals(expected.y, actual.y) { "expected: $expected, actual: $actual" }
    assertEquals(expected.z, actual.z) { "expected: $expected, actual: $actual" }
    assertEquals(expected.w, actual.w) { "expected: $expected, actual: $actual" }
}

fun assertMatrixEquals(expected: Matrix4, actual: Matrix4, delta: Float) {
    for (i in 0..3) {
        for (j in 0..3) {
            assertEquals(expected[i, j], actual[i, j], delta)
        }
    }
}

fun assertMatrixEquals(expected: Matrix4, actual: Matrix4) {
    for (i in 0..3) {
        for (j in 0..3) {
            assertEquals(expected[i, j], actual[i, j])
        }
    }
}
