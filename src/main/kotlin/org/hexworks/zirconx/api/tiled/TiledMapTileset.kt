package org.hexworks.zirconx.api.tiled

import com.fasterxml.jackson.module.kotlin.readValue
import org.hexworks.cobalt.core.api.UUID
import org.hexworks.zircon.api.data.GraphicalTile
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.resource.TilesetResource
import org.hexworks.zircon.api.tileset.Tileset
import org.hexworks.zirconx.api.tiled.ext.TiledTilesetFile
import org.hexworks.zirconx.api.xml
import java.awt.Graphics2D
import java.io.File

class TiledMapTileset(private val tilesetResource: TilesetResource) : Tileset<Graphics2D> {
    override val height: Int get() = tilesetResource.height
    override val width: Int get() = tilesetResource.width
    override val targetType = Graphics2D::class
    override val id: UUID = tilesetResource.id
    private val tilesets: List<Pair<IntRange, Java2DTiledTileset>>

    init {
        tilesetResource as TiledTilesetResource
        tilesets = tilesetResource.tiledMapData.tilesets
            .map {
                val tiledTilesetFile =
                    xml.readValue<TiledTilesetFile>(File(tilesetResource.path).resolveSibling(it.source))
                val range = it.firstgid until (it.firstgid + tiledTilesetFile.tilecount)
                range to Java2DTiledTileset.from(tiledTilesetFile, it.firstgid, File(tilesetResource.path))
            }
    }

    override fun drawTile(tile: Tile, surface: Graphics2D, position: Position) {
        require(tile is GraphicalTile) { "Tile was unexpected type: $tile" }
        val tileId = tile.name.toInt()
        for (idx in tilesets.indices) {
            val (range, tileset) = tilesets[idx]
            if (tileId in range) {
                tileset.drawTile(tile, surface, position)
                return
            }
        }
    }
}