package service

import entity.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

/**
 * @author Aziz Feriani
 * Methods from PlayerService to test
 */

class PlayerActionServiceTest {
    private val stations = mutableListOf<Station>()
    private val tiles = mutableListOf<Tile>()
    private val tileStack = TileStack(tiles)
    private var gameField = GameField(stations, tiles, tileStack)
    private lateinit var rootService: RootService
    private lateinit var playerActionService: PlayerActionService
    val ports = mutableListOf(Pair(0, 1), Pair(1, 0))
    private lateinit var tile: Tile
    private lateinit var turn: Turn

    /**
     setting up the test
     */
    @BeforeEach
    fun setUp() {
        val players = mutableListOf(Player("sam",false), Player("Jen", false), Player("Jen", false) , Player("Jen", false) )
        rootService = RootService()
        playerActionService = PlayerActionService(rootService)
        gameField = GameField(stations, tiles, tileStack)
         tile = Tile(ports)
         turn = Turn(GameField(mutableListOf(), mutableListOf(), tileStack = TileStack(mutableListOf())), players)
    }
    /**
    testing rotation 1
     */
    @Test
    fun testRotate() {
        val tile1 = Tile(mutableListOf(Pair(0, 1), Pair(2, 7), Pair(3, 4), Pair(5, 6)))
        val expectedTile1 = Tile(mutableListOf(Pair(0, 7), Pair(1, 4), Pair(2, 3), Pair(5, 6)))
        assertEquals(expectedTile1, playerActionService.rotate(tile1))
        val tile2 = Tile(mutableListOf(Pair(0,7),Pair(1,4),Pair(2,3),Pair(5,6)))
        val expectedTile2 = Tile(mutableListOf(Pair(0,7),Pair(1,2),Pair(3,6),Pair(4,5)))
        assertEquals(expectedTile2, playerActionService.rotate(tile2))

    }
    /**
    testing rotation 2
     */
    @Test
    fun testRotate2() {
        val tile3 = Tile(mutableListOf(Pair(0,7),Pair(1,2),Pair(3,6),Pair(4,5)))
        val expectedTile3 = Tile(mutableListOf(Pair(0,5),Pair(1,2),Pair(3,4),Pair(6,7)))
        assertEquals(expectedTile3, playerActionService.rotate(tile3))

        val tile4 = Tile(mutableListOf(Pair(0,5),Pair(1,2),Pair(3,4),Pair(6,7)))
        val expectedTile4 = Tile(mutableListOf(Pair(0,1),Pair(2,7),Pair(3,4),Pair(5,6)))
        assertEquals(expectedTile4, playerActionService.rotate(tile4))
    }
    /**
    testing rotation 3
     */
    @Test
    fun testRotate3()
    {val tile5 = Tile(mutableListOf(Pair(0,5),Pair(1,6),Pair(2,7),Pair(3,4)))
        val expectedTile5 = Tile(mutableListOf(Pair(0,3),Pair(1,4),Pair(2,7),Pair(5,6)))
        assertEquals(expectedTile5, playerActionService.rotate(tile5))

        val tile6 = Tile(mutableListOf(Pair(0,3),Pair(1,4),Pair(2,7),Pair(5,6)))
        val expectedTile6 = Tile(mutableListOf(Pair(0,7),Pair(1,4),Pair(2,5),Pair(3,6)))
        assertEquals(expectedTile6, playerActionService.rotate(tile6))
    }
}