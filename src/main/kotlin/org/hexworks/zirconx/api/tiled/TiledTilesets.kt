package org.hexworks.zirconx.api.tiled

import org.hexworks.zirconx.api.tiled.ext.TiledTilesetFile
import org.hexworks.zirconx.api.tiled.ext.TilesetTile
import org.hexworks.zirconx.api.tiled.ext.orEmpty

class TiledTilesets(private val tiledMapTileset: TiledMapTileset) {
    fun toTilesetTile(id: GlobalId): TilesetTile = with(tiledMapTileset) { id.toTilesetTile() }

    fun lookupTile(id: GlobalId): TilesetTile = tiledMapTileset.lookupTile(id)

    fun queryTile(predicate: (TilesetTile) -> Boolean): TilesetTile {
        val (_, tile) = tiledMapTileset.findTile(predicate)
        return tile
    }

    fun queryTileId(predicate: (TilesetTile) -> Boolean): GlobalId {
        val (id, _) = tiledMapTileset.findTile(predicate)
        return id
    }

    fun tileset(name: String): TiledTilesetView {
        val (range, tilesetFile) = tiledMapTileset.byName(name)
        return TiledTilesetView(tilesetFile, offset = range.first)
    }
}

class TiledTilesetView(private val tilesetData: TiledTilesetFile, private val offset: Int) {
    fun lookupTile(id: GlobalId): TilesetTile = lookupTile(id.toLocalId())

    fun lookupTile(id: LocalId): TilesetTile = tilesetData.tiles[id.value].orEmpty()

    private fun GlobalId.toLocalId() = LocalId(this.value - offset)
}