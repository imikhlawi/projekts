package service

import entity.*
import entity.Color
import entity.Player
import entity.Turn

/**
 * broad game logic
 */

class GameService(private val rootService: RootService) : AbstractRefreshingService() {
    var isLocalOnlyGame = false
    var isHostedGame = false
    var rotationAllowed = false
    var tileLookUp = mutableListOf<Tile>()

    /**
     * initializes game with given values
     */

    fun startNewGame(
        players: List<Player>,
        isLocalOnlyGame: Boolean = true,
        isHostedGame: Boolean = false,
        rotationAllowed: Boolean = false
    ) {
        this.isLocalOnlyGame = isLocalOnlyGame
        this.isHostedGame = isHostedGame
        this.rotationAllowed = rotationAllowed
        if (rootService.currentGame == null) {
            rootService.currentGame = Game(
                Turn(
                    GameField(
                        mutableListOf(), mutableListOf(), TileStack(mutableListOf())
                    ), mutableListOf()
                )
            )

            // add given players
            rootService.currentGame!!.currentTurn.players = players.toMutableList()
            /*for (player in rootService.networkService.joinedPlayers) {
                rootService.currentGame!!.currentTurn.players.add(Player(player, null))
            }*/

            readTileCSV()

            if (isLocalOnlyGame) {
                distributeTiles()
                playersToPositions()
            } else {
                if (isHostedGame) {
                    distributeTiles()
                    playersToPositions()
                } else {
                    // joined game where init data is coming from network
                    distributeTiles()
                    playersToPositions()
                }
            }
        } else {
            println("a game is currently in progress")
        }

        onAllRefreshables { this.refreshAfterStartGame() }
    }


    /**
     * @author Ikhlawi
     * Reverts the game to the previous state. If the current game state has been modified,
     * removes all future game states from the game history.
     *
     * @throws IllegalArgumentException if there is no previous game state.
     */
    fun undo() {
        val currentGame = rootService.currentGame
        checkNotNull(currentGame)
        if (currentGame.currentTurn.previousTurn != null) {
            currentGame.currentTurn = currentGame.currentTurn.previousTurn!!

        }

        onAllRefreshables { this.refreshAfterTurn() }
    }

    /**
     * @author Ikhlawi
     * Moves the game to the next state.
     *
     * @throws IllegalArgumentException if there is no next game state.
     */
    fun redo() {
        val currentGame = rootService.currentGame
        checkNotNull(currentGame)
        if (currentGame.currentTurn.nextTurn != null) {
            currentGame.currentTurn = currentGame.currentTurn.nextTurn!!

        }

        onAllRefreshables { this.refreshAfterTurn() }
    }

    /**
     * iterates currentPlayerIndex over players. Size in a loop
     */
    fun nextPlayer(): Boolean {
        val turn = rootService.currentGame!!.currentTurn

        if (PlayerActionService.isGameOver(turn)) {
            rootService.gameService.endGame()
            return false
        }
        turn.currentPlayerIndex = (turn.currentPlayerIndex + 1) % turn.players.size
        println("It's player's " + turn.currentPlayerIndex + " turn.")

        val isSmartAi = turn.players[turn.currentPlayerIndex].isSmartAi
        if (isSmartAi != null) {
            if (isSmartAi) rootService.playerActionService.playAiTurn(rotationAllowed)
            else rootService.playerActionService.playRandomTurn(rotationAllowed)
        }
        return true
    }

    /**
     * @author Ikhlawi
     * findWinner() is a function that finds the winner of the current game.
     *
     * @return a mutable list of Player objects representing the winners of the current game.
     */
    fun findWinner(): MutableList<Player> {


        // Create a mutable list to store the winners
        val winners = mutableListOf<Player>()

        // Iterate through the players in the current game
        for (player in rootService.currentGame!!.currentTurn.players) {
            // If the player has the maximum score, add them to the winners list
            winners.add(player)
        }
        winners.sortByDescending { it.score }
        // Return the winners list
        return winners
    }

    /**
     * called if all tiles are laid out
     */

    fun endGame() {

        onAllRefreshables { refreshAfterGameFinished() }

    }

    /**
     * distributes shuffled card stack to players
     */

    fun distributeTiles() {
        if (isLocalOnlyGame) {
            // we have to generate the drawStack and give each player a tile
            rootService.currentGame!!.currentTurn.gameField.tileStack.tiles = tileLookUp.toMutableList() // create copy
            rootService.currentGame!!.currentTurn.gameField.tileStack.tiles.shuffle()
            for (player in rootService.currentGame!!.currentTurn.players) {
                player.handTile = rootService.currentGame!!.currentTurn.gameField.tileStack.tiles.removeFirst()
            }
        } else if (isHostedGame) {
            // we have to generate the drawStack, create and send the gameInitMessage and then give each player a tile
            rootService.currentGame!!.currentTurn.gameField.tileStack.tiles = tileLookUp.toMutableList() // create copy
            rootService.currentGame!!.currentTurn.gameField.tileStack.tiles.shuffle()

            rootService.networkService.startNewHostedGame(
                rootService.networkService.playerName,
                this.rotationAllowed,
                rootService.currentGame!!.currentTurn.gameField.tileStack.tiles
            )


            for (player in rootService.currentGame!!.currentTurn.players) {
                player.handTile = rootService.currentGame!!.currentTurn.gameField.tileStack.tiles.removeFirst()
            }
        } else {
            // this is a joined game, we only have to give each player a tile from GameField.TileStack
            for (player in rootService.currentGame!!.currentTurn.players) {
                player.handTile = rootService.currentGame!!.currentTurn.gameField.tileStack.tiles.removeFirst()
            }
        }
        println("Tiles distributed to players")
    }

