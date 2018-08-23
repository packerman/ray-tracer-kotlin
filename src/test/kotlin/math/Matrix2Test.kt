package math

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class Matrix2Test {

    @Test
    fun createMatrix() {
        val m = Matrix2(-3f, -5f,
                1f, -2f)

        Assertions.assertEquals(2, m.size)
    }
}
