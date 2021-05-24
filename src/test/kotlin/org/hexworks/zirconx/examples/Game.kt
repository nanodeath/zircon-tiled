package org.hexworks.zirconx.examples

import org.hexworks.zircon.api.SwingApplications
import org.hexworks.zircon.api.application.AppConfig
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.screen.Screen
import org.hexworks.zircon.api.tileset.TilesetLoader
import org.hexworks.zircon.api.uievent.*
import org.hexworks.zirconx.api.tiled.TiledMap
import org.hexworks.zirconx.api.tiled.TiledSwingGraphicTilesetLoader
import org.hexworks.zirconx.api.tiled.ext.TiledObject
import java.awt.Graphics2D
import java.io.File

fun main(args: Array<String>) {
    val tileGrid = SwingApplications.startTileGrid(
        AppConfig.newBuilder()
            .withDebugMode(true)
            .withTilesetLoaders(TiledSwingGraphicTilesetLoader())
            .build()
    )
    val screen: Screen = Screen.create(tileGrid)
    val pathToMap = requireNotNull(args.firstOrNull()) { "No Tiled map provided" }
    val tiled: TiledMap = TiledMap.load(File(pathToMap))

    tiled.tileLayers.toZirconLayers().forEach {
        screen.addLayer(it)
    }
    tiled.objectLayers.forEach { layer ->
        println("Object layer: ${layer.name}")
        for (obj in layer) {
            when (obj) {
                is TiledObject.Point -> println(" - point ${obj.name} @ ${obj.x},${obj.y}")
                is TiledObject.Text -> println(" - text ${obj.name} \"${obj.text}\" @ ${obj.x},${obj.y} (attributes: ${obj.attributes})")
                is TiledObject.BaseObject -> println(" - rect ${obj.name} @ ${obj.x},${obj.y}")
            }
        }
    }

    val playerPosition = tiled.objectLayers["Creatures"]["Player"].let { obj ->
        Position.create(obj.x.toInt(), obj.y.toInt())
    }
    val playerTile = tiled.tilesets.queryTileId { it.type == "Player" }
    val player = Player(tiled.createZirconTile(playerTile), playerPosition)

    screen.display()
    val zirconCreatureLayer = tiled.initializeZirconLayer().build()
        .also { screen.addLayer(it) }
    player.draw(zirconCreatureLayer)

    println("Player position: $playerPosition")

    val blocked = tiled.tileLayers.getAllStacks()
        .mapNotNull { (pos, stack) ->
            pos.takeIf { stack.any { tiled.tilesets.toTilesetTile(it).properties["isSolid"] == true } }
        }
        .toSet()

    screen.handleKeyboardEvents(KeyboardEventType.KEY_PRESSED) { event: KeyboardEvent, phase: UIEventPhase ->
        val deltaPosition = when (event.code) {
            KeyCode.RIGHT -> Position.create(1, 0)
            KeyCode.LEFT -> Position.create(-1, 0)
            KeyCode.DOWN -> Position.create(0, 1)
            KeyCode.UP -> Position.create(0, -1)
            else -> return@handleKeyboardEvents UIEventResponse.pass()
        }
        zirconCreatureLayer.clear()
        val newPosition = player.position + deltaPosition
        if (newPosition !in blocked) {
            player.position = newPosition
        }
        player.draw(zirconCreatureLayer)

        UIEventResponse.processed()
    }
}



