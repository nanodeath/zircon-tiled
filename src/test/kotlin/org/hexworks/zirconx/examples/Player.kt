package org.hexworks.zirconx.examples

import org.hexworks.zircon.api.data.GraphicalTile
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.graphics.TileGraphics
import org.hexworks.zircon.internal.graphics.FastTileGraphics

class Player(val tile: GraphicalTile, var position: Position) {
    fun draw(tg: TileGraphics) {
        tg.draw(tile, position)
    }
}