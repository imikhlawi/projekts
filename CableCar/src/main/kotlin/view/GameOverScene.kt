package view

import entity.Player
import service.RootService
import tools.aqua.bgw.components.gamecomponentviews.TokenView
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.CompoundVisual
import tools.aqua.bgw.visual.ImageVisual
import tools.aqua.bgw.visual.TextVisual
import java.awt.Color

/**
 *
 * GameOverScene displays score after game over.
 * [endGameLabel] End Game Header
 * [musicToggleButton],[soundToggleButton] : music and sound on/off
 * [p1Name],[p2Name],[p3Name],[p4Name],[p5Name],[p6Name] aligns players Name
 * [playerImg1],[playerImg2],[playerImg3],[playerImg4],[playerImg5],[playerImg6]:contains the image for players
 * [first],[second],[third],[fourth],[fifth],[sixth] aligns Players Rank
 * [crown] crown and crown position
 * [mainMenuButton] return to Main Menu
 * [quitButton] exit the game
 *
 */

class GameOverScene(private val rootService: RootService) : BoardGameScene(1920, 1080), Refreshable {

    //End Game Title Label
    private val endGameLabel = Label(height = 200, width = 1920,
        font = Font(100, Color.WHITE, family = "Calibri", fontWeight = Font.FontWeight.BOLD), text = "GAME OVER")

    val musicToggleButton = Button(
        width = 140, height = 140, posX = 1720, posY = 880, visual = ImageVisual("music_enabled.png")    )

    val soundToggleButton = Button(
        width = 140, height = 140, posX = 1520, posY = 880, visual = ImageVisual("sound_enabled.png")    )

    val fullscreenToggleButton = Button(width = 140, height = 140, posX = 1320, posY = 880,
        visual = ImageVisual("fullscreen.png"))

    //aligns player names
    private var p1Name = Label(width = 400, height = 50, posX = 825, posY = 810,
        font = Font(40, Color.YELLOW, family = "Calibri", fontStyle = Font.FontStyle.OBLIQUE),
        text = "TIME TO SCREAM!")
    private var p2Name = Label(width = 400, height = 50, posX = 1075, posY = 760,
        font = Font(40, Color.BLUE, family = "Calibri", fontStyle = Font.FontStyle.OBLIQUE),
        text = "aaaaaaaaaaaaaaaa")
    private var p3Name = Label(width = 400, height = 50, posX = 600, posY = 760,
        font = Font(40, Color.ORANGE, family = "Calibri", fontStyle = Font.FontStyle.OBLIQUE),
        text = "aaaaaaaaaaaaaaaaaaaaaaaa").apply { isVisible=false }
    private var p4Name = Label(width = 400, height = 50, posX = 65, posY = 760,
        font = Font(40, Color.GREEN, family = "Calibri", fontStyle = Font.FontStyle.OBLIQUE),
        text = "aaaaaaaaaaaaaaaaaaaaa").apply { isVisible=false }
    private var p5Name = Label(width = 400, height = 50, posX = 315, posY = 810,
        font = Font(40, Color(183,0,255), family = "Calibri", fontStyle = Font.FontStyle.OBLIQUE),
        text = "aaaaaaaaaaaaaaaaaa").apply { isVisible=false }
    private var p6Name = Label(width = 400, height = 50, posX = 1350, posY = 810,
        font = Font(40, Color.BLACK, family = "Calibri", fontStyle = Font.FontStyle.OBLIQUE),
        text = "aaaaaaaaaaaaaaaaaaa").apply { isVisible=false }

    //aligns player image
    private val playerImg1 = TokenView(
        width = 188, height = 500, posX = 925, posY = 275, visual = ImageVisual("Player1.png")    )
    private val playerImg2 = TokenView(
        width = 193, height = 500, posX = 1175, posY = 275, visual = ImageVisual("Player2.png")    )
    private val playerImg3 = TokenView(
        width = 192, height = 500, posX = 675, posY = 275, visual = ImageVisual("Player3.png")    ).
    apply { isVisible=false }
    private val playerImg4 = TokenView(
        width = 191, height = 500, posX = 175, posY = 275, visual = ImageVisual("Player4.png")    ).
    apply { isVisible=false }
    private val playerImg5 = TokenView(
        width = 217.6, height = 600, posX = 425, posY = 175, visual = ImageVisual("Player5.png")    ).
    apply { isVisible=false }
    private val playerImg6 = TokenView(
        width = 193.3, height = 600, posX = 1425, posY = 175, visual = ImageVisual("Player6.png")    ).
    apply { isVisible=false }

    //aligns players Rank
    private var first = TokenView(
        width = 75, height = 33.4, posX = 925, posY = 225, visual = ImageVisual("1st.png")    )
    private val second = TokenView(
        width = 75, height = 31.4, posX = 1175, posY = 225, visual = ImageVisual("2nd.png")    )
    private val third = TokenView(
        width = 75, height = 32, posX = 675, posY = 225, visual = ImageVisual("3rd.png")    ).
    apply { isVisible=false }
    private val fourth = TokenView(
        width = 75, height = 35.5, posX = 175, posY = 225, visual = ImageVisual("4th.png")    ).
    apply { isVisible=false }
    private val fifth = TokenView(
        width = 75, height = 35.7,posX = 425, posY = 125, visual = ImageVisual("5th.png")    ).
    apply { isVisible=false }
    private val sixth = TokenView(
        width = 75, height = 34.6, posX = 1425, posY = 125, visual = ImageVisual("6th.png")    ).
    apply { isVisible=false }

