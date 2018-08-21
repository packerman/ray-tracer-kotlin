package examples

import math.*

private class Projectile(val position: Tuple, val velocity: Tuple) {

    init {
        require(position.isPoint)
        require(velocity.isVector)
    }
}

private class World(val gravity: Tuple, val wind: Tuple) {

    init {
        require(gravity.isVector)
        require(wind.isVector)
    }
}

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
