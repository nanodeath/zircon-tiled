package org.hexworks.zirconx.api.tiled

import org.hexworks.cobalt.core.api.UUID
import org.hexworks.zircon.api.resource.TilesetResource
import org.hexworks.zircon.api.tileset.Tileset
import org.hexworks.zircon.api.tileset.TilesetLoader
import org.hexworks.zircon.internal.tileset.SwingTilesetLoader.Companion.getLoaderKey
import java.awt.Graphics2D

class TiledSwingGraphicTilesetLoader : TilesetLoader<Graphics2D> {
    private val tilesetCache = mutableMapOf<UUID, Tileset<Graphics2D>>()

    override fun loadTilesetFrom(resource: TilesetResource): Tileset<Graphics2D> =
            tilesetCache.getOrPut(resource.id) { TiledMapTileset(resource) }

    override fun canLoadResource(resource: TilesetResource): Boolean =
            resource.tilesetSubtype == TiledTilesetResource.SUBTYPE
}