package entity

/**
 * The TileStack class represents a stack of tiles in the game, including information about the tiles in the stack.
 *
 * @property tiles a mutable list of Tile objects representing the tiles in the stack
 */
data class TileStack(var tiles: MutableList<Tile>) {

    /**
     * copies the object
     */

    fun copy(): TileStack {
        return TileStack(tiles.toMutableList())
    }
}
