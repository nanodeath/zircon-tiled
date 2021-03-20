package org.hexworks.zirconx.api.tiled

import org.hexworks.zircon.api.builder.data.TileBuilder
import org.hexworks.zircon.api.builder.graphics.LayerBuilder
import org.hexworks.zircon.api.data.GraphicalTile
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.graphics.Layer
import org.hexworks.zircon.internal.resource.TilesetSourceType
import org.hexworks.zirconx.api.tiled.ext.*
import java.io.File

class TiledMap(tiledMapData: TiledMapData, tiledMapFile: File) {
    private val actualTileLayers = tiledMapData.layers.filterIsInstance<TiledTileLayer>()
    private val actualObjectLayers = tiledMapData.layers.filterIsInstance<TiledObjectLayer>()

    init {
        require(actualTileLayers.isNotEmpty()) { "At least one tile layer must be present" }
    }

    private val zirconTilesetResource = TiledTilesetResource(
        TilesetSourceType.FILESYSTEM,
        tiledMapData,
        tiledMapFile.path
    )

    private val tileMapTileset = TiledMapTileset(zirconTilesetResource)
    val tileLayers = TiledTileLayers(this, actualTileLayers)
    val objectLayers = TiledObjectLayers(actualObjectLayers)
    val tilesets = TiledTilesets(tileMapTileset)

    fun createZirconTile(id: GlobalId, builder: (TileBuilder) -> TileBuilder = { it }): GraphicalTile =
        Tile.newBuilder()
            .withName(id.value.toString())
            .withTileset(zirconTilesetResource)
            .let(builder)
            .buildGraphicalTile()

    fun initializeZirconLayer(guessSize: Boolean = true): LayerBuilder =
        updateZirconLayer(
            LayerBuilder.newBuilder(),
            guessSize = guessSize
        )

    fun updateZirconLayer(
        builder: LayerBuilder,
        guessSize: Boolean = true
    ): LayerBuilder =
        builder.withTileset(zirconTilesetResource).apply {
            if (guessSize) {
                withSize(actualTileLayers.maxOf { it.width }, actualTileLayers.maxOf { it.height })
            }
        }

    fun GlobalId.toLocalId(): LocalId {
        val (range, _) = tileMapTileset.lookupTileset(this)
        return toLocalId(range.first)
    }

    internal fun toZirconLayers(layers: List<TiledTileLayer>): List<Layer> =
        layers.map { it.toLayer(zirconTilesetResource) }

    fun lookupTile(id: GlobalId): TilesetTile {
        val (range, tileset: TiledTilesetFile) = tileMapTileset.lookupTileset(id)
        return tileset.tiles[id.toLocalId(range.first).value].orEmpty()
    }

    companion object {
        fun load(file: File): TiledMap {
            val tiledMapFile = deserializeJson(file).let { TiledMapData.fromMap(it) }
            return TiledMap(tiledMapFile, file)
        }
    }
}