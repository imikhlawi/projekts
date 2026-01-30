package service

import ai.MCTS
import edu.udo.cs.sopra.ntf.GameStateVerificationInfo
import edu.udo.cs.sopra.ntf.TileInfo
import edu.udo.cs.sopra.ntf.TurnMessage
import entity.GameField
import entity.Player
import entity.Tile
import entity.Turn
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout

/**
 * Player action service: class to handle player ingame actions
 *
 * @property rootService
 * @constructor Create empty Player action service
 */

class PlayerActionService(private val rootService: RootService) : AbstractRefreshingService() {

    /**
     * Place tile
     *
     * @param fromHand
     * @param posX
     * @param posY
     * @param rotationDegree
     * @param fromTurnMsg
     */
    fun placeTile(fromHand: Boolean, posX: Int, posY: Int, rotationDegree: Int = 0, fromTurnMsg: Boolean = false) {
        // add new Turn
        val newTurn = rootService.currentGame!!.currentTurn.copy()
        rootService.currentGame!!.currentTurn.nextTurn = newTurn
        newTurn.previousTurn = rootService.currentGame!!.currentTurn
        val currentPlayer= rootService.currentGame!!.currentTurn.players[rootService.currentGame!!.currentTurn.currentPlayerIndex]

        rootService.currentGame!!.currentTurn = newTurn

        var tile: Tile?

        // add tile to gameField and send TurnMessage
        if (isPositionLegal(posX, posY, rootService.currentGame!!.currentTurn) ) {

            if (fromHand) {
                if (handTileLegal(posX, posY, rootService.currentGame!!.currentTurn)
                    || noPlaceMore(posX,posY,rootService.currentGame!!.currentTurn)
                    || rootService.currentGame!!.currentTurn.players[rootService.currentGame!!.currentTurn.currentPlayerIndex].isSmartAi != null) {

                    tile = currentPlayer .handTile

                    // rotate tile if needed
                    if (rotationDegree != 0) {
                        val tempId = tile!!.id
                        val tempOriginalPorts = tile.originalPorts

                        while (rotationDegree > tile!!.rotationDegree) {
                            val rotDeg = tile.rotationDegree
                            tile = rotate(tile)
                            tile.rotationDegree = (rotDeg + 1) % 4
                        }

                        tile.id = tempId
                        tile.originalPorts = tempOriginalPorts
                    }

                    tile!!.posX = posX
                    tile.posY = posY
                    // put tile onto Field
                    rootService.currentGame!!.currentTurn.gameField.field[posX][posY] = tile


                    // give player new tile from tileStack
                    if (rootService.currentGame!!.currentTurn.gameField.tileStack.tiles.isNotEmpty()) {
                        currentPlayer.handTile =
                            rootService.currentGame!!.currentTurn.gameField.tileStack.tiles.removeFirst()
                        if (rootService.currentGame!!.currentTurn.gameField.tileStack.tiles.isEmpty())
                            onAllRefreshables { refreshAfterDrawStackEmpty() }
                    }
                }
                    else {
                        onAllRefreshables { refreshAfterTryingPlaceTile()  }
                        return
                    }

            } else {
                // tile from tileStack
                if (stackTileLegal(posX, posY, rootService.currentGame!!.currentTurn)
                    || noPlaceMore(posX,posY,rootService.currentGame!!.currentTurn)
                    || rootService.currentGame!!.currentTurn.players[rootService.currentGame!!.currentTurn.currentPlayerIndex].isSmartAi != null) {

                    tile = rootService.currentGame!!.currentTurn.gameField.tileStack.tiles.removeFirst()
                    if (rootService.currentGame!!.currentTurn.gameField.tileStack.tiles.isEmpty())
                        onAllRefreshables { refreshAfterDrawStackEmpty() }

                    // rotate tile if needed
                    if (rotationDegree != 0) {
                        val tempId = tile.id
                        val tempOriginalPorts = tile.originalPorts

                        while (rotationDegree > tile!!.rotationDegree) {
                            val rotDeg = tile.rotationDegree
                            tile = rotate(tile)
                            tile.rotationDegree = (rotDeg + 1) % 4
                        }

                        tile.id = tempId
                        tile.originalPorts = tempOriginalPorts

                    }

                    tile.posX = posX
                    tile.posY = posY
                    // remove tile from tileStack and put it onto the field
                    rootService.currentGame!!.currentTurn.gameField.field[posX][posY] = tile

                    rootService.currentGame!!.currentTurn.gameField.tileStack.tiles.forEach { println(it.id) }
                    println("laying tile with pairs " + tile.ports + " with id " + tile.id)
                    if (!rootService.gameService.isLocalOnlyGame && !fromTurnMsg) {
                        // create placedTiles List
                        val placedTiles = mutableListOf<TileInfo>()
                        for (tile in rootService.currentGame!!.currentTurn.gameField.tiles) {
                            placedTiles.add(TileInfo(tile.posX, tile.posY, tile.id, tile.rotationDegree))
                        }
                        // create tileStack ID list
                        val supply = mutableListOf<Int>()
                        rootService.currentGame!!.currentTurn.gameField.tileStack.tiles.forEach { supply.add(it.id) }
                        // create PlayerScore list
                        val scores = mutableListOf<Int>()
                        rootService.currentGame!!.currentTurn.players.forEach { scores.add(it.score) }
                        rootService.networkService.sendTurnMessage(
                            TurnMessage(
                                posX, posY,
                                !fromHand,
                                tile.rotationDegree * 90,
                                GameStateVerificationInfo(placedTiles, supply, scores)
                            )
                        )
                    }
                }
                else {
                    onAllRefreshables { refreshAfterTryingPlaceTile() }
                    return
                }

            }
            rootService.currentGame!!.currentTurn.gameField.tiles.add(tile)
            buildPathsAnastasiia(rootService.currentGame!!.currentTurn)
        }

        if (rootService.gameService.nextPlayer())
            onAllRefreshables { this.refreshAfterTurn() }
    }

