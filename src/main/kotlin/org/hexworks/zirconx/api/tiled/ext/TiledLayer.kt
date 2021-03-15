package org.hexworks.zirconx.api.tiled.ext

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.hexworks.zircon.api.builder.data.TileBuilder
import org.hexworks.zircon.api.builder.graphics.LayerBuilder
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.graphics.Layer
import org.hexworks.zirconx.api.tiled.TiledTilesetResource

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(TiledObjectLayer::class, name = "objectgroup"),
    JsonSubTypes.Type(TiledTileLayer::class, name = "tilelayer")
)
sealed class TiledLayer

class TiledObjectLayer(
    @JsonProperty("draworder") val drawOrder: String,
    val name: String,
    val objects: List<TiledObject>
) : TiledLayer()

class TiledTileLayer(
    val data: IntArray,
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
                layer.draw(
                    tile = TileBuilder.newBuilder()
                        .withName(data[y * height + x].toString())
                        .withTileset(tileset)
                        .buildGraphicalTile(),
                    drawPosition = Position.create(x, y)
                )
            }
        }
        return layer
    }
}