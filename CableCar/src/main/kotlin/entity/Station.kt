package entity

/**
 * The Station class represents a station on the game board, including information about the departure and arrival players.
 *
 * @property departure a Player object representing the player who owns the station as departure point
 * @property arrival a Player object representing the player who owns the station as arrival point
 */
data class Station(val departure: Player) {
    val arrival: Player? = null

    /**
     * copies the object
     */

    fun copy(): Station {
        return Station(departure)
    }
}
