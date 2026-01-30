package entity

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * @author Ikhlawi
 * test for Tile
 */
internal class TileTest {
    /**
     * test the ports and id and positions of tile
     * test the copy method for this tile
     */
    @Test
    fun testTile() {
        val ports = mutableListOf(Pair(0, 1), Pair(1, 0))
        val tile = Tile(ports)
        assertEquals(ports, tile.ports)
        assertEquals(-1, tile.id)
        assertEquals(ports, tile.originalPorts)
        assertEquals(0, tile.rotationDegree)
        assertEquals(0, tile.posX)
        assertEquals(0, tile.posY)

        val copy = tile.copy()
        assertEquals(tile.ports, copy.ports)
        assertEquals(tile.id, copy.id)
        assertEquals(tile.originalPorts, copy.originalPorts)
        assertEquals(tile.rotationDegree, copy.rotationDegree)
        assertEquals(tile.posX, copy.posX)
        assertEquals(tile.posY, copy.posY)
    }}