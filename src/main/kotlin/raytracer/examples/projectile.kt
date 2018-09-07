package raytracer.examples

import raytracer.renderer.*

private class Projectile(val position: Point, val velocity: Vector)

private class World(val gravity: Vector, val wind: Vector)

private fun tick(world: World, p: Projectile): Projectile =
        Projectile(p.position + p.velocity,
                p.velocity + world.gravity + world.wind)

private fun simulate(projectile: Projectile, w: World, onPosition: (Point) -> Unit) {
    var p = projectile
    var ticks = 0
    while (p.position.y > 0) {
        p = tick(w, p)
        onPosition(p.position)
        ticks++
    }
    println("Number of ticks: $ticks")
}

fun main(args: Array<String>) {
    val p = Projectile(position = point(0f, 1f, 0f),
            velocity = vector(1f, 1.8f, 0f).normalize() * 11.25f)

    val w = World(gravity = vector(0f, -0.1f, 0f),
            wind = vector(-0.01f, 0f, 0f))

    val c = Canvas(900, 550)

    val red = color(1f, 0f, 0f)

    simulate(p, w) { position ->
        val i = position.x.toInt()
        val j = (c.height - position.y).toInt()
        c.writePixel(i, j, red)
    }

    c.saveToFile("projectile.ppm")
}
