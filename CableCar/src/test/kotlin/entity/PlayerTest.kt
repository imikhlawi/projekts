package entity

import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


/**
 * @author Ikhlawi
 * Methods from Player to test
 */
internal class PlayerTest {
    /**
     * test the player name and color
     */
    @Test
    fun testPlayer() {
        val player = Player( "John",false)
        player.color = Color.YELLOW


        assertEquals("John", player.name)
        assertEquals(Color.YELLOW, player.color)
    }

    /**
     * test a copy from the player
     */
    @Test
    fun testPlayerCopy() {
        val player = Player( "John",false)
        val playerCopy = player.copy()
        assertNotSame(player, playerCopy)
        assertEquals(player, playerCopy)
    }
}