    /**
     * @author Ikhlawi
     * Assigns number of cars and colors to the players based on the number of players.
     *
     * The number of cars and colors are assigned as follows:
     * - For 2 players: Yellow and Blue, alternating positions
     * - For 3 players: Yellow, Blue, and Orange
     * - For 4 players: Yellow, Blue, Orange, and Green
     * - For 5 players: Yellow, Blue, Orange, Green, and Purple
     * - For 6 players: Yellow, Blue, Orange, Green, Purple, and Black
     *
     */
    fun playersToPositions() {
        val players = rootService.currentGame?.currentTurn?.players
        val colors = listOf(Color.YELLOW, Color.BLUE, Color.ORANGE, Color.GREEN, Color.PURPLE, Color.BLACK)
        when (players?.size) {
            2 -> {
                for (i in 1.. 32) {
                    if (i % 2 == 1) {
                        players[0].color = Color.YELLOW
                        players[0].cars.add(i)
                    }
                    else {
                        players[1].color = Color.BLUE
                        players[1].cars.add(i)
                    }
                }
            }

            3 -> {
                players[0].cars = mutableListOf(1, 4, 6, 11, 15, 20, 23, 25, 28, 31)
                players[1].cars = mutableListOf(2, 7, 9, 12, 14, 19, 22, 27, 29, 32)
                players[2].cars = mutableListOf(3, 5, 8, 10, 13, 18, 21, 24, 26, 30)
                for ((index, player) in players.withIndex()) {
                    player.color = colors[index]
                }
            }

            4 -> {
                players[0].cars = mutableListOf(4, 7, 11, 16, 20, 23, 27, 32)
                players[1].cars = mutableListOf(3, 8, 12, 15, 19, 24, 28, 31)
                players[2].cars = mutableListOf(1, 6, 10, 13, 18, 21, 25, 30)
                players[3].cars = mutableListOf(2, 5, 9, 14, 17, 22, 26, 29)
                for ((index, player) in players.withIndex()) {
                    player.color = colors[index]
                }
            }

            5 -> {
                players[0].cars = mutableListOf(1, 5, 10, 14, 22, 28)
                players[1].cars = mutableListOf(6, 12, 18, 23, 27, 32)
                players[2].cars = mutableListOf(3, 7, 15, 19, 25, 29)
                players[3].cars = mutableListOf(2, 9, 13, 21, 26, 30)
                players[4].cars = mutableListOf(4, 8, 11, 20, 24, 31)
                for ((index, player) in players.withIndex()) {
                    player.color = colors[index]
                }
            }

            6 -> {
                players[0].cars = mutableListOf(1, 5, 10, 19, 27)
                players[1].cars = mutableListOf(2, 11, 18, 25, 29)
                players[2].cars = mutableListOf(4, 8, 14, 21, 26)
                players[3].cars = mutableListOf(6, 15, 20, 24, 31)
                players[4].cars = mutableListOf(3, 9, 13, 23, 30)
                players[5].cars = mutableListOf(7, 12, 22, 28, 32)
                for ((index, player) in players.withIndex()) {
                    player.color = colors[index]
                }
            }
        }
        for (player in players!!) {
            for (pos in player.cars) {
                player.paths.add(Path(pos))
            }
        }
    }

    /**
     * parses tile path connections to draw stack
     */


    fun readTileCSV() {
        // read file lines into lines array

        val reader = javaClass.getResource("/tiles.csv").openStream().bufferedReader()
        val lines = mutableListOf<String>()

        reader.lineSequence().forEach {
            lines.add(it)
        }

        // add ints from lines array into 2D IntArray
        val matrix: Array<IntArray> = Array(60) { IntArray(8) }
        for (i in 0 until lines.size) {
            val lineSplit = lines[i].split(",")
            for (j in lineSplit.indices) {
                matrix[i][j] = lineSplit[j].toInt()
            }
        }

        // create PairList from matrix with row = tile
        val tilePairList: MutableList<MutableList<Pair<Int, Int>>> = mutableListOf()
        for (i in matrix.indices) {
            tilePairList.add(mutableListOf())
            for (j in 0..matrix[0].size - 2 step 2) {
                tilePairList[i].add(Pair(matrix[i][j], matrix[i][j + 1]))
            }
        }

        // create 1D tileList from 2D PairList
        val tileList = mutableListOf<Tile>()
        for (i in 0 until tilePairList.size) {
            tileList.add(Tile(tilePairList[i])) // i+1: Position der Tile in CardDeck in Zusammenhang mit tiles.csv
        }

        // add index to Tile
        for (i in 0 until tileList.size) {
            tileList[i].id = i
        }
        // create tileMap
        this.tileLookUp = tileList
    }

    /**
     * triggers nope sound in ccapp
     */

    fun playNopeSound() {
        onAllRefreshables { playNopeSoundInCCApp() }
    }
}