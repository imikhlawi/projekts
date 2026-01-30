@file:Suppress("DEPRECATION")

package service

import com.soywiz.korio.dynamic.KDynamic.Companion.int
import entity.Color
import entity.Path
import entity.Player
import entity.Tile
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * @author Ikhlawi
 * Methods from GameService to test
 */
internal class GameServiceTest {
    private lateinit var gameService : GameService

    private lateinit var rootService : RootService
    private lateinit var playerService: PlayerActionService

    @BeforeEach
    fun setUp() {

        rootService = RootService()
        gameService = rootService.gameService
        playerService = rootService.playerActionService


    }

    @Test
            /**
             * @author Ikhlawi
             * checking if the player have the right index
             * after call nextPlayer method from
             * the service class
             */
    fun nextPlayer()
    {

        val players = listOf(Player("sam",false), Player("Jen", false),
            Player("Jen", false) , Player("Jen", false) )
        gameService.startNewGame(players)
        rootService.currentGame!!.currentTurn.currentPlayerIndex = 1

        gameService.nextPlayer()


        assertFalse(2 == rootService.currentGame!!.currentTurn.currentPlayerIndex)
        assertNotEquals(players[3].name,"fae")
        gameService.nextPlayer()
        assertTrue(0 == players[3].int)


    }
    /**
     * @author Ikhlawi
     * checking if undo fun return the right value for
     * the player index and tiles
     */
    @Test
    fun undo()
    {
        val players = listOf(Player("sam",false), Player("Jen", false) )



        gameService.startNewGame(players, isLocalOnlyGame = false, isHostedGame = true, rotationAllowed = true)
        while (rootService.currentGame!!.currentTurn.gameField.tileStack.tiles.size==5){

            gameService.undo()

            assertFalse( rootService.currentGame!!.currentTurn.gameField.tileStack.tiles.size == 6)
            assertNotEquals(players[0].handTile, players[1].handTile )

            gameService.redo()
            val currentPLayerIndex=rootService.currentGame!!.currentTurn.currentPlayerIndex
            val tile=rootService.currentGame!!.currentTurn.gameField.tileStack.tiles
            assertTrue(players[currentPLayerIndex].handTile==tile.last())
        }

    }
    @Test
            /**
             * @author Ikhlawi
             * checking if redo fun return the right value for
             * the player index and tiles
             */
    fun redo()
    {
        val players = listOf(Player("sam",false), Player("Jen", false) )
        gameService.startNewGame(players, isLocalOnlyGame = false, isHostedGame = true, rotationAllowed = true)
             while (rootService.currentGame!!.currentTurn.gameField.tileStack.tiles.size>5)
                 {


                     gameService.nextPlayer()
                     assertTrue(rootService.currentGame!!.currentTurn.currentPlayerIndex == 0)
                     gameService.undo()
                     assertFalse(rootService.currentGame!!.currentTurn.currentPlayerIndex ==1)
                     gameService.redo()
                     assertTrue(rootService.currentGame!!.currentTurn.currentPlayerIndex == 0)
                     gameService.redo()
                     assertNotEquals(players[0].paths,players[1].paths)

    }

    }

    @Test
            /**
             * @author Ikhlawi
             * test the winners with their scores
             * with fun findWinner from the GameService
             */
    fun findWinner() {


        val players = listOf(Player("sam",false), Player("Jen", false), Player("Jen", false) , Player("Jen", false) )
        10.also {  players[0].score = it }
        20.also {  players[1].score = it }
        25.also {  players[2].score = it }
        5.also {  players[3].score = it }
        gameService.startNewGame(players)

        assertEquals(players[2].score,25)
        assertTrue(gameService.findWinner()[0] == rootService.currentGame!!.currentTurn.players[2])
        assertNotEquals(gameService.findWinner().size,5)
        assertFalse(players[0].score == 100 )

    }

    @Test
            /**
             * @author Ikhlawi
             * Check if two players have the right colors and cars
             */
    fun playersToPositions() {
        val players = mutableListOf(Player("sam",false), Player("Jen", false))

        when (players.size) {
            2 -> {
                for (i in 1..32) {
                    if (i % 2 == 1) {
                        players[0].color = Color.YELLOW
                        players[0].cars.add(i)
                    } else {
                        players[1].color = Color.BLUE
                        players[1].cars.add(i)
                    }
                }
            }
        }
        for (player in players) {
            for (pos in player.cars) {
                player.paths.add(Path(pos))
            }
        }

        assertEquals(Color.YELLOW, players[0].color)
        assertEquals(Color.BLUE, players[1].color)
        assertEquals(16, players[0].cars.size)
        assertEquals(16, players[1].cars.size)
        assertEquals(16, players[0].paths.size)
        assertEquals(16, players[1].paths.size)

    }
    @Test
            /**
             * @author Ikhlawi
             * check if a tile have wrong ports with method
             * distribute from the service class
             */
    fun distributeTiles()
    {
        val tile = Tile(mutableListOf(Pair(0,0),Pair(0,0),Pair(0,0),Pair(0,0),Pair(0,0)))
        val players = listOf(Player("sam",false), Player("Jen", false),
            Player("Jen", false) , Player("Jen", false) )
        gameService.startNewGame(players)
        gameService.distributeTiles()
        val lastTile = rootService.currentGame!!.currentTurn.players[0].handTile!!

        assertNotSame(tile,lastTile)
        assertNotEquals(lastTile.originalPorts,tile.ports)
        assertTrue(lastTile.id != 100)
    }

}