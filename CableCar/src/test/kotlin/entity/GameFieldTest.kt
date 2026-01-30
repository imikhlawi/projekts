package entity

import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * Game field test
 *
 * @constructor Create empty Game field test
 */
class GameFieldTest {

    /**
     * Test copy
     *
     */
    @Test
    fun testCopy() {
        val stations = mutableListOf<Station>()
        val tiles = mutableListOf<Tile>()
        val tileStack = TileStack(tiles)

        val gameField = GameField(stations, tiles, tileStack)

        val gameFieldCopy = gameField.copy()

        // Test if the copy is not the same object as the original
        assertNotSame(gameField, gameFieldCopy)

        // Test if the properties of the original and the copy are equal
        assertEquals(gameField.stations, gameFieldCopy.stations)
        assertEquals(gameField.tiles, gameFieldCopy.tiles)
        assertEquals(gameField.tileStack, gameFieldCopy.tileStack)
    }
}

