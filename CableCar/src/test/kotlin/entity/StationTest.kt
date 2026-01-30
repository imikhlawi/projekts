package entity

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * @author Ikhlawi
 * Tests for Station
 */
internal class StationTest {
    @Test
            /**
             * test the station the arrival and departure
             */
    fun testStation() {
        val player = Player( "John",false)
        val station = Station(player)
        assertEquals(player, station.departure)
        assertEquals(null, station.arrival)

        val copy = station.copy()
        assertEquals(station.departure, copy.departure)
        assertEquals(station.arrival, copy.arrival)
    }
}