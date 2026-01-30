package entity

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * @author Ikhlawi
 * test for tileStack
 */
internal class TileStackTest{
    /**
     * test a pair of tile from a stack
     */
    @Test
    fun testTileCreation() {
        val tile = Tile(mutableListOf(Pair(1, 2)))
        assertNotNull(tile)
        assertEquals(mutableListOf(Pair(1, 2)), tile.ports)
        assertEquals(-1, tile.id)
        assertEquals(mutableListOf(Pair(1, 2)), tile.originalPorts)
        assertEquals(0, tile.rotationDegree)
        assertEquals(0, tile.posX)
        assertEquals(0, tile.posY)
    }
}