    /**
     * Play AI turn
     *
     * @param allowRotation
     */
    fun playAiTurn(allowRotation: Boolean) {
        val aiIndex = rootService.currentGame!!.currentTurn.currentPlayerIndex

        runBlocking {
            val move = try {
                withTimeout(8000L) {
                    MCTS(rootService, aiIndex).findNextMove(allowRotation)
                }
            } catch (err: OutOfMemoryError) {
                MCTS(rootService, aiIndex).findNextMoveSimplified(allowRotation)
            } catch (exc: TimeoutCancellationException) {
                MCTS(rootService, aiIndex).findNextMoveSimplified(allowRotation)
            }

            println(move.toString() + " " + rootService.currentGame!!.currentTurn.players[aiIndex].handTile)

            placeTile(!move.shouldDrawFromStack, move.posX, move.posY, move.rotationsNo)
        }
    }

    /**
     * Play random turn
     *
     * @param allowRotation
     */
    fun playRandomTurn(allowRotation: Boolean) {
        val aiIndex = rootService.currentGame!!.currentTurn.currentPlayerIndex
        val move = MCTS(rootService, aiIndex).findRandomMove(allowRotation)
        placeTile(!move.shouldDrawFromStack, move.posX, move.posY, move.rotationsNo)
    }

    /**
     * Rotate
     *
     * @param tile
     * @return
     */
    fun rotate(tile: Tile) : Tile{

        tile.rotationDegree = (tile.rotationDegree + 1) % 4

        when (tile) {
            Tile(mutableListOf(Pair(0,1),Pair(2,7),Pair(3,4),Pair(5,6)))
            -> return Tile(mutableListOf(Pair(0,7),Pair(1,4),Pair(2,3),Pair(5,6)))
            Tile(mutableListOf(Pair(0,7),Pair(1,4),Pair(2,3),Pair(5,6)))
            -> return Tile(mutableListOf(Pair(0,7),Pair(1,2),Pair(3,6),Pair(4,5)))
            Tile(mutableListOf(Pair(0,7),Pair(1,2),Pair(3,6),Pair(4,5)))
            -> return Tile(mutableListOf(Pair(0,5),Pair(1,2),Pair(3,4),Pair(6,7)))
            Tile(mutableListOf(Pair(0,5),Pair(1,2),Pair(3,4),Pair(6,7)))
            -> return Tile(mutableListOf(Pair(0,1),Pair(2,7),Pair(3,4),Pair(5,6)))
            Tile(mutableListOf(Pair(0,5),Pair(1,6),Pair(2,7),Pair(3,4)))
            -> return Tile(mutableListOf(Pair(0,3),Pair(1,4),Pair(2,7),Pair(5,6)))
            Tile(mutableListOf(Pair(0,3),Pair(1,4),Pair(2,7),Pair(5,6)))
            -> return Tile(mutableListOf(Pair(0,7),Pair(1,4),Pair(2,5),Pair(3,6)))
            Tile(mutableListOf(Pair(0,7),Pair(1,4),Pair(2,5),Pair(3,6)))
            -> return Tile(mutableListOf(Pair(0,5),Pair(1,2),Pair(3,6),Pair(4,7)))
            Tile(mutableListOf(Pair(0,5),Pair(1,2),Pair(3,6),Pair(4,7)))
            -> return Tile(mutableListOf(Pair(0,5),Pair(1,6),Pair(2,7),Pair(3,4)))
            Tile(mutableListOf(Pair(0,7),Pair(1,6),Pair(2,3),Pair(4,5)))
            -> return Tile(mutableListOf(Pair(0,3),Pair(1,2),Pair(4,5),Pair(6,7)))
            Tile(mutableListOf(Pair(0,3),Pair(1,2),Pair(4,5),Pair(6,7)))
            -> return Tile(mutableListOf(Pair(0,1),Pair(2,5),Pair(3,4),Pair(6,7)))
            Tile(mutableListOf(Pair(0,1),Pair(2,5),Pair(3,4),Pair(6,7)))
            -> return Tile(mutableListOf(Pair(0,1),Pair(2,3),Pair(4,7),Pair(5,6)))
            Tile(mutableListOf(Pair(0,1),Pair(2,3),Pair(4,7),Pair(5,6)))
            -> return Tile(mutableListOf(Pair(0,7),Pair(1,6),Pair(2,3),Pair(4,5)))
            Tile(mutableListOf(Pair(0,3),Pair(1,6),Pair(2,7),Pair(4,5)))
            -> return Tile(mutableListOf(Pair(0,3),Pair(1,4),Pair(2,5),Pair(6,7)))
            Tile(mutableListOf(Pair(0,3),Pair(1,4),Pair(2,5),Pair(6,7)))
            -> return Tile(mutableListOf(Pair(0,1),Pair(2,5),Pair(3,6),Pair(4,7)))
            Tile(mutableListOf(Pair(0,1),Pair(2,5),Pair(3,6),Pair(4,7)))
            -> return Tile(mutableListOf(Pair(0,5),Pair(1,6),Pair(2,3),Pair(4,7)))
            Tile(mutableListOf(Pair(0,5),Pair(1,6),Pair(2,3),Pair(4,7)))
            -> return Tile(mutableListOf(Pair(0,3),Pair(1,6),Pair(2,7),Pair(4,5)))
            Tile(mutableListOf(Pair(0,3),Pair(1,2),Pair(4,7),Pair(5,6)))
            -> return Tile(mutableListOf(Pair(0,7),Pair(1,6),Pair(2,5),Pair(3,4)))
            Tile(mutableListOf(Pair(0,7),Pair(1,6),Pair(2,5),Pair(3,4)))
            -> return Tile(mutableListOf(Pair(0,3),Pair(1,2),Pair(4,7),Pair(5,6)))
            Tile(mutableListOf(Pair(0,5),Pair(1,4),Pair(2,3),Pair(6,7)))
            -> return Tile(mutableListOf(Pair(0,1),Pair(2,7),Pair(3,6),Pair(4,5)))
            Tile(mutableListOf(Pair(0,1),Pair(2,7),Pair(3,6),Pair(4,5)))
            -> return Tile(mutableListOf(Pair(0,5),Pair(1,4),Pair(2,3),Pair(6,7)))
            else -> return tile
        }

    }

