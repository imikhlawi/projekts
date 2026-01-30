package ai

import service.PlayerActionService
/**
 * The MCTS class implements a Monte Carlo Tree Search algorithm to find the best move
 * for a player in a given game state.
 *
 * @property rs the RootService instance that holds the game state information
 * @property aiIndex the index of the player that the AI is playing as
 */
class MCTS (private val rs: service.RootService, private val aiIndex: Int) {
    /**
     * Finds the next best move for the AI player by simplifying the MCTS algorithm.
     *
     * @param allowRotation Boolean value that indicates whether rotations are allowed in the game or not.
     * @return A [Move] object representing the best move for the AI player.
     */
    fun findNextMoveSimplified(allowRotation: Boolean): Move {
        val defaultMove = Move(false, -1, -1, -1)
        val node = Node(rs, null, defaultMove)

        node.getPossibleMoves(allowRotation).forEach {
            val child = Node(rs, node, it)
            child.setScore()
            node.children.add(child)
        }
        node.children.shuffle()

        println("Decision made.")
        return node.children.maxByOrNull { it.score }!!.move
    }
    /**
     * Finds the next best move for the AI player by running the full MCTS algorithm.
     *
     * @param allowRotation Boolean value that indicates whether rotations are allowed in the game or not.
     * @return A [Move] object representing the best move for the AI player.
     */
    fun findNextMove(allowRotation: Boolean) : Move {
        val defaultMove = Move(false, -1, -1, -1)
        val root = Node(rs, null, defaultMove)

        var shouldStop = false
        while (true) {
            println("Still Thinking")
            val node = selectPromisingNode(root)
            if (PlayerActionService.isGameOver(node.state) || shouldStop) {
                backpropagation(node, true)
                println("Decision Made")
                return node.move
            }
            shouldStop = expandNode(node, allowRotation)
            val nodeToExplore = selectPromisingNode(node)
            val aiWon = simulateRandomPlayout(nodeToExplore, allowRotation)
            backpropagation(nodeToExplore, aiWon)
        }
    }
    /**
    * for the stupid AI
    * findRandomMove - a function that returns a random move from a set of possible moves
    * @param allowRotation: a boolean indicating if rotation of the pieces is allowed or not
    * @return Move: a randomly selected move
    */
    fun findRandomMove(allowRotation: Boolean) : Move {
        val defaultMove = Move(false, -1, -1, -1)
        val node = Node(rs, null, defaultMove)

        node.getPossibleMoves(allowRotation).forEach {
            val child = Node(rs, node, it)
            child.setScore()
            node.children.add(child)
        }

        return node.children.random().move
    }
    /**
     * selectPromisingNode - a function that selects the most promising node to be expanded
     * @param node: the current node
     * @return Node: the most promising node
     */
    private fun selectPromisingNode(node: Node): Node {
        var current = node
        while (current.children.isNotEmpty()) {
            current = current.children.maxByOrNull {
                if (it.visitCount != 0.0) it.score + it.winCount / it.visitCount
                else it.score
            }!!
        }
        return current
    }
    /**
     * expandNode - a function that expands the given node by adding its possible moves as children
     * @param node: the node to be expanded
     * @param allowRotation: a boolean indicating if rotation of the pieces is allowed or not
     * @return Boolean: returns true if there are no possible moves, false otherwise
     */

    private fun expandNode(node: Node, allowRotation: Boolean): Boolean {
        node.getPossibleMoves(allowRotation).forEach {
            val child = Node(rs, node, it)
            child.setScore()
            node.children.add(child)
        }
        node.children.shuffle()
        return node.children.isEmpty()
    }
    /**
     * simulateRandomPlayout - a function that simulates a random playout from the given node
     * @param node: the starting node for the simulation
     * @param allowRotation: a boolean indicating if rotation of the pieces is allowed or not
     * @return Boolean: returns true if the AI won the simulation, false otherwise
     */
    private fun simulateRandomPlayout(node: Node, allowRotation: Boolean): Boolean {
        var tempNode = node.copy()

        var shouldStop = false
        while (!PlayerActionService.isGameOver(tempNode.state) && !shouldStop) {
            //playerIndex = (playerIndex + 1) % node.state.players.size
            shouldStop = expandNode(tempNode, allowRotation)
            tempNode = selectPromisingNode(tempNode)
        }

        return tempNode.state.players[aiIndex].score >= tempNode.state.players.maxOf { it.score }
    }
    /**
     * Backpropagates the result of the simulation up the tree to update the statistics
     * of each node.
     *
     * @param last Node at the end of the simulation
     * @param aiWon Boolean indicating if the AI won the simulation
     */
    private fun backpropagation(last: Node, aiWon: Boolean) {
        // Start from the last node and move up the tree until the root node is reached
        var node: Node? = last
        while (node != null) {
            node.visitCount += 1
            if (aiWon) node.winCount += 1
            node = node.parent
        }
    }

}
