package raytracer.loader

import raytracer.renderer.render
import raytracer.renderer.saveToFile
import java.io.File

fun main(args: Array<String>) {
    require(args.isNotEmpty()) { "Specify path to file" }

    val filePath = args[0]

    val fileContents = File(filePath).readText()

    val scene = SceneLoader.loadFromString(fileContents)

    require(scene.cameras.isNotEmpty())

    val image = scene.cameras[0].render(scene.world)

    image.saveToFile("cover.ppm")
}
