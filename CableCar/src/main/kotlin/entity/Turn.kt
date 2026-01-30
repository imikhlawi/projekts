package entity

/**
 * The Turn class represents a turn in the game, including information about the game field, players,
 * previous turn, next turn and the index of the current player.
 *
 * @property gameField a GameField object representing the current state of the game field
 * @property players a mutable list of Player objects representing the players in the game
 * @property previousTurn a Turn object representing the previous turn
 * @property nextTurn a Turn object representing the next turn
 * @property currentPlayerIndex an integer representing the index of the current player in the players list
 */
data class Turn(var gameField: GameField, var players: MutableList<Player>) {

    var previousTurn : Turn? = null
    var nextTurn : Turn? = null
    var currentPlayerIndex : Int = 0

    /**
     * copies the object
     */
    fun copy(): Turn {
        val nPlayers = mutableListOf<Player>()
        for (player in players) {
            nPlayers.add(player.copy())
        }
        val nTurn = Turn(gameField.copy(), nPlayers)
        nTurn.currentPlayerIndex = currentPlayerIndex
        return nTurn
    }
}