package org.hexworks.zirconx.api.tiled.ext

import com.fasterxml.jackson.annotation.JsonProperty

data class TiledMapData(
    val width: Int,
    val height: Int,
    val layers: List<TiledLayer>,
    @JsonProperty("tileheight") val tileHeight: Int,
    @JsonProperty("tilewidth") val tileWidth: Int,
    val tilesets: List<TiledTileSet>
)