    companion object {
        /**
         * @author Ikhlawi
         * check if no place more for Tiles in the hand & stack to set it
         * in the last 4 until 8 places on the edge
         */
        fun noPlaceMore(posX: Int, posY: Int, turn: Turn):Boolean
        {
            val size = turn.gameField.tileStack.tiles.size
            if ( ( (posX in 1..8 && posY == 1)||
                        (posY in 1..8 && posX == 1)||
                        (posX in 1..8 && posY == 8)||
                        (posY in 1..8 && posX == 8) ) &&
                    size<3 && !handTileLegal(posX,posY,turn) && !stackTileLegal(posX,posY,turn)
            )
            {
                return true
            }
            return false
        }
        /**
         * @author Ikhlawi
         * Check if the position at (posX, posY) is legal to place a tile from the hand on.
         *
         * */
        fun handTileLegal(posX: Int,posY: Int, turn: Turn):Boolean
        {
            val handTile  = turn.players[turn.currentPlayerIndex].handTile!!
            val lastTile = turn.gameField.tileStack.tiles.size
            for (port in handTile.ports)
            {

                if ( (port.first == 0 && port.second == 1) && (posX in 1..8 && posY == 1) )
                {
                    return false
                }
                else if ( (port.first == 6 && port.second == 7) && (posY in 1..8 && posX == 1)  )
                {
                    return false
                }
                else if ( (port.first == 4 && port.second == 5) && (posX in 1..8 && posY == 8)  )
                {
                    return false
                }
                else if ( (port.first == 2 && port.second == 3) && (posY in 1..8 && posX == 8) )
                {
                    return false
                }
                else if ( ((port.first == 0 && port.second == 7)||(port.first == 1 && port.second == 6))
                    && (posY == 1 && posX == 1)  )
                {
                    return false
                }
                else if ( ((port.first == 5 && port.second == 6)||(port.first == 4 && port.second == 7))
                    && (posY == 8 && posX == 1) )
                {
                    return false
                }
                else if ( ((port.first == 3 && port.second == 4)||(port.first == 2 && port.second == 5))
                    && (posY == 8 && posX == 8)  )
                {
                    return false
                }
                else if (( (port.first == 0 && port.second == 3)||(port.first == 1 && port.second == 2))
                    && (posY == 1 && posX == 8)  )
                {
                    return false
                }
                else if(lastTile == 0)
                {
                    return true
                }

            }
            return true

        }
        /**
         * @author Ikhlawi
         * Check if the position at (posX, posY) is legal to place a tile from the stack on.
         *
         * */
        fun stackTileLegal(posX: Int, posY: Int, turn: Turn): Boolean
        {
            if (turn.gameField.tileStack.tiles.isEmpty()) return false
            val stackTile  = turn.gameField.tileStack.tiles[0].ports
            for (port in stackTile)
            {


                if ( (port.first == 0 && port.second == 1) && (posX in 1..8 && posY == 1) )
                {
                    return false
                }
                else if ( (port.first == 6 && port.second == 7) && (posY in 1..8 && posX == 1)  )
                {
                    return false
                }
                else if ( (port.first == 4 && port.second == 5) && (posX in 1..8 && posY == 8)  )
                {
                    return false
                }
                else if ( (port.first == 2 && port.second == 3) && (posY in 1..8 && posX == 8) )
                {
                    return false
                }
                else if ( ((port.first == 0 && port.second == 7)||(port.first == 1 && port.second == 6))
                    && (posY == 1 && posX == 1)  )
                {
                    return false
                }
                else if ( ((port.first == 5 && port.second == 6)||(port.first == 4 && port.second == 7))
                    && (posY == 8 && posX == 1) )
                {
                    return false
                }
                else if ( ((port.first == 3 && port.second == 4)||(port.first == 2 && port.second == 5))
                    && (posY == 8 && posX == 8)  )
                {
                    return false
                }
                else if (( (port.first == 0 && port.second == 3)||(port.first == 1 && port.second == 2))
                    && (posY == 1 && posX == 8)  )
                {
                    return false
                }
            }
            return true
        }

        /**
         * @author Ikhlawi
         * Check if the spot at (posX, posY) is free.
         *
         * @param posX The x-coordinate of the spot to check.
         * @param posY The y-coordinate of the spot to check.
         * @return True if the spot is free, false if illegal or already occupied.
         */
        private fun isSpotFree(posX: Int, posY: Int, turn: Turn) =
            !((posX in 0..9 && (posY == 0 || posY == 10)) || (posY in 0..9 && (posX == 0 || posX == 10)) ||
                    (posX in 4..5 && posY in 4..5) ||
                    turn.gameField.field[posX][posY] != null)

        /**
         * @author Ikhlawi
         * Check if the position at (posX, posY) is legal to place a tile on.
         *
         * @param posX The x-coordinate of the position to check.
         * @param posY The y-coordinate of the position to check.
         * @return True if the position is legal to place a tile on, false otherwise.
         */
        fun isPositionLegal(posX: Int, posY: Int, turn: Turn): Boolean {

            val isFree = isSpotFree(posX, posY, turn)
            val tileEdge = isConnectedToTile(turn.gameField.field, posX, posY)

            if (isFree) {
                //println("tile is free")

                if (tileEdge) {
                    //println("tile does not stand alone")
                    return true
                }
            }

            return false
        }

        /**
         * calls isGameOver() when
         * the field is filled with 60 tiles
         * there are no cards left
         */

        fun isGameOver(turn: Turn) : Boolean {
            var isFieldFull = true
            var vbreak = false
            for (i in 1 until turn.gameField.field.size-1) {
                for (j in 1 until turn.gameField.field[i].size-1) {
                    if (turn.gameField.field[i][j] == null) {

                        if ((i == 4 && j == 4) || (i == 4 && j == 5) || (i == 5 && j == 4) || (i == 5 && j == 5)) {
                            //println("Spot ($i, $j) is empty, but it is MiddleStation.")
                            continue
                        }
                        //println("Spot ($i, $j) is empty")
                        isFieldFull = false
                        vbreak = true
                        break
                    }
                }
                if (vbreak) {
                    break
                }
            }
            //println("isFieldFull = $isFieldFull")
            val noCardsLeft = false
            /*
            for (player in turn.players) {
                if (player.handTile != null) {
                    println("${player.name} has handTileID: ${player.handTile!!.id}")
                    noCardsLeft = false
                    break
                }
                println("${player.name} has no handTile.")
            }
            println("noCardsLeft = $noCardsLeft")
             */
            return isFieldFull || noCardsLeft
        }

        /**
         * buildPathsAnastasiia() calculates the legal Position for the current turn
         */

        fun buildPathsAnastasiia(turn: Turn) {
            for (player in turn.players) {
                for (path in player.paths) {
                    if (path.complete) continue

                    var checkAgain = true

                    while(checkAgain) {
                        checkAgain = false

                        // For a non-empty path, check if it is connected to a station or can be extended
                        if (path.tiles.isNotEmpty()) {

                            // Check if connected to a power station
                            if (isConnectedToPower(path.tiles.last().posX, path.tiles.last().posY, path.lastPort)) {
                                path.complete = true
                                player.score += 2 * path.tiles.count()
                                break
                            }
                            // Check if connected to a normal station
                            if (isConnectedToStation(path.tiles.last().posX, path.tiles.last().posY, path.lastPort)) {
                                path.complete = true
                                player.score += path.tiles.count()
                                break
                            }

                            var placedTile: Tile?
                            // Check on the right if lastPort goes to the right
                            if (path.lastPort == 2 || path.lastPort == 3) {
                                placedTile = tileToTheRight(path.tiles.last().posX, path.tiles.last().posY, turn.gameField)
                                if (placedTile != null) {
                                    path.tiles.add(placedTile)
                                    val inPort = if (path.lastPort == 2) 7 else 6
                                    path.lastPort = findOutPort(inPort, placedTile.ports)
                                    checkAgain = true
                                }
                            }
                            // Check at the top if lastPort goes to the top
                            else if (path.lastPort == 0 || path.lastPort == 1) {
                                placedTile = tileAtTheTop(path.tiles.last().posX, path.tiles.last().posY, turn.gameField)
                                if (placedTile != null) {
                                    path.tiles.add(placedTile)
                                    val inPort = if (path.lastPort == 0) 5 else 4
                                    path.lastPort = findOutPort(inPort, placedTile.ports)
                                    checkAgain = true
                                }
                            }
                            // Check on the left if lastPort goes to the left
                            else if (path.lastPort == 6 || path.lastPort == 7) {
                                placedTile = tileToTheLeft(path.tiles.last().posX, path.tiles.last().posY, turn.gameField)
                                if (placedTile != null) {
                                    path.tiles.add(placedTile)
                                    val inPort = if (path.lastPort == 6) 3 else 2
                                    path.lastPort = findOutPort(inPort, placedTile.ports)
                                    checkAgain = true
                                }
                            }
                            // Check at the bottom if lastPort goes to the bottom
                            else if (path.lastPort == 4 || path.lastPort == 5) {
                                placedTile = tileAtTheBottom(path.tiles.last().posX, path.tiles.last().posY, turn.gameField)
                                if (placedTile != null) {
                                    path.tiles.add(placedTile)
                                    val inPort = if (path.lastPort == 4) 1 else 0
                                    path.lastPort = findOutPort(inPort, placedTile.ports)
                                    checkAgain = true
                                }
                            }
                        }
                        // For an empty path, check if a new tile was placed at its starting position
                        else {
                            // Get the coordinates of the starting station
                            var placedTile: Tile?
                            val coordinate = stationNoToCoordinate(path.startPos)
                            // Check if a new tile was placed at the station
                            placedTile = when (path.startPos) {
                                in 1..8 -> tileAtTheBottom(coordinate.first, coordinate.second, turn.gameField)
                                in 9..16 -> tileToTheLeft(coordinate.first, coordinate.second, turn.gameField)
                                in 17..24 -> tileAtTheTop(coordinate.first, coordinate.second, turn.gameField)
                                else -> tileToTheRight(coordinate.first, coordinate.second, turn.gameField)
                            }

                            // If not, then not.
                            if (placedTile == null) break
                            // Else add tile to the path and figure out its out-port
                            path.tiles.add(placedTile)
                            val inPort = inPortFromStartPos(path.startPos)
                            path.lastPort = findOutPort(inPort, placedTile.ports)
                            checkAgain = true
                        }
                    }
                }
            }
        }

        private fun findOutPort(inPort: Int, portList: MutableList<Pair<Int, Int>>): Int {
            for (pair in portList) {
                if (pair.first == inPort) return pair.second
                if (pair.second == inPort) return pair.first
            }
            throw Exception("In-port number " + inPort + "not found in port list.")
        }
        private fun stationNoToCoordinate(stationNo: Int): Pair<Int, Int> {
            if (stationNo < 1 || stationNo > 32)
                throw IllegalStateException("Tf you doing?")

            if (stationNo <= 8)
                return Pair(stationNo, 0)
            if (stationNo <= 16)
                return Pair(9, stationNo - 8)
            return if (stationNo <= 24)
                Pair(25 - stationNo, 9)
            else
                Pair(0, 33 - stationNo)
        }
        private fun tileToTheRight(x: Int, y: Int, field: GameField): Tile? {
            if (x + 1 > 8)
                throw IllegalStateException("Illegal coordinate x.")
            return field.field[x + 1][y]
        }
        private fun tileAtTheTop(x: Int, y: Int, field: GameField): Tile? {
            if (y - 1 < 1)
                throw IllegalStateException("Illegal coordinate y.")
            return field.field[x][y - 1]
        }
        private fun tileToTheLeft(x: Int, y: Int, field: GameField): Tile? {
            if (x - 1 < 1)
                throw IllegalStateException("Illegal coordinate x.")
            return field.field[x - 1][y]
        }
        private fun tileAtTheBottom(x: Int, y: Int, field: GameField): Tile? {
            if (y + 1 > 8)
                throw IllegalStateException("Illegal coordinate y.")
            return field.field[x][y + 1]
        }
        private fun isConnectedToStation(x: Int, y: Int, outPort: Int): Boolean {
            if (y == 1 && outPort == 1) return true
            if (y == 8 && outPort == 5) return true
            if (x == 1 && outPort == 7) return true
            if (x == 8 && outPort == 3) return true
            return false
        }

        /**
         * isConnectedToPower() returns true when
         * the tile port is connected to the Power station
         */
        fun isConnectedToPower(x: Int, y: Int, outPort: Int): Boolean {
            if (y == 6 && (x == 4 || x == 5) && (outPort == 0 || outPort == 1)) return true
            if (y == 3 && (x == 4 || x == 5) && (outPort == 4 || outPort == 5)) return true
            if (x == 3 && (y == 4 || y == 5) && (outPort == 2 || outPort == 3)) return true
            if (x == 6 && (y == 4 || y == 5) && (outPort == 6 || outPort == 7)) return true
            return false
        }
        private fun inPortFromStartPos(startPos: Int): Int {
            if (startPos in 1..8) return 0
            if (startPos in 9..16) return 2
            if (startPos in 17..24) return 4
            return 6
        }

        /**
         * @author Ikhlawi
         * Check if there is an adjacent tile at the spot (posX, posY).
         *
         * @param posX The x-coordinate of the spot to check.
         * @param posY The y-coordinate of the spot to check.
         * @return True if there is an adjacent tile at the spot, false otherwise.
         */
        private fun isConnectedToTile(currentField: Array<Array<Tile?>>, posX: Int, posY: Int): Boolean {

            if (posX < 1 || posX > 8 || posY < 1 || posY > 8)
                throw Exception("Tile coordinates must lie between 1 and 8.")

            if (posY == 1 || posY == 8 || posX == 1 || posX == 8 ||
                posX in 4..5 && posY == 3 ||
                posX in 4..5 && posY == 6 ||
                posY in 4..5 && posX == 3 ||
                posY in 4..5 && posX == 6) return true

            return (currentField[posX][posY + 1] != null ||
                    currentField[posX][posY - 1] != null ||
                    currentField[posX + 1][posY] != null ||
                    currentField[posX - 1][posY] != null)
        }

//        /**
//         * @author Ikhlawi
//         */
//        private fun checkRight(placedTile: Tile, lastTile: Tile): Boolean {
//            return (placedTile.posY == lastTile.posY + 1) && (placedTile.posX == lastTile.posX)
//        }
//        /**
//         * @author Ikhlawi
//         */
//        private fun checkLeft(placedTile: Tile, lastTile: Tile): Boolean {
//            return (placedTile.posY == lastTile.posY - 1) && (placedTile.posX == lastTile.posX)
//        }
//        /**
//         * @author Ikhlawi
//         */
//        private fun checkTop(placedTile: Tile, lastTile: Tile): Boolean {
//            return (placedTile.posY == lastTile.posY) && (placedTile.posX == lastTile.posX + 1)
//        }
//        /**
//         * @author Ikhlawi
//         */
//        private fun checkBottom(placedTile: Tile, lastTile: Tile): Boolean {
//            return (placedTile.posY == lastTile.posY) && (placedTile.posX == lastTile.posX - 1)
//        }

//        /**
//         * @author Ikhlawi
//         *
//         *
//         * buildPaths is a function that builds paths in a game and increases the player's score.
//         * @param player: Player - An object representing the current player.
//         * @param placedTile: Tile - An object representing the tile that has been placed on the board.
//         * The function starts by setting the variable "checkAgain" to false and getting a reference to the current game.
//         * Then, it iterates through the player's existing paths. For each path, it checks if the path is complete. If not,
//         * the function sets the variable "checkAgain" to true and starts a loop that runs until "checkAgain" is set to false.
//         * Within the loop, the function checks various conditions to determine if the placed tile can be added to the path by
//         * comparing the ports of the last tile in the path to the ports of the placed tile. If the tile can be added, it is
//         * added and the "lastPort" variable is updated. If the path is completed, either by the last tile matching
//         * the start tile or by the last tile being in a specific position --power station--
//         * (x=4, y=4 or x=4, y=5 or x=5, y=4 or x=5, y=5)
//         * the player's score is incremented by the number of
//         * tiles in the path, and the "complete" variable of the path is set to true.
//         */
//        fun buildPaths(player: Player, placedTile: Tile) {
//            var checkAgain = false
//            //val currentGame = rootService.currentGame
//            //checkNotNull(currentGame)
//            for (path in player.paths) {
//
//                if (path.complete) continue
//                checkAgain = true
//                while (checkAgain) {
//                    var lastPort = path.lastPort
//                    var inPort = 0
//
//                    checkAgain = false
//                    if (path.tiles.isNotEmpty()) {
//
//                        val lastTile = path.tiles.last()
//                        if ((lastPort == 2 || lastPort == 3) && checkRight(placedTile, lastTile)) {
//                            path.tiles.add(placedTile)
//                            inPort = if (lastPort == 2) 7
//                            else 6
//                            for (i in placedTile.ports) {
//                                if (i.first == inPort) {
//                                    path.lastPort = i.second
//                                    break
//                                }
//                            }
//                            checkAgain = true
//                        }
//                        if ((lastPort == 0 || lastPort == 1) && checkTop(placedTile, lastTile)) {
//                            path.tiles.add(placedTile)
//                            inPort = if (lastPort == 0) 5
//                            else 4
//                            for (i in placedTile.ports) {
//                                if (i.first == inPort) {
//                                    path.lastPort = i.second
//                                    break
//                                }
//                            }
//                            checkAgain = true
//                        }
//                        if ((lastPort == 6 || lastPort == 7) && checkLeft(placedTile, lastTile)) {
//                            path.tiles.add(placedTile)
//                            inPort = if (lastPort == 6) 3
//                            else 2
//                            for (i in placedTile.ports) {
//                                if (i.first == inPort) {
//                                    path.lastPort = i.second
//                                    break
//                                }
//                            }
//                            checkAgain = true
//                        }
//                        if ((lastPort == 4 || lastPort == 5) && checkBottom(placedTile, lastTile)) {
//                            path.tiles.add(placedTile)
//                            inPort = if (lastPort == 4) 1
//                            else 0
//                            for (i in placedTile.ports) {
//                                if (i.first == inPort) {
//                                    path.lastPort = i.second
//                                    break
//                                }
//                            }
//                            checkAgain = true
//                        }
//
//                    }
//
//                    if (placedTile.posX == 1 ||
//                        placedTile.posX == 8 ||
//                        placedTile.posY == 1 ||
//                        placedTile.posY == 8
//                    ) {
//                        path.tiles.add(placedTile)
//                        inPort = when (path.startPos) {
//
//                            1, 2, 3, 4, 5, 6, 7, 8 -> 4
//                            9, 10, 11, 12, 13, 14, 15, 16 -> 2
//                            17, 18, 19, 20, 21, 22, 23, 24 -> 0
//                            25, 26, 27, 28, 29, 30, 31, 32 -> 6
//                            else -> throw IllegalStateException("Invalid start position")
//                        }
//                        lastPort = placedTile.ports[inPort].second
//                        checkAgain = true
//                    }
//                }
//                if (path.tiles.last().equals(path.startPos)) { //mit x und y
//
//                    player.score = path.tiles.count()
//                    path.complete = true
//                }
//                /*if ((path.tiles.last().posX == 4 && path.tiles.last().posY == 4)
//                    || (path.tiles.last().posX == 4 && path.tiles.last().posY == 5)
//                    || (path.tiles.last().posX == 5 && path.tiles.last().posY == 4)
//                    || (path.tiles.last().posX == 5 && path.tiles.last().posY == 5)
//                ) {
//                    player.score += path.tiles.count()
//                }*/
//            }
//        }
//
//        /**
//         * calculates the score when
//         * tile successfully connects and builds path
//         */
//
//        fun buildPathWithPowerStation(player: Player, placedTile: Tile)
//        {
//            //val currentGame = rootService.currentGame
//            //checkNotNull(currentGame)
//            for (path in player.paths) {
//                if ((path.tiles.last().posX == 4 && path.tiles.last().posY == 4)
//                    || (path.tiles.last().posX == 4 && path.tiles.last().posY == 5)
//                    || (path.tiles.last().posX == 5 && path.tiles.last().posY == 4)
//                    || (path.tiles.last().posX == 5 && path.tiles.last().posY == 5)
//                ) {
//                    player.score += path.tiles.count()
//                }
//            }
//        }

        /**
         * @author Jonah
         * Rotate a tile 90 degrees clockwise Sense
         *
         * @param tile The tile which should rotate
         */
        fun rotate(tile: Tile) : Tile{

            tile.rotationDegree = (tile.rotationDegree + 1) % 4

            when (tile) {
                Tile(mutableListOf(Pair(0,1),Pair(2,7),Pair(3,4),Pair(5,6)))
                -> return Tile(mutableListOf(Pair(0,7),Pair(1,4),Pair(2,3),Pair(5,6)))
                Tile(mutableListOf(Pair(0,7),Pair(1,4),Pair(2,3),Pair(5,6)))
                -> return Tile(mutableListOf(Pair(0,7),Pair(1,2),Pair(3,6),Pair(4,5)))
                Tile(mutableListOf(Pair(0,7),Pair(1,2),Pair(3,6),Pair(4,5)))
                -> return Tile(mutableListOf(Pair(0,5),Pair(1,2),Pair(3,4),Pair(6,7)))
                Tile(mutableListOf(Pair(0,5),Pair(1,2),Pair(3,4),Pair(6,7)))
                -> return Tile(mutableListOf(Pair(0,1),Pair(2,7),Pair(3,4),Pair(5,6)))
                Tile(mutableListOf(Pair(0,5),Pair(1,6),Pair(2,7),Pair(3,4)))
                -> return Tile(mutableListOf(Pair(0,3),Pair(1,4),Pair(2,7),Pair(5,6)))
                Tile(mutableListOf(Pair(0,3),Pair(1,4),Pair(2,7),Pair(5,6)))
                -> return Tile(mutableListOf(Pair(0,7),Pair(1,4),Pair(2,5),Pair(3,6)))
                Tile(mutableListOf(Pair(0,7),Pair(1,4),Pair(2,5),Pair(3,6)))
                -> return Tile(mutableListOf(Pair(0,5),Pair(1,2),Pair(3,6),Pair(4,7)))
                Tile(mutableListOf(Pair(0,5),Pair(1,2),Pair(3,6),Pair(4,7)))
                -> return Tile(mutableListOf(Pair(0,5),Pair(1,6),Pair(2,7),Pair(3,4)))
                Tile(mutableListOf(Pair(0,7),Pair(1,6),Pair(2,3),Pair(4,5)))
                -> return Tile(mutableListOf(Pair(0,3),Pair(1,2),Pair(4,5),Pair(6,7)))
                Tile(mutableListOf(Pair(0,3),Pair(1,2),Pair(4,5),Pair(6,7)))
                -> return Tile(mutableListOf(Pair(0,1),Pair(2,5),Pair(3,4),Pair(6,7)))
                Tile(mutableListOf(Pair(0,1),Pair(2,5),Pair(3,4),Pair(6,7)))
                -> return Tile(mutableListOf(Pair(0,1),Pair(2,3),Pair(4,7),Pair(5,6)))
                Tile(mutableListOf(Pair(0,1),Pair(2,3),Pair(4,7),Pair(5,6)))
                -> return Tile(mutableListOf(Pair(0,7),Pair(1,6),Pair(2,3),Pair(4,5)))
                Tile(mutableListOf(Pair(0,3),Pair(1,6),Pair(2,7),Pair(4,5)))
                -> return Tile(mutableListOf(Pair(0,3),Pair(1,4),Pair(2,5),Pair(6,7)))
                Tile(mutableListOf(Pair(0,3),Pair(1,4),Pair(2,5),Pair(6,7)))
                -> return Tile(mutableListOf(Pair(0,1),Pair(2,5),Pair(3,6),Pair(4,7)))
                Tile(mutableListOf(Pair(0,1),Pair(2,5),Pair(3,6),Pair(4,7)))
                -> return Tile(mutableListOf(Pair(0,5),Pair(1,6),Pair(2,3),Pair(4,7)))
                Tile(mutableListOf(Pair(0,5),Pair(1,6),Pair(2,3),Pair(4,7)))
                -> return Tile(mutableListOf(Pair(0,3),Pair(1,6),Pair(2,7),Pair(4,5)))
                Tile(mutableListOf(Pair(0,3),Pair(1,2),Pair(4,7),Pair(5,6)))
                -> return Tile(mutableListOf(Pair(0,7),Pair(1,6),Pair(2,5),Pair(3,4)))
                Tile(mutableListOf(Pair(0,7),Pair(1,6),Pair(2,5),Pair(3,4)))
                -> return Tile(mutableListOf(Pair(0,3),Pair(1,2),Pair(4,7),Pair(5,6)))
                Tile(mutableListOf(Pair(0,5),Pair(1,4),Pair(2,3),Pair(6,7)))
                -> return Tile(mutableListOf(Pair(0,1),Pair(2,7),Pair(3,6),Pair(4,5)))
                Tile(mutableListOf(Pair(0,1),Pair(2,7),Pair(3,6),Pair(4,5)))
                -> return Tile(mutableListOf(Pair(0,5),Pair(1,4),Pair(2,3),Pair(6,7)))
                else -> return tile
            }
        }

    }
}