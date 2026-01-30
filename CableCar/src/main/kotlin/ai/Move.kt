package ai

/**
 * The `Move` class represents a move made by an AI in a game.
 *
 * @property shouldDrawFromStack A flag indicating whether the AI should draw a piece from the stack or not.
 * @property rotationsNo The number of rotations that should be applied to the piece before placement.
 * @property posX The x-coordinate of the target position for the piece.
 * @property posY The y-coordinate of the target position for the piece.
 */
data class Move(val shouldDrawFromStack: Boolean, val rotationsNo: Int, val posX: Int, val posY: Int)