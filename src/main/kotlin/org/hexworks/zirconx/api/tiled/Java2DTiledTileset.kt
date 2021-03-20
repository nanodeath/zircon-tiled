package org.hexworks.zirconx.api.tiled

import org.hexworks.zircon.api.data.GraphicalTile
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Tile
import org.hexworks.zirconx.api.tiled.ext.TiledTilesetFile
import org.hexworks.zirconx.internal.tiled.logDuration
import org.hexworks.zirconx.internal.tiled.logDurationNs
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import java.io.InputStream
import javax.imageio.ImageIO

class Java2DTiledTileset internal constructor(
    image: () -> InputStream,
    private val indexOffset: Int,
    private val tileWidth: Int,
    private val tileHeight: Int,
    columns: Int,
    tileCount: Int,
    name: String
) {
    private val image = logDuration("Java2DTiledTileset.image($name)") { ImageIO.read(image()) }

    private val subimageLoader: SubimageLoader = SimpleArrayCache(
        tileCount,
        RealSubimageLoader(this.image, tileWidth, tileHeight, columns)
    )

    fun drawTile(tile: Tile, surface: Graphics2D, position: Position) {
        val idx = (tile as GraphicalTile).name.toInt() - indexOffset
        val texture = logDurationNs("loadTexture", 300_000) { subimageLoader.loadSubimage(idx) }

        val x = position.x * tileWidth
        val y = position.y * tileHeight
        surface.drawImage(texture, x, y, null)
    }

    companion object {
        internal fun from(data: TiledTilesetFile, offset: Int, tileMapFile: File): Java2DTiledTileset {
            val imageFile = tileMapFile.parentFile.resolve(data.image.source)
            val image = { imageFile.inputStream() }
            return logDuration("Java2DTiledTileset.constructor(${imageFile.name})") {
                Java2DTiledTileset(
                    image,
                    offset,
                    data.tilewidth,
                    data.tileheight,
                    data.columns,
                    data.tilecount,
                    imageFile.name
                )
            }
        }
    }
}

private interface SubimageLoader {
    fun loadSubimage(idx: Int): BufferedImage
}

/**
 * Image loader that actually accesses the provided [BufferedImage]. Slow.
 */
private class RealSubimageLoader(
    private val image: BufferedImage,
    private val tileWidth: Int,
    private val tileHeight: Int,
    private val columns: Int
) : SubimageLoader {
    override fun loadSubimage(idx: Int): BufferedImage =
        image.getSubimage(idx % columns * tileWidth, idx / columns * tileHeight, tileWidth, tileHeight)
}

/**
 * Basic array-based cache that can store a fixed number of textures. Not threadsafe.
 */
private class SimpleArrayCache(maxSize: Int, private val delegate: SubimageLoader) : SubimageLoader {
    private val cache: Array<BufferedImage?> = arrayOfNulls(maxSize)

    override fun loadSubimage(idx: Int): BufferedImage =
        when (val cachedValue = cache[idx]) {
            null -> delegate.loadSubimage(idx).also { cache[idx] = it }
            else -> cachedValue
        }
}
