package raytracer.math

import org.junit.jupiter.api.Assertions.assertEquals

fun assertTupleEquals(expected: Tuple, actual: Tuple, delta: Float) {
    assertEquals(expected.x, actual.x, delta)
    assertEquals(expected.y, actual.y, delta)
    assertEquals(expected.z, actual.z, delta)
    assertEquals(expected.w, actual.w, delta)
}

fun assertTupleEquals(expected: Tuple, actual: Tuple) {
    assertEquals(expected.x, actual.x)
    assertEquals(expected.y, actual.y)
    assertEquals(expected.z, actual.z)
    assertEquals(expected.w, actual.w)
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
