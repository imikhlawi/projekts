package entity

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * @author Ikhlawi
 * test for turn class
 */
internal class TurnTest {
    /**
     * test the turn of players with their colors
     */
    @Test
    fun testTurn() {
        val players = mutableListOf<Player>(Player( "John",false),Player( "MArk",false))

        players[0].color = Color.YELLOW
        players[1].color = Color.BLACK
        val turn = Turn(GameField(mutableListOf(), mutableListOf(), tileStack = TileStack(mutableListOf())), players)
        assertEquals(Color.YELLOW, turn.players[0].color)
        assertTrue(players[0].name == "John")
    }
}