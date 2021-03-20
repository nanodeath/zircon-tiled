package org.hexworks.zirconx.api.tiled.ext

import org.hexworks.zircon.api.data.Size
import org.hexworks.zirconx.api.tiled.numberToDouble

class TiledTilesetFile(
    val name: String,
    val tilewidth: Int,
    val tileheight: Int,
    val tilecount: Int,
    val columns: Int,
    val image: Image,
    val tiles: Map<Int, TilesetTile>
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
        fun fromMap(map: Map<String, Any>): TiledTilesetFile {
            val tileWidth = map["tilewidth"] as Int
            val tileHeight = map["tileheight"] as Int
            return TiledTilesetFile(
                map["name"] as String,
                tileWidth,
                tileHeight,
                map["tilecount"] as Int,
                map["columns"] as Int,
                Image.fromMap(map),
                (map["tiles"] as List<Map<String, Any>>).asSequence()
                    .map { TilesetTile.fromMap(it, Size.create(tileWidth, tileHeight)) }
                    .map { it.id to it }.toMap()
            )
        }
    }
}

class TilesetTile(
    val id: Int,
    val image: String?,
    val imageHeight: Int?,
    val imageWidth: Int?,
    /** collisions */
    val objectGroup: TiledObjectLayer?,
    val probability: Double?,
    val properties: Map<String, Any>,
    val terrain: IntArray?,
    val type: String?
) {

    companion object {
        val empty = TilesetTile(0, null, null, null, null, null, emptyMap(), null, null)

        @Suppress("UNCHECKED_CAST")
        internal fun fromMap(map: Map<String, Any>, scale: Size): TilesetTile {
            return TilesetTile(
                map["id"] as Int,
                map["image"] as String?,
                map["imageheight"] as Int?,
                map["imagewidth"] as Int?,
                (map["objectgroup"] as Map<String, Any>?)?.let { TiledObjectLayer.fromMap(it, scale) },
                map["probability"]?.numberToDouble(),
                (map["properties"] as List<Map<String, Any>>?).let { parseProperties(it) },
                (map["terrain"] as List<Int>?)?.toIntArray(),
                map["type"] as String?
            )
        }

        private fun parseProperties(properties: List<Map<String, Any>>?): Map<String, Any> {
            if (properties == null) {
                return emptyMap()
            }
            return properties.asSequence()
                .map { it["name"] as String to it["value"]!! }
                .toMap()
        }
    }
}

@Suppress("NOTHING_TO_INLINE")
internal fun TilesetTile?.orEmpty(): TilesetTile = this ?: TilesetTile.empty