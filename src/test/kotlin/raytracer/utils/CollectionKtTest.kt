package raytracer.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

internal class CollectionKtTest {

    @Test
    fun second() {
        val a = listOf(1, 2, 3)

        assertEquals(2, a.second())
    }

    @Test
    fun secondOneElementList() {
        val a = listOf(1)

        assertThrows(IllegalStateException::class.java) { a.second() }
    }

    @Test
    fun secondEmptyList() {
        val a = emptyList<Int>()

        assertThrows(IllegalStateException::class.java) { a.second() }
    }
}
