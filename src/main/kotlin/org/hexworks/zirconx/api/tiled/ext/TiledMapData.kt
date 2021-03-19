package org.hexworks.zirconx.api.tiled.ext

import org.hexworks.zircon.api.data.Size

data class TiledMapData(
    val width: Int,
    val height: Int,
    val layers: List<TiledLayer>,
    val tileHeight: Int,
    val tileWidth: Int,
    val tilesets: List<TiledTileSet>
) {
    internal companion object {
        @Suppress("UNCHECKED_CAST")
        fun fromMap(json: Map<String, Any>): TiledMapData {
            val tileHeight = json["tileheight"] as Int
            val tileWidth = json["tilewidth"] as Int
            return TiledMapData(
                json["width"] as Int,
                json["height"] as Int,
                (json["layers"] as List<Map<String, Any>>).map { TiledLayer.fromMap(it, Size.create(tileWidth, tileHeight)) },
                tileHeight,
                tileWidth,
                (json["tilesets"] as List<Map<String, Any>>).map { TiledTileSet.fromMap(it) }
            )
        }
    }
}
