package org.hexworks.zirconx.api.tiled.ext

class TiledTilesetFile(
    val tilewidth: Int,
    val tileheight: Int,
    val tilecount: Int,
    val columns: Int,
    val image: Image
) {
    class Image(val source: String, val width: Int, val height: Int)
}