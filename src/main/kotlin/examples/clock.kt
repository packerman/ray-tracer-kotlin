package examples

import math.point
import math.rotationY
import kotlin.math.PI

private val twelve = point(0f, 0f, 1f)

private fun hourRotation(i: Int) = rotationY((i * PI / 6).toFloat())

private val hourPositions = (1..12).map { h -> hourRotation(h) }
