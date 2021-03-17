package org.hexworks.zirconx.api.tiled

import org.hexworks.zircon.api.graphics.Layer
import org.hexworks.zircon.internal.resource.TilesetSourceType
import org.hexworks.zirconx.api.tiled.ext.TiledMapData
import org.hexworks.zirconx.api.tiled.ext.TiledObjectLayer
import org.hexworks.zirconx.api.tiled.ext.TiledTileLayer
import java.io.File

class TiledMap(private val tiledMapData: TiledMapData, private val tiledMapFile: File) {
    fun toLayerList(): List<Layer> =
        tiledMapData.layers
            .filterIsInstance<TiledTileLayer>()
            .map { it.toLayer(TiledTilesetResource(TilesetSourceType.FILESYSTEM, tiledMapData, tiledMapFile.path)) }

    val objectLayers by lazy {
        tiledMapData.layers
            .filterIsInstance<TiledObjectLayer>()
    }

    companion object {
        fun loadTiledFile(file: File): TiledMap {
            val tiledMapFile = deserializeJson(file).let { TiledMapData.fromMap(it) }
            return TiledMap(tiledMapFile, file)
        }
    }
}