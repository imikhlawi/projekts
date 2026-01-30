package ai
import entity.Path
import entity.Tile
import entity.Turn
import service.PlayerActionService
/**
 * This class implements AI logic for game moves simulation.
 * It is used to analyze potential game moves and to determine the best move based on the analysis.
 */
class AiActionService {
    companion object {
        /**
         * Creates a copy of the current turn object.
         * @param turn - the current turn object.
         * @return a new turn object, which is a copy of the current one.
         */
        private fun createNextTurn(turn: Turn): Turn {
            val newTurn = turn.copy()

            // copy all players and their paths separately!
            for (i in 0 until turn.players.size) {
                newTurn.players[i] = turn.players[i].copy()
                newTurn.players[i].paths = mutableListOf()

                for (path in turn.players[i].paths) {
                    val newPath = Path(path.startPos)
                    newPath.tiles = path.tiles.toMutableList()
                    newPath.complete = path.complete
                    newPath.lastPort = path.lastPort

                    newTurn.players[i].paths.add(newPath)
                }
            }
            newTurn.currentPlayerIndex = (newTurn.currentPlayerIndex + 1) % newTurn.players.size
            return newTurn
        }

        /**
         * Simulates a game move from for a given player in the AI analysis.
         * @param turn - the current turn object.
         * @param move - the move object, representing the game move.
         * @return a new turn object, representing the game state after the move.
         */
        fun doMove(turn: Turn, move: Move) : Turn {
            // add new Turn
            val newTurn = createNextTurn(turn)
            val currentPlayer=newTurn.players[newTurn.currentPlayerIndex]
            var tile: Tile?

            if (!move.shouldDrawFromStack) {
                // tile from hand
                tile = newTurn.players[newTurn.currentPlayerIndex].handTile
                if (tile != null) {
                    // rotate tile if needed
                    if (move.rotationsNo != 0) {
                        val tempId = tile.id
                        val tempOriginalPorts = tile.originalPorts

                        while (move.rotationsNo > tile!!.rotationDegree) {
                            val rotDeg = tile.rotationDegree
                            tile = PlayerActionService.rotate(tile)
                            tile.rotationDegree = (rotDeg + 1) % 4
                        }
                        tile.id = tempId
                        tile.originalPorts = tempOriginalPorts
                    }
                    tile.posX = move.posX
                    tile.posY = move.posY

                    // put tile onto Field
                    newTurn.gameField.field[move.posX][move.posY] = tile

                    // give player new tile from tileStack
                    if (newTurn.gameField.tileStack.tiles.isNotEmpty()){
                        currentPlayer.handTile = newTurn.gameField.tileStack.tiles.removeFirst()
                    }
                    else newTurn.players[newTurn.currentPlayerIndex].handTile = null
                }
                else throw Exception("Hand tile is null!")
            }
            else {
                // tile from tileStack
                if (newTurn.gameField.tileStack.tiles.isNotEmpty()){
                    tile = newTurn.gameField.tileStack.tiles.removeFirst()

                    // rotate tile if needed
                    if (move.rotationsNo != 0) {
                        val tempId = tile.id
                        val tempOriginalPorts = tile.originalPorts

                        while (move.rotationsNo > tile!!.rotationDegree) {
                            val rotDeg = tile.rotationDegree
                            tile = PlayerActionService.rotate(tile)
                            tile.rotationDegree = (rotDeg + 1) % 4
                        }
                        tile.id = tempId
                        tile.originalPorts = tempOriginalPorts
                    }
                    tile.posX = move.posX
                    tile.posY = move.posY

                    // put tile onto the field
                    newTurn.gameField.field[move.posX][move.posY] = tile
                }
                else {
                    val newMove = Move(false, move.rotationsNo, move.posX, move.posY)
                    return doMove(newTurn, newMove)
                }
            }
            PlayerActionService.buildPathsAnastasiia(newTurn)
            return newTurn
        }
    }
}