package org.hexworks.zirconx.api.tiled.ext

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
        fun fromMap(json: Map<String, Any>): TiledMapData =
            TiledMapData(
                json["width"] as Int,
                json["height"] as Int,
                (json["layers"] as List<Map<String, Any>>).map { TiledLayer.fromMap(it) },
                json["tileheight"] as Int,
                json["tilewidth"] as Int,
                (json["tilesets"] as List<Map<String, Any>>).map { TiledTileSet.fromMap(it) }
            )
    }
}
