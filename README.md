# Tiled support for Zircon [Experimental]

This is an experimental module for adding support for [Tiled](https://www.mapeditor.org/) to 
[Zircon](https://github.com/Hexworks/zircon).

## How to Run

You'll need [this fork](https://github.com/nanodeath/zircon/tree/tiled) of Zircon for this to work.

1. Download Tiled.
1. Create a map in Tiled. **Don't** check `Embed in map` when creating your tileset.
1. Choose `File > Export as...` and save your map as a Tiled JSON file.
1. Run `org.hexworks.zirconx.examples.GameKt.main` and pass in the absolute path to your JSON file as the first argument.

## Supported
* Basic tile layers
* Multiple tile layers

## Limitations

* Most things that Tiled supports are unsupported.
* Embedded tilesets
* Layers with a type other than Tile layer
* All layer properties, including visibility, opacity, tint, and offset
* Most tileset properties, except grid height/width
* Tile collisions
* Tile/tileset/map metadata

## Other TODOs

* [x] ~~Switch over to kotlinx.serialization. This depends on being able to switch all of Tiled's outputs to JSON, which
I _think_ we can do.~~ Switched over to naive Jackson object deserialization, which should be easy to replicate in JS implementations.
* [x] Support object layers. See #1.
* [ ] Support tile collision data.
* [ ] Implement the Right Way to handle asset loading instead of just loading files.

## License

See `LICENSE`.

## Notes

Originally branched from [zircon.skeleton.kotlin](https://github.com/Hexworks/zircon.skeleton.kotlin).