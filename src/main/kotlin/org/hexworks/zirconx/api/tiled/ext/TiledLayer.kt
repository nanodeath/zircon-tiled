package org.hexworks.zirconx.api.tiled.ext

import org.hexworks.zircon.api.builder.data.TileBuilder
import org.hexworks.zircon.api.builder.graphics.LayerBuilder
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.graphics.Layer
import org.hexworks.zirconx.api.tiled.TiledTilesetResource

class TiledLayer(
    val data: IntArray,
    val width: Int,
    val height: Int,
    val x: Int,
    val y: Int,
    val id: Int,
    val opacity: Double,
    val type: String,
    val visible: Boolean
) {
    fun toLayer(tileset: TiledTilesetResource): Layer {
        val layer = LayerBuilder.newBuilder()
            .withSize(Size.create(width, height))
            .build()

        for (y in 0 until height) {
            for (x in 0 until width) {
                layer.draw(
                    tile = TileBuilder.newBuilder()
                        .withName(data[y * height + x].toString())
                        .withTileset(tileset)
                        .buildGraphicalTile(),
                    drawPosition = Position.create(x, y)
                )
            }
        }
        return layer
    }
}