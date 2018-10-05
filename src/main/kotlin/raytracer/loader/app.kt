package raytracer.loader

import org.snakeyaml.engine.v1.api.Load
import org.snakeyaml.engine.v1.api.LoadSettingsBuilder
import raytracer.renderer.render
import raytracer.renderer.saveToFile
import java.io.File

fun main(args: Array<String>) {
    require(args.isNotEmpty()) { "Specify path to file" }

    val filePath = args[0]

    val fileContents = File(filePath).readText()

    val settings = LoadSettingsBuilder()
            .build()
    val load = Load(settings)
    val loaded = load.loadFromString(fileContents)

    val scene = Loader().loadScene(loaded)

    require(scene.cameras.isNotEmpty())

    val image = scene.cameras[0].render(scene.world)

    image.saveToFile("cover.ppm")
}
