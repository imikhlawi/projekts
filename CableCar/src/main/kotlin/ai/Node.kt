package ai
import entity.Turn
import service.PlayerActionService

/**
 * Node class that represents a state in the game .
 * @property rs RootService object that holds the game state
 * @property parent Parent node in the game tree
 * @property move Move made to reach this node's state
 * @property children List of child nodes in the game tree
 * @property state Turn object representing this node's state
 * @property score Score assigned to this node
 * @property winCount Number of times this node led to a win for the player
 * @property visitCount Number of times this node has been visited in tree search
 */
data class Node(val rs: service.RootService, val parent: Node?, val move: Move) {
    val children: MutableList<Node> = mutableListOf()
    val state: Turn =
        if (parent != null) AiActionService.doMove(parent.state, move)
        else Turn (rs.currentGame!!.currentTurn.gameField.copy(),
            rs.currentGame!!.currentTurn.players.toMutableList())

    var score = 0.0
    var winCount = 0.0
    var visitCount = 0.0

    init {
        if (parent == null) state.currentPlayerIndex = rs.currentGame!!.currentTurn.currentPlayerIndex
    }

    /**
     * Returns a list of possible moves in this node's state.
     *
     * @param allowRotation Whether to allow rotation of tiles in the moves
     *
     * @return List of Move objects representing the possible moves in this node's state
     */
    fun getPossibleMoves(allowRotation: Boolean): MutableList<Move> {
        val moves: MutableList<Move> = mutableListOf()
        if (state.players[state.currentPlayerIndex].handTile == null) return moves

        val rotations = if (allowRotation) 3 else 0

        for (x in 1 until state.gameField.field.size - 1) {
            for (y in 1 until state.gameField.field[x].size - 1) {
                if (!PlayerActionService.isPositionLegal(x, y, state)) continue
                for (i in 0..rotations) {
                    if (PlayerActionService.handTileLegal(x, y, state) || PlayerActionService.noPlaceMore(x, y, state))
                        moves.add(Move(false, i, x, y))

                    if (state.gameField.tileStack.tiles.isNotEmpty() &&
                        (PlayerActionService.stackTileLegal(x, y, state) || PlayerActionService.noPlaceMore(x, y, state)))
                        moves.add(Move(true, i, x, y))
                }
            }
        }

        return moves
    }
    /**
     * Sets the score for this node based on its state.
     */
    fun setScore () {
        score = 0.0
        val playerIndex =
            if(state.currentPlayerIndex - 1 >= 0) state.currentPlayerIndex - 1
            else state.players.size - 1
        val prev = parent!!.state

        var actComparator = 0
        var prevComparator = 0

        // -5 if another path was completed too early
        for (path in state.players[playerIndex].paths) {
            if (path.complete && path.tiles.size < 5) actComparator += 5
        }
        for (path in prev.players[playerIndex].paths) {
            if (path.complete && path.tiles.size < 5) prevComparator += 5
        }
        if (actComparator > prevComparator) score -= (actComparator - prevComparator)
        actComparator = 0
        prevComparator = 0

        // +3 if another rival's path was completed
        for (i in 0 until state.players.size) {
            if (i == playerIndex) continue
            for (path in state.players[i].paths) {
                if (path.complete && path.tiles.size < 5) actComparator += 3
            }
        }
        for (i in 0 until prev.players.size) {
            if (i == playerIndex) continue
            for (path in prev.players[i].paths) {
                if (path.complete && path.tiles.size < 5) prevComparator += 3
            }
        }
        if (actComparator > prevComparator) score += (actComparator - prevComparator)
        actComparator = 0
        prevComparator = 0

        // +4 if another train connected to power station
        for (path in state.players[playerIndex].paths) {
            if (path.tiles.isNotEmpty() &&
                PlayerActionService.isConnectedToPower(path.tiles.last().posX, path.tiles.last().posY, path.lastPort))
                actComparator += 4
        }
        for (path in prev.players[playerIndex].paths) {
            if (path.tiles.isNotEmpty() &&
                PlayerActionService.isConnectedToPower(path.tiles.last().posX, path.tiles.last().posY, path.lastPort))
                prevComparator += 4
        }
        if (actComparator > prevComparator) score += (actComparator - prevComparator)
        actComparator = 0
        prevComparator = 0

        // +1 if another path extended
        for (path in state.players[playerIndex].paths) {
            actComparator += path.tiles.size
        }
        for (path in prev.players[playerIndex].paths) {
            prevComparator += path.tiles.size
        }
        if (actComparator > prevComparator) score += (actComparator - prevComparator)
        actComparator = 0
        prevComparator = 0

        // -3 if a rival's path extended
        for (i in 0 until state.players.size) {
            if (i == playerIndex) continue
            for (path in state.players[i].paths) {
                actComparator += 3 * path.tiles.size
            }
            for (path in prev.players[i].paths) {
                prevComparator += 3 * path.tiles.size
            }
        }
        if (actComparator > prevComparator) score -= (actComparator - prevComparator)
    }
}
