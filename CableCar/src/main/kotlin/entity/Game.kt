package entity
/**
 * The Game class represents the current state of the game, including information about the current turn.
 *
 * @property currentTurn the current turn object, containing information about the player, their actions and score
 */
data class Game(var currentTurn: Turn)