package org.hexworks.zirconx.api.tiled.ext

class TiledTilesetFile(
    val tilewidth: Int,
    val tileheight: Int,
    val tilecount: Int,
    val columns: Int,
    val image: Image
) {
    class Image(val source: String, val width: Int, val height: Int) {
        companion object {
            fun fromMap(map: Map<String, Any>): Image = Image(
                map["image"] as String,
                map["imagewidth"] as Int,
                map["imageheight"] as Int
            )
        }
    }

    internal companion object {
        fun fromMap(map: Map<String, Any>) =
            TiledTilesetFile(
                map["tilewidth"] as Int,
                map["tileheight"] as Int,
                map["tilecount"] as Int,
                map["columns"] as Int,
                Image.fromMap(map)
            )
    }
}