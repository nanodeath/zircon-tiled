package org.hexworks.zirconx.api.tiled

import org.hexworks.cobalt.core.api.UUID
import org.hexworks.cobalt.core.platform.factory.UUIDFactory
import org.hexworks.zircon.api.resource.TilesetResource
import org.hexworks.zircon.internal.resource.TileType
import org.hexworks.zircon.internal.resource.TilesetSourceType
import org.hexworks.zircon.internal.resource.TilesetType
import org.hexworks.zirconx.api.tiled.ext.TiledMapData
import org.hexworks.zirconx.api.tiled.ext.TiledTileSet

/**
 * This is a [TilesetResource] that describes the [TiledMap] itself, _not_ individual [TiledTileSets][TiledTileSet]
 */
class TiledTilesetResource(
    override val tilesetSourceType: TilesetSourceType,
    internal val tiledMapData: TiledMapData,
    override val path: String // map of the Tiled map file
) : TilesetResource {
    override val id: UUID = UUIDFactory.randomUUID()
    override val tileType = TileType.GRAPHIC_TILE
    override val tilesetType = TilesetType.GRAPHIC_TILESET
    override val tilesetSubtype = SUBTYPE
    override val width: Int = tiledMapData.tileWidth
    override val height: Int = tiledMapData.tileHeight

    companion object {
        const val SUBTYPE = "TILED_MAP"
    }
}