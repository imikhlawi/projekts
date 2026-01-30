package entity
/**
 * The GameField class represents the current state of the game board, including information about the stations,
 * tiles, and tileStack.
 *
 * @property stations a mutable list of Station objects, representing the game's stations
 * @property tiles a mutable list of Tile objects, representing the game's tiles
 * @property tileStack a TileStack object, representing the stack of tiles available for players to play
 * @property field a 2D array of Tile objects 10x10, representing the game board
 */
data class GameField(var stations: MutableList<Station>, var tiles: MutableList<Tile>, var tileStack: TileStack) {
    var field: Array<Array<Tile?>> = Array(10) { Array(10) { null } }

    /**
     * copies the object
     */

    fun copy(): GameField {
        val nGameField = GameField(stations.toMutableList(), tiles.toMutableList(), tileStack.copy())
        for ((i, arr) in field.withIndex()) {
            for ((j, tile) in arr.withIndex()) {
                nGameField.field[i][j] = tile?.copy()
            }
        }
        return nGameField
    }
}