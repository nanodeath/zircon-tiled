package org.hexworks.zirconx.api.tiled

import org.hexworks.zircon.api.builder.data.TileBuilder
import org.hexworks.zircon.api.builder.graphics.LayerBuilder
import org.hexworks.zircon.api.data.GraphicalTile
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zircon.api.graphics.Layer
import org.hexworks.zircon.internal.resource.TilesetSourceType
import org.hexworks.zirconx.api.tiled.ext.*
import java.io.File

/**
 * Main entry point into the Tiled integration for Zircon; most everything you want will be found either in this class
 * or in a field of this class.
 *
 * The tiled map must contain at least one Tile Layer.
 *
 * Note that it's relatively expensive to create, so definitely aim to construct it as few times as possible.
 *
 * Instantiate using [TiledMap.load].
 */
class TiledMap(tiledMapData: TiledMapData, tiledMapFile: File) {
    private val tiledTileLayers = tiledMapData.layers.filterIsInstance<TiledTileLayer>()
    private val tiledObjectLayers = tiledMapData.layers.filterIsInstance<TiledObjectLayer>()

    init {
        require(tiledTileLayers.isNotEmpty()) { "At least one tile layer must be present" }
    }

    private val zirconTilesetResource = TiledTilesetResource(
        TilesetSourceType.FILESYSTEM,
        tiledMapData,
        tiledMapFile.path
    )

    private val tileMapTileset = TiledMapTileset(zirconTilesetResource)

    /**
     * Encapsulates Tiled's Tile Layers, which represents tiles on a grid. Tiles in the layer can't contain extra
     * metadata.
     *
     * See [Tiled docs](https://doc.mapeditor.org/en/stable/manual/layers/#tile-layers).
     */
    val tileLayers = TiledTileLayers(this, tiledTileLayers)

    /**
     * Encapsulates Tiled's Object Layers, which represents objects (points, rectangles, polygons, etc) with sub-grid
     * positions. Objects can have their own metadata, or if that object is tile, can share (and override) metadata
     * from the original tile.
     *
     * See [Tiled docs](https://doc.mapeditor.org/en/stable/manual/layers/#object-layers).
     */
    val objectLayers = TiledObjectLayers(tiledObjectLayers)

    /**
     * Encapsulates Tiled's Tilesets, which represent a collection of small images (often implemented as a large
     * combined image).
     *
     * See [Tiled docs](https://doc.mapeditor.org/en/stable/manual/editing-tilesets/).
     */
    val tilesets = TiledTilesets(tileMapTileset)

    /**
     * Constructs a Zircon [GraphicalTile] based on the provided [id]. You can customize the built tile using
     * [builder], which is invoked after this function initializes necessary attributes.
     */
    fun createZirconTile(id: GlobalId, builder: (TileBuilder) -> TileBuilder = { it }): GraphicalTile =
        Tile.newBuilder()
            .withName(id.value.toString())
            .withTileset(zirconTilesetResource)
            .let(builder)
            .buildGraphicalTile()

    /**
     * Creates a new Zircon [LayerBuilder] pre-initialized with Tiled-relevant configuration.
     *
     * @param guessSize if true, call [LayerBuilder.withSize] with the maximum width and height of any tile layer.
     *   If false, don't call [LayerBuilder.withSize].
     */
    fun initializeZirconLayer(guessSize: Boolean = true): LayerBuilder =
        updateZirconLayer(
            LayerBuilder.newBuilder(),
            guessSize = guessSize
        )

    /**
     * Updates the provided Zircon [builder] with Tiled-relevant configuration.
     *
     * @param guessSize if true, call [LayerBuilder.withSize] with the maximum width and height of any tile layer.
     *   If false, don't call [LayerBuilder.withSize].
     */
    fun updateZirconLayer(
        builder: LayerBuilder,
        guessSize: Boolean = true
    ): LayerBuilder =
        builder.withTileset(zirconTilesetResource).apply {
            if (guessSize) {
                withSize(tiledTileLayers.maxOf { it.width }, tiledTileLayers.maxOf { it.height })
            }
        }

    /**
     * Convert a [GlobalId] to a [LocalId] by referring to the [TiledMapTileset].
     */
    fun GlobalId.toLocalId(): LocalId {
        val (range, _) = tileMapTileset.lookupTileset(this)
        return toLocalId(range.first)
    }

    internal fun toZirconLayers(layers: List<TiledTileLayer>): List<Layer> =
        layers.map { it.toLayer(zirconTilesetResource) }

    /**
     * Resolve a [TilesetTile] given a [GlobalId].
     *
     * @throws NoSuchElementException if [id] is not in range for any associated tilesets.
     * @return either the [TilesetTile] if one was found, otherwise [TilesetTile.empty].
     */
    fun lookupTile(id: GlobalId): TilesetTile {
        val (range, tileset: TiledTilesetFile) = tileMapTileset.lookupTileset(id)
        return tileset.tiles[id.toLocalId(range.first).value].orEmpty()
    }

    companion object {
        /**
         * Loads the given Tiled map and associated tilesets and images.
         */
        fun load(file: File): TiledMap {
            val tiledMapFile = deserializeJson(file).let { TiledMapData.fromMap(it) }
            return TiledMap(tiledMapFile, file)
        }
    }
}