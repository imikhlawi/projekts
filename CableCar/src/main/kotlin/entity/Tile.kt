package entity

/**
 * The Tile class represents a tile on the game board, including information about the tile's ports, rotation, and position.
 *
 * @property ports a mutable list of pairs of integers representing the tile's ports (x,y coordinates)
 * @property rotationDegree an integer representing the tile's rotation in degrees
 * @property posX an integer representing the tile's x position on the game board
 * @property posY an integer representing the tile's y position on the game board
 */
data class Tile(var ports: MutableList<Pair<Int, Int>>) {

    var id: Int = -1
    var originalPorts = mutableListOf<Pair<Int, Int>>()
    var rotationDegree = 0
    var posX = 0
    var posY = 0
    init {
        for (pair in ports) {
            originalPorts.add(Pair(pair.first, pair.second))
        }
    }

    /**
     * copies the object
     */

    fun copy(): Tile {
        val nTile = Tile(mutableListOf())
        for (pair in ports) {
            nTile.ports.add(Pair(pair.first, pair.second))
        }
        nTile.originalPorts = originalPorts
        nTile.rotationDegree = rotationDegree
        nTile.posX = posX
        nTile.posY = posY
        nTile.id = id
        return nTile
    }
}
