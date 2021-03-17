package org.hexworks.zirconx.api.tiled.ext

import org.hexworks.zirconx.api.tiled.numberToDouble
import org.hexworks.zirconx.api.tiled.numberToLong

sealed class TiledObject {
    open class BaseObject : TiledObject() {
        var id: Int = 0
            internal set
        var name: String = ""
            internal set
        var x: Double = 0.0
            internal set
        var y: Double = 0.0
            internal set
        var width: Double = 0.0
            internal set
        var height: Double = 0.0
            internal set
        var rotation: Double = 0.0
            internal set
        var template: String? = null
            internal set
        var type: String = ""
            internal set
        var visible: Boolean = true
            internal set
    }

    class Tile internal constructor(rawGid: Long) : BaseObject() {
        val gid: Int = (rawGid and FLIPPED_FLAGS.inv()).toInt()

        // https://doc.mapeditor.org/en/stable/reference/tmx-map-format/#tmx-tile-flipping
        val flipHorizontally = rawGid and (FLIPPED_HORIZONTALLY_FLAG) != 0L
        val flipVertically = rawGid and (FLIPPED_VERTICALLY_FLAG) != 0L
        val flipDiagonally = rawGid and (FLIPPED_DIAGONALLY_FLAG) != 0L

        companion object {
            private const val FLIPPED_HORIZONTALLY_FLAG = 0x80000000L
            private const val FLIPPED_VERTICALLY_FLAG = 0x40000000L
            private const val FLIPPED_DIAGONALLY_FLAG = 0x20000000L
            private const val FLIPPED_FLAGS =
                FLIPPED_HORIZONTALLY_FLAG or FLIPPED_VERTICALLY_FLAG or FLIPPED_DIAGONALLY_FLAG

            internal fun fromMap(map: Map<String, Any>) =
                Tile(
                    map["gid"]?.numberToLong()!!
                )
        }
    }

    class Point internal constructor() : BaseObject()

    class Text internal constructor(val text: String, val attributes: Map<String, Any>) : BaseObject() {
        internal companion object {
            @Suppress("UNCHECKED_CAST")
            fun fromMap(map: Map<String, Any>): Text {
                val attributes = map["text"] as Map<String, Any>
                val text = attributes["text"] as String
                return if (attributes.size > 1) {
                    Text(text, attributes - "text")
                } else {
                    Text(text, emptyMap())
                }
            }
        }
    }

    internal companion object {
        fun fromMap(map: Map<String, Any>): TiledObject {
            return (when {
                map["point"] == true -> Point()
                map["text"] != null -> Text.fromMap(map)
                map["gid"] != null -> Tile.fromMap(map)
                else -> BaseObject()
            }).also { it.populateFromMap(map) }
        }

        private fun BaseObject.populateFromMap(map: Map<String, Any>) {
            id = map["id"] as Int
            name = map["name"] as String
            x = map["x"]?.numberToDouble()!!
            y = map["y"]?.numberToDouble()!!
            width = map["width"]?.numberToDouble()!!
            height = map["height"]?.numberToDouble()!!
            rotation = map["rotation"]?.numberToDouble()!!
            template = map["template"] as String?
            type = map["type"] as String
            visible = map["visible"] as Boolean
        }
    }
}