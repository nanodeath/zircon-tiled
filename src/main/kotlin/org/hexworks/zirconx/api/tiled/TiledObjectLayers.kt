package org.hexworks.zirconx.api.tiled

import org.hexworks.zircon.api.data.Position
import org.hexworks.zirconx.api.tiled.ext.TiledObject
import org.hexworks.zirconx.api.tiled.ext.TiledObjectLayer
import kotlin.math.floor

class TiledObjectLayers internal constructor(layers: List<TiledObjectLayer>): Iterable<TiledObjectLayerView> {
    private val layersByName = layers.associateBy { it.name }
    private val layerViews = layersByName.mapValues { (name, layer) -> TiledObjectLayerView(name, layer) }

    operator fun get(name: String): TiledObjectLayerView = requireNotNull(layerViews[name]) { "No objectLayer by name $name" }

    override fun iterator(): Iterator<TiledObjectLayerView> = layerViews.values.iterator()
}

class TiledObjectLayerView internal constructor(val name: String, private val layerData: TiledObjectLayer): Iterable<TiledObject> {
    private val byName = layerData.objects.associateBy { it.name }

    operator fun get(name: String): TiledObject = byName[name] ?: throw NoSuchElementException()

    override fun iterator(): Iterator<TiledObject> = layerData.objects.iterator()
}

fun TiledObjectLayerView.objectsAt(x: Int, y: Int): List<TiledObject> =
    filter { obj -> floor(obj.x).toInt() == x && floor(obj.y).toInt() == y }

fun TiledObjectLayerView.objectsAt(pos: Position) =
    objectsAt(pos.x, pos.y)