package math

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class Matrix3Test {

    @Test
    fun createMatrix() {
        val m = Matrix3(-3f, -5f, 0f,
                1f, -2f, -7f,
                0f, 1f, 1f)

        Assertions.assertEquals(3, m.size)
    }
}
