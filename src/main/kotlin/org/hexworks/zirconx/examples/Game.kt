package org.hexworks.zirconx.examples

import org.hexworks.zircon.api.CP437TilesetResources
import org.hexworks.zircon.api.SwingApplications
import org.hexworks.zircon.api.application.AppConfig
import org.hexworks.zircon.api.screen.Screen
import org.hexworks.zirconx.api.tiled.TiledMap
import java.io.File

fun main(args: Array<String>) {
    val tileGrid = SwingApplications.startTileGrid(
        AppConfig.newBuilder()
            .withDefaultTileset(CP437TilesetResources.taffer20x20())
            .withDebugMode(true)
            .build()
    )
    val screen: Screen = Screen.create(tileGrid)
    val pathToMap = requireNotNull(args.firstOrNull()) { "No Tiled map provided" }
    val tiled: TiledMap = TiledMap.loadTiledFile(File(pathToMap))
    tiled.toLayerList().forEach { screen.addLayer(it) }
    screen.display()
}



