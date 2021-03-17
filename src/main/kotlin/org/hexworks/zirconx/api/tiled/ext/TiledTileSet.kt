package org.hexworks.zirconx.api.tiled.ext

data class TiledTileSet(
    val firstGid: Int,
    val source: String
) {
    internal companion object {
        fun fromMap(map: Map<String, Any>): TiledTileSet {
            val source = map["source"] as String
            require(source.endsWith(".json", ignoreCase = true)) { "Only JSON tilesets are supported: $source" }
            return TiledTileSet(
                map["firstgid"] as Int,
                source
            )
        }
    }
}