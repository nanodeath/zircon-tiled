package org.hexworks.zirconx.api.tiled

import org.hexworks.zircon.api.data.Position
import org.hexworks.zirconx.api.tiled.ext.TiledTileLayer
import kotlin.NoSuchElementException
import kotlin.collections.ArrayList
import org.hexworks.zircon.api.graphics.Layer as ZirconLayer

class TiledTileLayers internal constructor(
    private val tiledMap: TiledMap,
    private val layers: List<TiledTileLayer>
) : Iterable<TiledTileLayer> {
    private val layersByName: Map<String, TiledTileLayer> = layers.associateBy { it.name }
    private val layerViewsByName: Map<String, TiledTileLayerView> =
        layersByName.mapValues { (_, layer) -> TiledTileLayerView(layer) }

    fun getAllPositions(): Sequence<Position> {
        val maxWidth = layers.maxOf { it.width }
        val maxHeight = layers.maxOf { it.height }
        return sequence {
            for (y in 0 until maxHeight) {
                for (x in 0 until maxWidth) {
                    yield(Position.create(x, y))
                }
            }
        }
    }

    fun getAllStacks(): Sequence<Pair<Position, List<GlobalId>>> =
        getAllPositions().map { pos -> pos to getTilesAt(pos.x, pos.y) }

    fun getTilesAt(x: Int, y: Int): List<GlobalId> =
        layers.mapNotNullTo(ArrayList(layers.size)) { layer ->
            layer.getTileAt(x, y)?.let { GlobalId(it) }
        }

    operator fun get(name: String): TiledTileLayerView =
        layerViewsByName[name] ?: throw NoSuchElementException("No tileLayer by name $name")

    fun toZirconLayers(): List<ZirconLayer> = tiledMap.toZirconLayers(layers)

    override fun iterator() = layers.iterator()
}

class TiledTileLayerView internal constructor(private val layer: TiledTileLayer) {
    fun getTileAt(x: Int, y: Int): GlobalId? = layer.getTileAt(x, y)?.let { GlobalId(it) }
}
