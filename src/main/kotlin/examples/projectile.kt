package examples

import math.Point
import math.Vector
import math.point
import math.vector

private class Projectile(val position: Point, val velocity: Vector)

private class World(val gravity: Vector, val wind: Vector)

private fun tick(world: World, p: Projectile): Projectile =
        Projectile(p.position + p.velocity,
                p.velocity + world.gravity + world.wind)

private fun simulate(projectile: Projectile, w: World) {
    var p = projectile
    var ticks = 0
    while (p.position.y > 0) {
        p = tick(w, p)
        println(p.position)
        ticks++
    }
    println("Number of ticks: $ticks")
}

fun main(args: Array<String>) {
    val p = Projectile(point(0f, 1f, 0f), vector(1f, 1f, 0f).normalize())
    val w = World(vector(0f, -0.1f, 0f), vector(-0.01f, 0f, 0f))

    simulate(p, w)
}
