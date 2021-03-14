package org.hexworks.zirconx.api.tiled

import com.github.benmanes.caffeine.cache.Caffeine
import org.hexworks.zircon.api.data.GraphicalTile
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zirconx.api.tiled.ext.TiledTilesetFile
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import java.io.InputStream
import javax.imageio.ImageIO

class Java2DTiledTileset(
    image: () -> InputStream,
    private val indexOffset: Int,
    private val tileWidth: Int,
    private val tileHeight: Int,
    private val columns: Int
) {
    private val image = ImageIO.read(image())
    private val textureCache = Caffeine.newBuilder()
        .maximumSize(1024L)
        .build<Int, BufferedImage>()

    fun drawTile(tile: Tile, surface: Graphics2D, position: Position) {
        val idx = (tile as GraphicalTile).name.toInt() - indexOffset
        val texture = textureCache.get(idx, this::loadTexture)

        val x = position.x * tileWidth
        val y = position.y * tileHeight
        surface.drawImage(texture, x, y, null)
    }

    private fun loadTexture(tileIdx: Int): BufferedImage =
        image.getSubimage(tileIdx % columns * tileWidth, tileIdx / columns * tileHeight, tileWidth, tileHeight)

    companion object {
        fun from(data: TiledTilesetFile, offset: Int, tileMapFile: File): Java2DTiledTileset {
            val image = { tileMapFile.parentFile.resolve(data.image.source).inputStream() }
            return Java2DTiledTileset(image, offset, data.tilewidth, data.tileheight, data.columns)
        }
    }
}