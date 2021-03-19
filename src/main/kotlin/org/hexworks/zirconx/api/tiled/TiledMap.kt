package org.hexworks.zirconx.api.tiled

import org.hexworks.zircon.api.data.GraphicalTile
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.graphics.Layer
import org.hexworks.zircon.internal.resource.TilesetSourceType
import org.hexworks.zirconx.api.tiled.ext.*
import java.io.File

class TiledMap(private val tiledMapData: TiledMapData, private val tiledMapFile: File) {
    val tilesetResource: TiledTilesetResource by lazy {
        TiledTilesetResource(
            TilesetSourceType.FILESYSTEM,
            tiledMapData,
            tiledMapFile.path
        )
    }

    private val tileMapTileset: TiledMapTileset by lazy {
        TiledMapTileset(tilesetResource)
    }

    fun toLayerList(): List<Layer> =
        tiledMapData.layers
            .filterIsInstance<TiledTileLayer>()
            .map { it.toLayer(tilesetResource) }

    fun getObjectLayer(name: String): TiledObjectLayer = objectLayers.first { it.name == name }

    val objectLayers get() = tiledMapData.layers.filterIsInstance<TiledObjectLayer>()

    fun findTileBy(predicate: (TilesetTile) -> Boolean): GraphicalTile {
        val (tileIdx, _) = tileMapTileset.findTile(predicate)
        return Tile.createGraphicTile(name = tileIdx.toString(), tags = emptySet(), tileset = tilesetResource)
    }

    companion object {
        fun loadTiledFile(file: File): TiledMap {
            val tiledMapFile = deserializeJson(file).let { TiledMapData.fromMap(it) }
            return TiledMap(tiledMapFile, file)
        }
    }
}

fun TiledMap.getObjectByName(name: String, layerName: String? = null): TiledObject? =
    getAllObjectsByNameSeq(name, layerName).firstOrNull()

fun TiledMap.getAllObjectsByName(name: String, layerName: String? = null): List<TiledObject> =
    getAllObjectsByNameSeq(name, layerName).toList()

fun TiledMap.getAllObjectsByNameSeq(name: String, layerName: String? = null): Sequence<TiledObject> =
    if (layerName == null) {
        objectLayers.asSequence().flatMap { it.allByNameSeq(name) }
    } else {
        getObjectLayer(layerName).allByNameSeq(name)
    }