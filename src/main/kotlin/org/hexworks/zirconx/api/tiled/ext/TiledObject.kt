package org.hexworks.zirconx.api.tiled.ext

import com.fasterxml.jackson.annotation.*

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION, defaultImpl = TiledObject.BaseObject::class)
@JsonSubTypes(
    JsonSubTypes.Type(TiledObject.Point::class),
    JsonSubTypes.Type(TiledObject.Text::class),
    JsonSubTypes.Type(TiledObject.Tile::class)
)
sealed class TiledObject {
    open class BaseObject : TiledObject() {
        val id: Int = 0
        val name: String = ""
        val x: Double = 0.0
        val y: Double = 0.0
        val width: Double = 0.0
        val height: Double = 0.0
        val rotation: Double = 0.0
        val template: String? = null
        val type: String = ""
        val visible: Boolean = true
    }

    class Tile : BaseObject() {
        @JsonProperty("gid") val rawGid: Long = 0

        @JsonIgnore val gid: Int = (rawGid and FLIPPED_FLAGS.inv()).toInt()

        // https://doc.mapeditor.org/en/stable/reference/tmx-map-format/#tmx-tile-flipping
        @JsonIgnore val flipHorizontally = rawGid and (FLIPPED_HORIZONTALLY_FLAG) != 0L
        @JsonIgnore val flipVertically = rawGid and (FLIPPED_VERTICALLY_FLAG) != 0L
        @JsonIgnore val flipDiagonally = rawGid and (FLIPPED_DIAGONALLY_FLAG) != 0L

        private companion object {
            const val FLIPPED_HORIZONTALLY_FLAG = 0x80000000L
            const val FLIPPED_VERTICALLY_FLAG = 0x40000000L
            const val FLIPPED_DIAGONALLY_FLAG = 0x20000000L
            const val FLIPPED_FLAGS = FLIPPED_HORIZONTALLY_FLAG or FLIPPED_VERTICALLY_FLAG or FLIPPED_DIAGONALLY_FLAG
        }
    }

    class Point : BaseObject() {
        @Suppress("unused")
        val point: Boolean = false
    }

    class Text internal constructor() : BaseObject() {
        @JsonProperty("text") private lateinit var textData: StringAndFormatting

        @get:JsonIgnore val text get() = textData.text
        @get:JsonIgnore val attributes get() = textData.attributes()

        internal class StringAndFormatting private constructor(val text: String) {
            private val attributes = mutableMapOf<String, Any>()

            @JsonAnyGetter
            fun attributes(): Map<String, Any> = attributes

            @JsonAnySetter
            fun setAttribute(name: String, value: Any) {
                attributes[name] = value
            }
        }
    }
}