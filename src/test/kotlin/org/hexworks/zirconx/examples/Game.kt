package org.hexworks.zirconx.examples

import org.hexworks.zircon.api.SwingApplications
import org.hexworks.zircon.api.application.AppConfig
import org.hexworks.zircon.api.builder.graphics.LayerBuilder
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.screen.Screen
import org.hexworks.zircon.api.uievent.*
import org.hexworks.zircon.internal.graphics.FastTileGraphics
import org.hexworks.zirconx.api.tiled.TiledMap
import org.hexworks.zirconx.api.tiled.ext.TiledObject
import org.hexworks.zirconx.api.tiled.getObjectByName
import java.io.File

fun main(args: Array<String>) {
    val tileGrid = SwingApplications.startTileGrid(
        AppConfig.newBuilder()
            .withDebugMode(true)
            .build()
    )
    val screen: Screen = Screen.create(tileGrid)
    val pathToMap = requireNotNull(args.firstOrNull()) { "No Tiled map provided" }
    val tiled: TiledMap = TiledMap.loadTiledFile(File(pathToMap))

    tiled.toLayerList().forEach {
        screen.addLayer(it)
    }
    tiled.objectLayers.forEach { layer ->
        println("Object layer: ${layer.name}")
        for (obj in layer.objects) {
            when (obj) {
                is TiledObject.Point -> println(" - point ${obj.name} @ ${obj.x},${obj.y}")
                is TiledObject.Text -> println(" - text ${obj.name} \"${obj.text}\" @ ${obj.x},${obj.y} (attributes: ${obj.attributes})")
                is TiledObject.BaseObject -> println(" - rect ${obj.name} @ ${obj.x},${obj.y}")
            }
        }
    }

    val size = Size.create(tiled.toLayerList().first().width, tiled.toLayerList().first().height)
    val tg = FastTileGraphics(size, tiled.tilesetResource)

    val playerPosition = tiled.getObjectByName("Player", layerName = "Creatures")?.let { obj ->
        Position.create(obj.x.toInt(), obj.y.toInt())
    } ?: error("No Player point found?")
    val player = Player(tiled.findTileBy { it.type == "Player" }, playerPosition)
    player.draw(tg)
    screen.display()
    val zirconCreatureLayer = LayerBuilder.newBuilder()
        .withSize(size)
        .withTileGraphics(tg)
        .withTileset(tiled.tilesetResource)
        .build()
        .also { screen.addLayer(it) }

    println("Player position: $playerPosition")
    tiled.getObjectLayer("Creatures")

    val blocked = tiled.allTiles()
        .mapNotNull { (pos, stack) ->
            pos.takeIf { stack.any { it?.properties?.get("isSolid") == true } }
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



