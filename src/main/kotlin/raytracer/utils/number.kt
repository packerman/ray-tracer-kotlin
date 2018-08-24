package raytracer.utils

fun Float.clamp(bottom: Float, top: Float) = when {
    this < bottom -> bottom
    this > top -> top
    else -> this
}
