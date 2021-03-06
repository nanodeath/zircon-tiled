package org.hexworks.zirconx.api.tiled.ext

import org.hexworks.zircon.api.builder.data.TileBuilder
import org.hexworks.zircon.api.builder.graphics.LayerBuilder
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.graphics.Layer
import org.hexworks.zirconx.api.tiled.TiledTilesetResource
import org.hexworks.zirconx.api.tiled.numberToDouble

sealed class TiledLayer {
    internal companion object {
        fun fromMap(map: Map<String, Any>, scale: Size): TiledLayer =
            when (val type = map["type"]) {
                "tilelayer" -> TiledTileLayer.fromMap(map)
                "objectgroup" -> TiledObjectLayer.fromMap(map, scale)
                else -> TODO("layer type $type is not yet supported")
            }
    }
}

class TiledObjectLayer(
    val drawOrder: String,
    val name: String,
    val objects: List<TiledObject>
) : TiledLayer() {
    fun byName(name: String): TiledObject? = objects.firstOrNull { it.name == name }

    fun allByName(name: String): List<TiledObject> = objects.filter { it.name == name }
    fun allByNameSeq(name: String): Sequence<TiledObject> = objects.asSequence().filter { it.name == name }

    internal companion object {
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any>, scale: Size) = TiledObjectLayer(
            map["draworder"] as String,
            map["name"] as String,
            (map["objects"] as List<Map<String, Any>>).map { TiledObject.fromMap(it, scale) }
        )
    }
}

class TiledTileLayer(
    val name: String,
    val data: List<Int>,
    val width: Int,
    val height: Int,
    val x: Int,
    val y: Int,
    val id: Int,
    val opacity: Double,
    val visible: Boolean
) : TiledLayer() {
    fun toLayer(tileset: TiledTilesetResource): Layer {
        val layer = LayerBuilder.newBuilder()
            .withSize(Size.create(width, height))
            .build()

        for (y in 0 until height) {
            for (x in 0 until width) {
                val idx = data[y * height + x]
                if (idx < 1) continue
                layer.draw(
                    tile = TileBuilder.newBuilder()
                        .withName(idx.toString())
                        .withTileset(tileset)
                        .buildGraphicalTile(),
                    drawPosition = Position.create(x, y)
                )
            }
        }
        return layer
    }

    fun getTileAt(x: Int, y: Int): Int? =
        if (x !in 0 until width || y !in 0 until height) {
            null
        } else {
            data[y * height + x]
        }

    internal companion object {
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any>): TiledTileLayer {
            return TiledTileLayer(
                map["name"] as String,
                map["data"] as List<Int>,
                map["width"] as Int,
                map["height"] as Int,
                map["x"] as Int,
                map["y"] as Int,
                map["id"] as Int,
                map["opacity"]?.numberToDouble()!!,
                map["visible"] as Boolean,
            )
        }
    }
}
