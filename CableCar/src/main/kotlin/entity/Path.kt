package entity
/**
 * The Path class represents a path of tiles on the game board, including information about the path's tiles,
 * last port, completion status, and starting position.
 *
 * @property tiles a mutable list of Tile objects representing the tiles on the path
 * @property lastPort an integer representing the last port of the path
 * @property complete a boolean indicating if the path is complete or not
 * @property startPos an integer indicating the starting position of the path on the game board.
 */
data class Path(var startPos: Int) {

    var tiles: MutableList<Tile> = mutableListOf()
    var lastPort: Int = -1
    var complete: Boolean = false

    /**
     * copies the object
     */
    fun copy(): Path {
        val newPath = Path(startPos)
        newPath.tiles = tiles
        newPath.lastPort = lastPort
        newPath.complete = complete
        return newPath
    }
}