package org.hexworks.zirconx.api.tiled

import org.hexworks.zircon.api.resource.TilesetResource
import org.hexworks.zircon.api.tileset.Tileset
import org.hexworks.zircon.internal.resource.TileType
import org.hexworks.zircon.internal.resource.TilesetType
import org.hexworks.zircon.internal.tileset.SwingTilesetLoader
import java.awt.Graphics2D

class TiledSwingGraphicTilesetLoader : SwingTilesetLoader.CustomLoader {
    override val loaderKey = "${TileType.GRAPHIC_TILE}-${TilesetType.GRAPHIC_TILESET}-${TiledTilesetResource.SUBTYPE}"

    override fun load(tilesetResource: TilesetResource): Tileset<Graphics2D> =
        TiledMapTileset(tilesetResource)
}