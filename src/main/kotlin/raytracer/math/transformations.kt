package raytracer.math

import kotlin.math.cos
import kotlin.math.sin

fun translation(x: Float, y: Float, z: Float) =
        Matrix4(1f, 0f, 0f, x,
                0f, 1f, 0f, y,
                0f, 0f, 1f, z,
                0f, 0f, 0f, 1f)

fun scaling(x: Float, y: Float, z: Float) =
        Matrix4(x, 0f, 0f, 0f,
                0f, y, 0f, 0f,
                0f, 0f, z, 0f,
                0f, 0f, 0f, 1f)

fun rotationX(r: Float) =
        Matrix4(1f, 0f, 0f, 0f,
                0f, cos(r), -sin(r), 0f,
                0f, sin(r), cos(r), 0f,
                0f, 0f, 0f, 1f)

fun rotationY(r: Float) =
        Matrix4(cos(r), 0f, sin(r), 0f,
                0f, 1f, 0f, 0f,
                -sin(r), 0f, cos(r), 0f,
                0f, 0f, 0f, 1f)

fun rotationZ(r: Float) =
        Matrix4(cos(r), -sin(r), 0f, 0f,
                sin(r), cos(r), 0f, 0f,
                0f, 0f, 1f, 0f,
                0f, 0f, 0f, 1f)

fun shearing(xToY: Float, xToZ: Float, yToX: Float, yToZ: Float, zToX: Float, zToY: Float) =
        Matrix4(1f, xToY, xToZ, 0f,
                yToX, 1f, yToZ, 0f,
                zToX, zToY, 1f, 0f,
                0f, 0f, 0f, 1f)

fun identity() = Matrix4.IDENTITY

fun Matrix4.rotateX(r: Float) = rotationX(r) * this

fun Matrix4.rotateY(r: Float) = rotationY(r) * this

fun Matrix4.rotateZ(r: Float) = rotationZ(r) * this

fun Matrix4.scale(x: Float, y: Float, z: Float) = scaling(x, y, z) * this

fun Matrix4.translate(x: Float, y: Float, z: Float) = translation(x, y, z) * this