    private val crown = TokenView(
        width = 120, height = 117.6, posX = 990, posY = 220, visual = ImageVisual("Crown.png")    )

    /*private val restartButton = Button(
        width = 400, height = 100, posX = 181, posY = 880, visual = CompoundVisual(
            ColorVisual.WHITE.apply { transparency = 0.3 }, TextVisual(
                font = Font(size = 60, color = Color.RED, family = "Calibri"), text = "Restart Game"
            )
        )
    ).apply {
        onMouseClicked = {
            resetConfig()
            recreateGame()
        }
    }*/

    // Back to main Menu button
    val mainMenuButton = Button(
        width = 400, height = 100, posX = 100, posY = 900, visual = CompoundVisual(
            ColorVisual.WHITE.apply { transparency = 0.3 }, TextVisual(
                font = Font(size = 60, color = Color.RED, family = "Calibri"), text = "Main Menu"
            )
        )
    )

    val quitButton = Button(
        width = 140, height = 140, posX = 1700, posY = 40, visual = ImageVisual("quit_button.png")    )

    init {
        addComponents(
            soundToggleButton, musicToggleButton, fullscreenToggleButton,
            p4Name, p5Name, p3Name, p1Name, p2Name, p6Name,
            playerImg1, playerImg2, playerImg3, playerImg4, playerImg5, playerImg6,
            crown, first, second, third, fourth, fifth, sixth, mainMenuButton, quitButton, endGameLabel
        )
        arrayOf(
            soundToggleButton, musicToggleButton, fullscreenToggleButton,
            p1Name, p2Name, p3Name, p4Name, p5Name, p6Name,
            playerImg1, playerImg2, playerImg3, playerImg4, playerImg5, playerImg6,
            crown, first, second, third, fourth, fifth, sixth, mainMenuButton, quitButton, endGameLabel
        ).forEach { it.opacity = 1.0 }

        background = ImageVisual("game_over_screen.jpg")
        opacity = 1.0
    }

    /**
     * override refreshAfterGameFinished to get the list of :
     * players,score and winners in a sorted way from the current game
     */
    override fun refreshAfterGameFinished() {

        val winner: List<Player> = rootService.gameService.findWinner()
        winner.forEach { println(it.score) }

        p1Name.text = "${winner[0].name}: ${winner[0].score}"

        p2Name.text = "${winner[1].name}: ${winner[1].score}"

        if (rootService.currentGame!!.currentTurn.players.size > 2) {
            p3Name.text = "${winner[2].name}: ${winner[2].score}"
            arrayOf(p3Name, third, playerImg3).forEach { it.isVisible=true;it.opacity = 1.0  }
        }

        if (rootService.currentGame!!.currentTurn.players.size > 3) {
            p4Name.text = "${winner[3].name}: ${winner[3].score}"
            arrayOf(p4Name, fourth, playerImg4).forEach { it.isVisible=true;it.opacity = 1.0 }
        }

        if (rootService.currentGame!!.currentTurn.players.size > 4) {
            p5Name.text = "${winner[4].name}: ${winner[4].score}"
            arrayOf(p5Name, fifth, playerImg5).forEach { it.isVisible=true;it.opacity = 1.0  }
        }

        if (rootService.currentGame!!.currentTurn.players.size > 5) {
            p6Name.text = "${winner[5].name}: ${winner[5].score}"
            arrayOf(p6Name, sixth, playerImg6).forEach { it.isVisible=true;it.opacity = 1.0  }
        }
    }

    /**
     * reset the player name and score of the current game
     */

     fun resetConfig() {
        /*var playerList = rootService.currentGame!!.currentTurn.players
        var winnerList=rootService.gameService.findWinner()
        for (winner in winnerList) {
            winner.name = ""
            winner.score = 0
        }
        playerList= emptyList<Player>().toMutableList()
        winnerList=emptyList<Player>().toMutableList()*/

        arrayOf(p1Name,p2Name,p3Name, p4Name, p5Name, p6Name,
        ).forEach { it.text=""; it.name="";it.isVisible=false; it.opacity=0.0}

        arrayOf(p3Name, p4Name, p5Name, p6Name, third, fourth, fifth, sixth,
            playerImg3, playerImg4, playerImg5, playerImg6
        ).forEach { it.opacity = 0.0 }
    }

    /*/**
     * saves the current playerList if the user restarts the game
     */
    private fun recreateGame() {
        val playerName = mutableListOf<Player>()
        for ((i, player) in rootService.currentGame!!.currentTurn.players.withIndex()) {
            player.name.also { playerName[i] }
        }
        refreshAfterRestartGame(playerName)
    }*/

}