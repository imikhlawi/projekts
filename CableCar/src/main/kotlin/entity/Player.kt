package entity
/**
 * The Player class represents a player in the game, including information about the player's name, cars, color,
 * paths, hand tile, and score.
 *
 * @property name a string representing the player's name
 * @property cars a mutable list of integers representing the cars the player has
 * @property color a Color enumeration representing the color the player has chosen
 * @property paths a mutable list of Path objects representing the paths the player has
 * @property handTile a Tile object representing the tile the player holds in their hand
 * @property score an integer representing the player's score
 */
data class Player(var name: String, val isSmartAi: Boolean?) {

    var cars = mutableListOf<Int>()
    var color: Color? = null
    var paths = mutableListOf<Path>()
    var handTile: Tile? = null
    var score = 0

    /**
     * copies the object
     */

    fun copy(): Player {
        val nPlayer = Player(name, isSmartAi)
        nPlayer.cars = cars.toMutableList()
        nPlayer.color = color
        nPlayer.paths = paths.toMutableList()

        if (handTile != null)
            nPlayer.handTile = handTile!!.copy()
        else nPlayer.handTile = null

        nPlayer.score = score
        return nPlayer
    }

}