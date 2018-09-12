package raytracer.renderer

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.system.measureTimeMillis

@Disabled
class MatrixPerformanceTest {

    private val random = Random()

    @Test
    internal fun measureInverseTime() {
        (1..5).forEach { _ ->
            val n = 1000000
            val matrices = (1..n).map { random.nextMatrix(-1f, 1f) }
            val timeMillis = measureTimeMillis {
                val inverts = matrices.map(Matrix4::inverse)
                println("Number of matrices: " + inverts.size)
            }
            println("Total time: $timeMillis ms")
        }
    }

    private companion object {
        fun Random.nextFloat(a: Float, b: Float) =
                nextFloat() * (b - a) + a

        fun Random.nextMatrix(a: Float = 0f, b: Float = 1f) = Matrix4(
                nextFloat(a, b), nextFloat(a, b), nextFloat(a, b), nextFloat(a, b),
                nextFloat(a, b), nextFloat(a, b), nextFloat(a, b), nextFloat(a, b),
                nextFloat(a, b), nextFloat(a, b), nextFloat(a, b), nextFloat(a, b),
                nextFloat(a, b), nextFloat(a, b), nextFloat(a, b), nextFloat(a, b)
        )
    }
}
