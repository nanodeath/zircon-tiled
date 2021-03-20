package org.hexworks.zirconx.api.tiled

import org.hexworks.cobalt.core.api.UUID
import org.hexworks.zircon.api.data.GraphicalTile
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.resource.TilesetResource
import org.hexworks.zircon.api.tileset.Tileset
import org.hexworks.zirconx.api.tiled.ext.*
import org.hexworks.zirconx.internal.tiled.logDuration
import java.awt.Graphics2D
import java.io.File

class TiledMapTileset(private val tilesetResource: TilesetResource) : Tileset<Graphics2D> {
    override val height: Int get() = tilesetResource.height
    override val width: Int get() = tilesetResource.width
    override val targetType = Graphics2D::class
    override val id: UUID = tilesetResource.id
    private val originalTilesetsByRange: List<Pair<IntRange, TiledTilesetFile>>
    private val tilesets: List<Pair<IntRange, Java2DTiledTileset>>

    init {
        tilesetResource as TiledTilesetResource
        logDuration("TiledMapTileset.constructor") {
            val tilesetData: Map<String, TiledTilesetFile> = logDuration("TiledMapTileset.tilesetData") {
                tilesetResource.tiledMapData.tilesets.map { tiledTileSet: TiledTileSet ->
                    tiledTileSet.source to deserializeJson(File(tilesetResource.path).resolveSibling(tiledTileSet.source))
                        .let { TiledTilesetFile.fromMap(it) }
                }
                    .toMap()
            }
            originalTilesetsByRange = logDuration("TiledMapTileset.originalTilesetsByRange") {
                tilesetResource.tiledMapData.tilesets
                    .map { tiledTileSet: TiledTileSet ->
                        val tiledTilesetFile = tilesetData[tiledTileSet.source]!!
                        val range = tiledTileSet.firstGid until (tiledTileSet.firstGid + tiledTilesetFile.tilecount)
                        range to tiledTilesetFile
                    }
            }
            tilesets = logDuration("TiledMapTileset.tilesets") {
                tilesetResource.tiledMapData.tilesets
                    .map { tiledTileSet: TiledTileSet ->
                        val tiledTilesetFile = tilesetData[tiledTileSet.source]!!
                        val range = tiledTileSet.firstGid until (tiledTileSet.firstGid + tiledTilesetFile.tilecount)
                        range to Java2DTiledTileset.from(
                            tiledTilesetFile,
                            tiledTileSet.firstGid,
                            File(tilesetResource.path)
                        )
                    }
            }
        }
    }

    fun findTile(predicate: (TilesetTile) -> Boolean): Pair<GlobalId, TilesetTile> {
        for (idx in originalTilesetsByRange.indices) {
            val (range, tileset: TiledTilesetFile) = originalTilesetsByRange[idx]
            for ((tileIdx, tile) in tileset.tiles) {
                if (predicate(tile)) {
                    return LocalId(tileIdx).toGlobalId(range.first) to tile
                }
            }
        }
        throw NoSuchElementException()
    }

    fun lookupTile(id: GlobalId): TilesetTile {
        for ((range, file) in originalTilesetsByRange) {
            if (id.value in range) {
                return file.tiles[id.toLocalId(range.first).value].orEmpty()
            }
        }
        throw NoSuchElementException()
    }

    fun byName(name: String): Pair<IntRange, TiledTilesetFile> =
        originalTilesetsByRange.first { (_, file) -> file.name == name }

    fun lookupTileset(id: GlobalId): Pair<IntRange, TiledTilesetFile> =
        originalTilesetsByRange.first { (range, _) -> id.value in range }

    fun GlobalId.toTilesetTile(): TilesetTile {
        if (this.value == 0) return TilesetTile.empty
        for (idx in originalTilesetsByRange.indices) {
            val (range, tileset: TiledTilesetFile) = originalTilesetsByRange[idx]
            if (this.value in range) {
                return tileset.tiles[this.toLocalId(range.first).value].orEmpty()
            }
        }
        throw NoSuchElementException()
    }

    override fun drawTile(tile: Tile, surface: Graphics2D, position: Position) {
        require(tile is GraphicalTile) { "Tile was unexpected type: $tile" }
        if (tile.name == "0") {
            // 0 is a special case meaning there's actually no tile there
            return
        }
        val tileId = tile.name.toInt()
        for (idx in tilesets.indices) {
            val (range, tileset) = tilesets[idx]
            if (tileId in range) {
                tileset.drawTile(tile, surface, position)
                return
            }
        }
        error("Unable to map tile $tile to a tileset")
    }
}