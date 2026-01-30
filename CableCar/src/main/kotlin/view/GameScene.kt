package view

import entity.Player
import entity.Tile
import entity.Turn
import service.CardImageLoader
import service.GameService
import service.PlayerActionService
import service.RootService
import tools.aqua.bgw.components.ComponentView
import tools.aqua.bgw.components.gamecomponentviews.CardView
import tools.aqua.bgw.components.gamecomponentviews.TokenView
import tools.aqua.bgw.components.layoutviews.GridPane
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.CompoundVisual
import tools.aqua.bgw.visual.ImageVisual
import tools.aqua.bgw.visual.TextVisual
import java.awt.Color

/**
 * main scene
 *
 * Attributes:
 * [handTileLabel]: initializes player's current tile(s) and drawn tile (/tilestack)
 * [rotateButton]: rotates chosen tile by 90 degrees before placing on game field
 */

class GameScene(private val rootService: RootService) : BoardGameScene(1920, 1080), Refreshable {

    private val cardImageLoader = CardImageLoader()

    private lateinit var gameService: GameService
    private lateinit var playerActionService: PlayerActionService
    private var currentTurn: Turn? = null
    var playerList = listOf<Player>()
    private var isInputPlayer = mutableListOf(false,false,false,false,false,false)

    private var networkPlayerName :String? = null
    private var isJoinAiGameScene : Boolean? = null
    private val tileBackImage = ImageVisual("tile_back.png")

    private var currentTile: Tile? = null
    private var currentTileCardView: CardView? = null
    private var isDrawStackTileChosen : Boolean? = null
    private var failSafeExitCount = 0

    private val labelFont = Font(50, Color.BLACK, family = "Calibri")
    private val playerScoreFont = Font(40, Color.BLACK, family = "Calibri")
    private val playerScoreHighlightedFont = Font(40, Color.GREEN, family = "Calibri")
    private val buttonTextFont = Font(30, color = Color.WHITE, family = "Calibri")

    private val playersGrid = GridPane<GridPane<ComponentView>>(columns = 1, rows = 6,
        spacing = 50.0, posX = 62, posY = 216, layoutFromCenter = false)
    private val topStationGrid = GridPane<ComponentView>(columns = 8, rows = 1,
        posX = 560, posY = 40, layoutFromCenter = false)
    private val leftStationGrid = GridPane<ComponentView>(columns = 1, rows = 8,
        posX = 460, posY = 140, layoutFromCenter = false)
    private val rightStationGrid = GridPane<ComponentView>(columns = 1, rows = 8,
        posX = 1360, posY = 140, layoutFromCenter = false)
    private val bottomStationGrid = GridPane<ComponentView>(columns = 8, rows = 1,
        posX = 560, posY = 940, layoutFromCenter = false)
    private val mainBoardGrid = GridPane<ComponentView>(columns = 8, rows = 8,
        posX = 560, posY = 140, layoutFromCenter = false)

    private val player1HandCard = CardView(width = 100, height = 100, posX = 35, posY = 820,
        front = ColorVisual.WHITE, back = tileBackImage).apply { isDisabled = true; opacity = 0.0 }
    private val player2HandCard = CardView(width = 100, height = 100, posX = 155, posY = 820,
        front = ColorVisual.WHITE, back = tileBackImage).apply { isDisabled = true; opacity = 0.0 }
    private val player3HandCard = CardView(width = 100, height = 100, posX = 275, posY = 820,
        front = ColorVisual.WHITE, back = tileBackImage).apply { isDisabled = true; opacity = 0.0 }
    private val player4HandCard = CardView(width = 100, height = 100, posX = 35, posY = 940,
        front = ColorVisual.WHITE, back = tileBackImage).apply { isDisabled = true; opacity = 0.0 }
    private val player5HandCard = CardView(width = 100, height = 100, posX = 155, posY = 940,
        front = ColorVisual.WHITE, back = tileBackImage).apply { isDisabled = true; opacity = 0.0 }
    private val player6HandCard = CardView(width = 100, height = 100, posX = 275, posY = 940,
        front = ColorVisual.WHITE, back = tileBackImage).apply { isDisabled = true; opacity = 0.0 }

    private val playerHandCardList = mutableListOf(player1HandCard,player2HandCard,player3HandCard,
            player4HandCard,player5HandCard,player6HandCard)

    private val player1HandCardBG = TokenView(width = 120, height = 120, posX = 25, posY = 810,
        visual = ColorVisual.YELLOW).apply { isDisabled = true; opacity = 0.0 }
    private val player2HandCardBG = TokenView(width = 120, height = 120, posX = 145, posY = 810,
        visual = ColorVisual.BLUE).apply { isDisabled = true; opacity = 0.0 }
    private val player3HandCardBG = TokenView(width = 120, height = 120, posX = 265, posY = 810,
        visual = ColorVisual.ORANGE).apply { isDisabled = true; opacity = 0.0 }
    private val player4HandCardBG = TokenView(width = 120, height = 120, posX = 25, posY = 930,
        visual = ColorVisual.GREEN).apply { isDisabled = true; opacity = 0.0 }
    private val player5HandCardBG = TokenView(width = 120, height = 120, posX = 145, posY = 930,
        visual = ColorVisual(183,0,255)).apply { isDisabled = true; opacity = 0.0 }
    private val player6HandCardBG = TokenView(width = 120, height = 120, posX = 265, posY = 930,
        visual = ColorVisual.BLACK).apply { isDisabled = true; opacity = 0.0 }

    private val playerHandCardBGList = mutableListOf(player1HandCardBG,player2HandCardBG,player3HandCardBG,
        player4HandCardBG,player5HandCardBG,player6HandCardBG)

    private val handTileLabel = Label(width = 300, height = 100, posX = 1564, posY = 50,
        font = labelFont, text = "Hand Tile").apply { isDisabled = true; opacity = 0.0 }

    private val handTileCardView = CardView(width = 200, height = 200, posX = 1614, posY = 140,
        front = ColorVisual.WHITE, back = tileBackImage
    ).apply {
        isDisabled = true; opacity = 0.0
        flip()
        onMouseClicked = {
            if (isDrawStackTileChosen == null) {
                hintLabel.opacity = 0.0
                currentTileCardView = this
                currentTile = currentTurn?.players?.get(currentTurn!!.currentPlayerIndex)?.handTile
                isDrawStackTileChosen = false
            }
        }
    }

    private val drawnTilesLabel = Label(height = 100, width = 300, posX = 1564, posY = 360,
        font = labelFont, text = "Draw Stack").apply { isDisabled = true; opacity = 0.0 }

    private val drawnTilesCardView = CardView(height = 200, width = 200, posX = 1614, posY = 450,
        front = ColorVisual.WHITE, back = tileBackImage
    ).apply {
        isDisabled = true; opacity = 0.0
        onMouseClicked = {
            if (isDrawStackTileChosen == null || isDrawStackTileChosen == false) {
                hintLabel.opacity = 0.0
                flip()
                handTileCardView.flip()
                currentTileCardView = this
                handTileCardView.isDisabled = true
                currentTile = currentTurn?.gameField?.tileStack?.tiles?.first()
                isDrawStackTileChosen = true
            }
        }
    }

    private val undoButton = Button(width = 150, height = 50, posX = 1539, posY = 770,
        font = buttonTextFont, text = "Undo", visual = ColorVisual(186, 136, 133)
    ).apply { isDisabled = true; opacity = 0.0; onMouseClicked = { gameService.undo() } }

    private val redoButton = Button(width = 150, height = 50, posX = 1739, posY = 770,
        font = buttonTextFont, text = "Redo", visual = ColorVisual(186, 136, 133)
    ).apply { isDisabled = true; opacity = 0.0; onMouseClicked = { gameService.redo() } }

    private val rotateButton = Button(width = 150, height = 50, posX = 1639, posY = 690,
        font = buttonTextFont, text = "Rotate", visual = ColorVisual(186, 136, 133, 255)
    ).apply {
        isDisabled = true; opacity = 0.0
        onMouseClicked = {
            if (isDrawStackTileChosen != null && currentTile != null && currentTileCardView != null) {
                currentTile!!.ports = playerActionService.rotate(currentTile!!).ports
                currentTile!!.rotationDegree = (currentTile!!.rotationDegree + 90) % 360
                currentTileCardView!!.frontVisual = setTileFront(currentTile) } } }

    private val hintLabel = Label(width = 380, height = 180, posX = 1524, posY = 850, visual = CompoundVisual(
        ColorVisual.WHITE.apply { transparency = 0.5 },
        TextVisual(font = Font(size = 44, color = Color.RED, family = "Calibri", fontStyle = Font.FontStyle.ITALIC),
            text = "Choose tile either\nfrom hand or stack!"))
    ).apply { isDisabled = true; opacity = 0.0 }

    private val playerInputBGLabel = Label(width = 420, height = 1020, posX = 1505, posY = 40,
        visual = ColorVisual.WHITE.apply { transparency = 0.35 }
    )


    private val playerInputs = listOf(handTileLabel, handTileCardView, drawnTilesLabel, drawnTilesCardView, undoButton,
        redoButton, rotateButton, hintLabel, playerInputBGLabel)

    val quickMenuButton = Button(width = 140, height = 140, posX = 40, posY = 40,
        visual = ImageVisual("quick_menu_button.png"))

    private val playerScoreBGLabel = Label(width = 420, height = 580, posY = 200, visual = ColorVisual.WHITE
        ).apply { opacity = 0.8 }

    private val boardCellLabel = arrayOf(
        arrayOf(TokenView(visual = ColorVisual(0,0,0,0)), TokenView(visual = ColorVisual(0,0,0,0)),
            TokenView(visual = ColorVisual(0,0,0,0)), TokenView(visual = ColorVisual(0,0,0,0)),
            TokenView(visual = ColorVisual(0,0,0,0)), TokenView(visual = ColorVisual(0,0,0,0)),
            TokenView(visual = ColorVisual(0,0,0,0)), TokenView(visual = ColorVisual(0,0,0,0))),
        arrayOf(TokenView(visual = ColorVisual(0,0,0,0)), TokenView(visual = ColorVisual(0,0,0,0)),
            TokenView(visual = ColorVisual(0,0,0,0)), TokenView(visual = ColorVisual(0,0,0,0)),
            TokenView(visual = ColorVisual(0,0,0,0)), TokenView(visual = ColorVisual(0,0,0,0)),
            TokenView(visual = ColorVisual(0,0,0,0)), TokenView(visual = ColorVisual(0,0,0,0))),
        arrayOf(TokenView(visual = ColorVisual(0,0,0,0)), TokenView(visual = ColorVisual(0,0,0,0)),
            TokenView(visual = ColorVisual(0,0,0,0)), TokenView(visual = ColorVisual(0,0,0,0)),
            TokenView(visual = ColorVisual(0,0,0,0)), TokenView(visual = ColorVisual(0,0,0,0)),
            TokenView(visual = ColorVisual(0,0,0,0)), TokenView(visual = ColorVisual(0,0,0,0))),
        arrayOf(TokenView(visual = ColorVisual(0,0,0,0)), TokenView(visual = ColorVisual(0,0,0,0)),
            TokenView(visual = ColorVisual(0,0,0,0)), TokenView(visual = ColorVisual(0,0,0,0)),
            TokenView(visual = ColorVisual(0,0,0,0)), TokenView(visual = ColorVisual(0,0,0,0)),
            TokenView(visual = ColorVisual(0,0,0,0)), TokenView(visual = ColorVisual(0,0,0,0))),
        arrayOf(TokenView(visual = ColorVisual(0,0,0,0)), TokenView(visual = ColorVisual(0,0,0,0)),
            TokenView(visual = ColorVisual(0,0,0,0)), TokenView(visual = ColorVisual(0,0,0,0)),
            TokenView(visual = ColorVisual(0,0,0,0)), TokenView(visual = ColorVisual(0,0,0,0)),
            TokenView(visual = ColorVisual(0,0,0,0)), TokenView(visual = ColorVisual(0,0,0,0))),
        arrayOf(TokenView(visual = ColorVisual(0,0,0,0)), TokenView(visual = ColorVisual(0,0,0,0)),
            TokenView(visual = ColorVisual(0,0,0,0)), TokenView(visual = ColorVisual(0,0,0,0)),
            TokenView(visual = ColorVisual(0,0,0,0)), TokenView(visual = ColorVisual(0,0,0,0)),
            TokenView(visual = ColorVisual(0,0,0,0)), TokenView(visual = ColorVisual(0,0,0,0))),
        arrayOf(TokenView(visual = ColorVisual(0,0,0,0)), TokenView(visual = ColorVisual(0,0,0,0)),
            TokenView(visual = ColorVisual(0,0,0,0)), TokenView(visual = ColorVisual(0,0,0,0)),
            TokenView(visual = ColorVisual(0,0,0,0)), TokenView(visual = ColorVisual(0,0,0,0)),
            TokenView(visual = ColorVisual(0,0,0,0)), TokenView(visual = ColorVisual(0,0,0,0))),
        arrayOf(TokenView(visual = ColorVisual(0,0,0,0)), TokenView(visual = ColorVisual(0,0,0,0)),
            TokenView(visual = ColorVisual(0,0,0,0)), TokenView(visual = ColorVisual(0,0,0,0)),
            TokenView(visual = ColorVisual(0,0,0,0)), TokenView(visual = ColorVisual(0,0,0,0)),
            TokenView(visual = ColorVisual(0,0,0,0)), TokenView(visual = ColorVisual(0,0,0,0))),
        )

    val startGameButton = Button(width = 350, height = 100, posX = 20, posY = 880, visual = CompoundVisual(
        ColorVisual.WHITE.apply { transparency = 0.5 },
        TextVisual(font = Font(size = 60, color = Color.RED, family = "Calibri"), text = "Start Game"))
    ).apply { isDisabled = true; opacity = 0.0 }

    val pleaseWaitLabel = Button(width = 600, height = 200, posX = 660, posY = 440, visual = CompoundVisual(
        ColorVisual.WHITE.apply { transparency = 0.5 },
        TextVisual(font = Font(size = 60, color = Color.RED, family = "Calibri", fontStyle = Font.FontStyle.ITALIC),
            text = "please wait..."))
    ).apply { isDisabled = true; opacity = 0.0 }

    private val failSafeExitButton = Button(width = 10, height = 10, posX = 1910, posY = 0, visual = ColorVisual(0,0,0,0)
    ).apply { onMouseClicked = {
        failSafeExitCount++
        if (failSafeExitCount == 5) {
            rootService.gameService.endGame()
        }
    } }

    init {
        playersGrid.setColumnWidths(400)

        background = ImageVisual("game_scene.png")
        addComponents(playerScoreBGLabel, playersGrid, topStationGrid, leftStationGrid, rightStationGrid,
            bottomStationGrid, mainBoardGrid, quickMenuButton, startGameButton,
            playerInputBGLabel, handTileLabel, handTileCardView, failSafeExitButton,
            player1HandCardBG,player2HandCardBG,player3HandCardBG,player4HandCardBG,player5HandCardBG,player6HandCardBG,
            player1HandCard,player2HandCard,player3HandCard,player4HandCard,player5HandCard,player6HandCard,
            drawnTilesLabel, drawnTilesCardView, undoButton, redoButton, rotateButton, hintLabel, pleaseWaitLabel)
    }

    /**
     * only called in host mode: shows primitive lobby screen with player names and shows start game button, when permitted
     */

    fun hostGameWaitForPlayers(hostName :String, isHostAi : Boolean) {
        this.networkPlayerName = hostName
        playerList += Player(hostName,if(isHostAi) true else null)
        playerList.forEach { println("playerList " + it.name) }
        rootService.networkService.joinedPlayers.forEach { println("net playerList$it") }
        showPlayers()
        if(playerList.size in 2..6) {startGameButton.isDisabled = false; startGameButton.opacity = 1.0}
        else {startGameButton.isDisabled = true; startGameButton.opacity = 0.0}
    }

    /**
     * update player table on join
     */

    override fun refreshAfterPlayerJoinedInWaitSession(playerName:String){
        playerList += Player(playerName,null)
        playerList.forEach { println("playerList " + it.name) }
        rootService.networkService.joinedPlayers.forEach { println("net playerList$it") }
        showPlayers()
        if(playerList.size in 2..6) {startGameButton.isDisabled = false; startGameButton.opacity = 1.0}
        else {startGameButton.isDisabled = true; startGameButton.opacity = 0.0}
    }

    /**
     * update player table on leave
     */

    override fun refreshAfterPlayerLeftInWaitSession(playerName:String){
        val toBeDeleted = playerList.find { it.name == playerName }
        playerList -= toBeDeleted!!
        playerList.forEach { println("playerList " + it.name) }
        rootService.networkService.joinedPlayers.forEach { println("net playerList$it") }
        showPlayers()
        if(playerList.size in 2..6) {startGameButton.isDisabled = false; startGameButton.opacity = 1.0}
        else {startGameButton.isDisabled = true; startGameButton.opacity = 0.0}
    }

    /**
     * only called in join mode
     */

    fun joinGameWaitForPlayers(joinName :String, isJoinAi : Boolean) {
        this.networkPlayerName = joinName
        isJoinAiGameScene = isJoinAi
        pleaseWaitLabel.opacity = 1.0; pleaseWaitLabel.isDisabled = false
    }

    /**
     * main scene init method. determines if players are to make inputs. if first player is AI playAITurn() is called
     */

    override fun refreshAfterStartGame() {
        gameService = rootService.gameService
        playerActionService = rootService.playerActionService
        playerList = rootService.currentGame!!.currentTurn.players
        currentTurn = rootService.currentGame!!.currentTurn

        undoButton.isVisible = true; redoButton.isVisible = true

        if(networkPlayerName == null)         //Hotseat mode
            for( i in playerList.indices)
                isInputPlayer[i] = playerList[i].isSmartAi == null
        else {
            if(isJoinAiGameScene == null) {         //host mode
                isInputPlayer[playerList.indexOf(playerList.find { it.name == networkPlayerName })] =
                    playerList.find { it.name == networkPlayerName }?.isSmartAi != true
            }       //join mode
            else{
                isInputPlayer[playerList.indexOf(playerList.find { it.name == networkPlayerName })] =
                    !isJoinAiGameScene!!
            }

            undoButton.isVisible = false; redoButton.isVisible = false
        }

        println(isInputPlayer)

        pleaseWaitLabel.opacity = 0.0; pleaseWaitLabel.isDisabled = true
        drawnTilesLabel.isVisible = true; drawnTilesCardView.isVisible = true

        initGameBoard()
        initStationPosition()

        playerList.forEach { println(it.isSmartAi) }

        if (playerList[rootService.currentGame!!.currentTurn.currentPlayerIndex].isSmartAi != null)
            rootService.playerActionService.playAiTurn(gameService.rotationAllowed)

        turn()
    }

    /**
     * initializes game board,
     * each board cell is a token view, click on board cell to place tile
     */

    private fun initGameBoard() {
        val mainStationPos = 3..4

        for (i in 0..7) for (j in 0..7) {

            if (i in mainStationPos && j in mainStationPos) {
                boardCellLabel[i][j] = TokenView(height = 100, width = 100,
                    visual = ColorVisual(0, 0, 0, 0))
            } else {
                boardCellLabel[i][j] = TokenView(height = 100, width = 100,
                    visual = ColorVisual(0, 0, 0, 0)
                ).apply {
                    onMouseClicked = {
                        if (PlayerActionService.isPositionLegal(i+1, j+1, rootService.currentGame!!.currentTurn)
                            && isDrawStackTileChosen != null) {
                            playerActionService.placeTile(!isDrawStackTileChosen!!, i+1, j+1,
                                currentTile!!.rotationDegree/90)
                        } else {
                            gameService.playNopeSound()

                        }
                    }
                }
            }
            mainBoardGrid[i, j] = boardCellLabel[i][j]
        }
    }

    /**
     * initializes all stations in 2d-array to positions around the game board
     */

    private fun initStationPosition() {
        val stations = initStationArray()

        for (i in 0..3) for (j in 0..7) {

            val stationCardView = TokenView(
                height = 100, width = 100,
                visual = ImageVisual(cardImageLoader.stationImage(stations[i][j].first, stations[i][j].second))
            ).apply {
                if ((i == 1 || i == 2) && j == 7 && stations[i][j] == Pair(entity.Color.BLACK, false)) {
                    this.visual = ColorVisual(0,0,0,0)
                }
                when (i) {
                    0 -> this.rotation = 90.0
                    1 -> this.rotation = 180.0
                    2 -> this.rotation = 270.0
                    3 -> this.rotation = 0.0
                }
            }

            when (i) {
                0 -> topStationGrid[j, 0] = stationCardView
                1 -> rightStationGrid[0, j] = stationCardView
                2 -> bottomStationGrid[j, 0] = stationCardView
                3 -> leftStationGrid[0, j] = stationCardView
            }
        }
    }

    /**
     * method is called on every turn. refreshes scene and shows input options if necessary
     */

    fun turn() {
        playerList = rootService.currentGame!!.currentTurn.players
        currentTurn = rootService.currentGame!!.currentTurn

        showPlayers(); refreshGameBoard(); playersHandCard()
        println("tileStack :" + currentTurn!!.gameField.tileStack.tiles.count())
        if (currentTile != null) currentTile!!.rotationDegree = 0
        if (currentTileCardView != null) currentTileCardView!!.rotation = 0.0
        playerInputs.forEach { it.opacity = 0.0; it.isDisabled = true }

        println("currentPlayer Index:"+rootService.currentGame?.currentTurn?.currentPlayerIndex!!)
        println("handCards1:"+ rootService.currentGame!!.currentTurn.players[0].handTile)
        println("handCards2:"+ rootService.currentGame!!.currentTurn.players[1].handTile)

        if (isInputPlayer[rootService.currentGame?.currentTurn?.currentPlayerIndex!!]) {
            playerInputs.forEach { it.opacity = 1.0; it.isDisabled = false }

            if(currentTurn!!.gameField.tileStack.tiles.isNotEmpty()){
                drawnTilesCardView.frontVisual = setTileFront(currentTurn!!.gameField.tileStack.tiles.first())
            }
            if(currentTurn!!.players[(currentTurn!!.currentPlayerIndex)%currentTurn!!.players.size].handTile!=null)
            {
                handTileCardView.frontVisual = setTileFront(currentTurn!!.players[ (currentTurn!!.currentPlayerIndex)
                        % currentTurn!!.players.size ].handTile!!)
            } else { handTileCardView.frontVisual = ColorVisual(0,0,0,0) }

            //increment order workaround

            handTileCardView.showFront()
            isDrawStackTileChosen = null
            drawnTilesCardView.showBack()

            rotateButton.isVisible = gameService.rotationAllowed
        }
    }

    /**
     * refreshes player list with score. on the left side. current player is highlighted green
     */

    private fun showPlayers() {

        for (i in 0..5) {
            val playerGrid = GridPane<ComponentView>(columns = 5, rows = 1)

            if (i in playerList.indices){

                val playerColorLabel = Label(height = 50, width = 50,
                    visual = when(i) {
                        0 -> ColorVisual.YELLOW; 1 -> ColorVisual.BLUE; 2 -> ColorVisual.ORANGE; 3 -> ColorVisual.GREEN
                        4 -> ColorVisual(183,0,255); else -> ColorVisual.BLACK }
                ).apply {  }

                playerGrid[0,0] = playerColorLabel

                playerGrid[1,0] = Label(width = 10, visual = ColorVisual(0,0,0,0))

                // highlight current player
                val playerNameLabel = Label(width = 270, height = 50, font = playerScoreFont, text = playerList[i].name,
                    alignment = Alignment.CENTER_LEFT
                ).apply { if (currentTurn != null && i == currentTurn!!.currentPlayerIndex)
                    font = playerScoreHighlightedFont }

                playerGrid[2, 0] = playerNameLabel

                // highlight current player
                val playerScoreLabel = Label(width = 70, height = 50, font = playerScoreFont,
                    text = playerList[i].score.toString(), alignment = Alignment.CENTER_RIGHT
                ).apply { if (currentTurn != null && i == currentTurn!!.currentPlayerIndex)
                    font = playerScoreHighlightedFont }

                playerGrid[3,0] = playerScoreLabel

                playerGrid[4,0] = Label(width = 100, visual = ColorVisual(0,0,0,0))
            }
            playersGrid[0,i] = playerGrid
        }
    }

    /**
     * refreshes display of game field of all places tiles
     */

    private fun refreshGameBoard() {
        for (i in 0..7) for (j in 0..7) {
            val boardCellTile = rootService.currentGame!!.currentTurn.gameField.field[i+1][j+1]
            if (boardCellTile != null)
                boardCellLabel[i][j].visual = setTileFront(boardCellTile)
            else
                boardCellLabel[i][j].visual = ColorVisual(0,0,0,0)
        }
    }

    /**
     * refreshes view of all current hand tiles distributed to active players
     */

    private fun playersHandCard(){
        // by default is 2 players and player card set initialized
        for (i in playerList.indices) {
            playerHandCardList[i].apply { isDisabled = false; opacity = 1.0 }
            playerHandCardBGList[i].apply { isDisabled = false; opacity = 1.0 }
            if (currentTurn!!.players[i].handTile != null)
                playerHandCardList[i].frontVisual = setTileFront(currentTurn!!.players[i].handTile!!)
            else
                playerHandCardList[i].frontVisual = ColorVisual(0,0,0,0)
            playerHandCardList[i].showFront()
        }
    }

    /**
     * triggered by service when ready for next turn
     */

    override fun refreshAfterTurn() { turn() }

    /**
     * triggered by service if draw stack empty
     */

    override fun refreshAfterDrawStackEmpty() { drawnTilesCardView.isVisible = false; drawnTilesLabel.isVisible = false }

    /**
     * resets gamescene internal attributes not accessed by service
     */

    fun resetScene(){
        networkPlayerName = null
        isJoinAiGameScene = null
        playerList = emptyList()
        currentTurn = null
        currentTile = null
        currentTileCardView = null
        isDrawStackTileChosen = null
        failSafeExitCount = 0
    }

    /**
     * 2D-array performs all stations around the game board and their car's colors according to player number
     * when number of players is 1,2,4: 32 cars with color
     * when number of players is 3,5,6: no car at positions 16 & 17 (30 cars with color)
     */

    private fun initStationArray(): Array<Array<Pair<entity.Color, Boolean>>> {
        val numOfPlayers = playerList.size
        val stations = Array(4) { Array(8) { Pair(entity.Color.YELLOW, false) } }
        when (numOfPlayers) {
            2 -> {
                for (i in 0..1) {
                    for (j in 0..7) {
                        if ((i * 8 + j) % 2 == 0) {
                            stations[i][j] = Pair(entity.Color.YELLOW, false)
                        } else {
                            stations[i][j] = Pair(entity.Color.BLUE, false)
                        }
                    }
                }
                for (i in 2..3) {
                    for (j in 0..7) {
                        if ((i * 8 + j) % 2 == 0) {
                            stations[i][j] = Pair(entity.Color.BLUE, false)
                        } else {
                            stations[i][j] = Pair(entity.Color.YELLOW, false)
                        }
                    }
                }
                return stations
            }
            3 -> {
                stations[0][0] = Pair(entity.Color.YELLOW, false); stations[0][3] = Pair(entity.Color.YELLOW, false)
                stations[0][5] = Pair(entity.Color.YELLOW, false); stations[1][2] = Pair(entity.Color.YELLOW, false)
                stations[1][6] = Pair(entity.Color.YELLOW, false); stations[2][4] = Pair(entity.Color.YELLOW, false)
                stations[2][1] = Pair(entity.Color.YELLOW, false); stations[3][7] = Pair(entity.Color.YELLOW, false)
                stations[3][4] = Pair(entity.Color.YELLOW, false); stations[3][1] = Pair(entity.Color.YELLOW, false)

                stations[0][1] = Pair(entity.Color.BLUE, false); stations[0][6] = Pair(entity.Color.BLUE, false)
                stations[1][0] = Pair(entity.Color.BLUE, false); stations[1][3] = Pair(entity.Color.BLUE, false)
                stations[1][5] = Pair(entity.Color.BLUE, false); stations[2][5] = Pair(entity.Color.BLUE, false)
                stations[2][2] = Pair(entity.Color.BLUE, false); stations[3][5] = Pair(entity.Color.BLUE, false)
                stations[3][3] = Pair(entity.Color.BLUE, false); stations[3][0] = Pair(entity.Color.BLUE, false)

                stations[0][2] = Pair(entity.Color.ORANGE, false); stations[0][4] = Pair(entity.Color.ORANGE, false)
                stations[0][7] = Pair(entity.Color.ORANGE, false); stations[1][1] = Pair(entity.Color.ORANGE, false)
                stations[1][4] = Pair(entity.Color.ORANGE, false); stations[2][6] = Pair(entity.Color.ORANGE, false)
                stations[2][3] = Pair(entity.Color.ORANGE, false); stations[2][0] = Pair(entity.Color.ORANGE, false)
                stations[3][6] = Pair(entity.Color.ORANGE, false); stations[3][2] = Pair(entity.Color.ORANGE, false)

                stations[1][7] = Pair(entity.Color.BLACK, false); stations[2][7] = Pair(entity.Color.BLACK, false)
                return stations
            }
            4 -> {
                stations[0][3] = Pair(entity.Color.YELLOW, false); stations[0][6] = Pair(entity.Color.YELLOW, false)
                stations[1][2] = Pair(entity.Color.YELLOW, false); stations[1][7] = Pair(entity.Color.YELLOW, false)
                stations[2][4] = Pair(entity.Color.YELLOW, false); stations[2][1] = Pair(entity.Color.YELLOW, false)
                stations[3][5] = Pair(entity.Color.YELLOW, false); stations[3][0] = Pair(entity.Color.YELLOW, false)

                stations[0][2] = Pair(entity.Color.BLUE, false); stations[0][7] = Pair(entity.Color.BLUE, false)
                stations[1][3] = Pair(entity.Color.BLUE, false); stations[1][6] = Pair(entity.Color.BLUE, false)
                stations[2][5] = Pair(entity.Color.BLUE, false); stations[2][0] = Pair(entity.Color.BLUE, false)
                stations[3][4] = Pair(entity.Color.BLUE, false); stations[3][1] = Pair(entity.Color.BLUE, false)

                stations[0][0] = Pair(entity.Color.ORANGE, false); stations[0][5] = Pair(entity.Color.ORANGE, false)
                stations[1][1] = Pair(entity.Color.ORANGE, false); stations[1][4] = Pair(entity.Color.ORANGE, false)
                stations[2][6] = Pair(entity.Color.ORANGE, false); stations[2][3] = Pair(entity.Color.ORANGE, false)
                stations[3][7] = Pair(entity.Color.ORANGE, false); stations[3][2] = Pair(entity.Color.ORANGE, false)

                stations[0][1] = Pair(entity.Color.GREEN, false); stations[0][4] = Pair(entity.Color.GREEN, false)
                stations[1][0] = Pair(entity.Color.GREEN, false); stations[1][5] = Pair(entity.Color.GREEN, false)
                stations[2][7] = Pair(entity.Color.GREEN, false); stations[2][2] = Pair(entity.Color.GREEN, false)
                stations[3][6] = Pair(entity.Color.GREEN, false); stations[3][3] = Pair(entity.Color.GREEN, false)

                return stations
            }
            5 -> {
                stations[0][0] = Pair(entity.Color.YELLOW, false); stations[0][4] = Pair(entity.Color.YELLOW, false)
                stations[1][1] = Pair(entity.Color.YELLOW, false); stations[1][5] = Pair(entity.Color.YELLOW, false)
                stations[2][2] = Pair(entity.Color.YELLOW, false); stations[3][4] = Pair(entity.Color.YELLOW, false)

                stations[0][5] = Pair(entity.Color.BLUE, false); stations[1][3] = Pair(entity.Color.BLUE, false)
                stations[2][6] = Pair(entity.Color.BLUE, false); stations[2][1] = Pair(entity.Color.BLUE, false)
                stations[3][5] = Pair(entity.Color.BLUE, false); stations[3][0] = Pair(entity.Color.BLUE, false)

                stations[0][2] = Pair(entity.Color.ORANGE, false); stations[0][6] = Pair(entity.Color.ORANGE, false)
                stations[1][6] = Pair(entity.Color.ORANGE, false); stations[2][5] = Pair(entity.Color.ORANGE, false)
                stations[3][7] = Pair(entity.Color.ORANGE, false); stations[3][3] = Pair(entity.Color.ORANGE, false)

                stations[0][1] = Pair(entity.Color.GREEN, false); stations[1][0] = Pair(entity.Color.GREEN, false)
                stations[1][4] = Pair(entity.Color.GREEN, false); stations[2][3] = Pair(entity.Color.GREEN, false)
                stations[3][6] = Pair(entity.Color.GREEN, false); stations[3][2] = Pair(entity.Color.GREEN, false)

                stations[0][3] = Pair(entity.Color.PURPLE, false); stations[0][7] = Pair(entity.Color.PURPLE, false)
                stations[1][2] = Pair(entity.Color.PURPLE, false); stations[2][4] = Pair(entity.Color.PURPLE, false)
                stations[2][0] = Pair(entity.Color.PURPLE, false); stations[3][1] = Pair(entity.Color.PURPLE, false)

                stations[1][7] = Pair(entity.Color.BLACK, false); stations[2][7] = Pair(entity.Color.BLACK, false)
                return stations
            }
            6 -> {
                stations[0][0] = Pair(entity.Color.YELLOW, false); stations[0][4] = Pair(entity.Color.YELLOW, false)
                stations[1][1] = Pair(entity.Color.YELLOW, false); stations[2][5] = Pair(entity.Color.YELLOW, false)
                stations[3][5] = Pair(entity.Color.YELLOW, false)

                stations[0][1] = Pair(entity.Color.BLUE, false); stations[1][2] = Pair(entity.Color.BLUE, false)
                stations[2][6] = Pair(entity.Color.BLUE, false); stations[3][7] = Pair(entity.Color.BLUE, false)
                stations[3][3] = Pair(entity.Color.BLUE, false)

                stations[0][3] = Pair(entity.Color.ORANGE, false); stations[0][7] = Pair(entity.Color.ORANGE, false)
                stations[1][5] = Pair(entity.Color.ORANGE, false); stations[2][3] = Pair(entity.Color.ORANGE, false)
                stations[3][6] = Pair(entity.Color.ORANGE, false)

                stations[0][5] = Pair(entity.Color.GREEN, false); stations[1][6] = Pair(entity.Color.GREEN, false)
                stations[2][4] = Pair(entity.Color.GREEN, false); stations[2][0] = Pair(entity.Color.GREEN, false)
                stations[3][1] = Pair(entity.Color.GREEN, false)

                stations[0][2] = Pair(entity.Color.PURPLE, false); stations[1][0] = Pair(entity.Color.PURPLE, false)
                stations[1][4] = Pair(entity.Color.PURPLE, false); stations[2][1] = Pair(entity.Color.PURPLE, false)
                stations[3][2] = Pair(entity.Color.PURPLE, false)

                stations[0][6] = Pair(entity.Color.BLACK, false); stations[1][3] = Pair(entity.Color.BLACK, false)
                stations[2][2] = Pair(entity.Color.BLACK, false); stations[3][4] = Pair(entity.Color.BLACK, false)
                stations[3][0] = Pair(entity.Color.BLACK, false)

                stations[1][7] = Pair(entity.Color.BLACK, false); stations[2][7] = Pair(entity.Color.BLACK, false)
                return stations
            }
            else -> {
                return emptyArray()
            }
        }
    }

    /**
     * lut returns texture for input Tile
     */

    private fun setTileFront(tile: Tile?): ImageVisual {
        when (tile) {
            Tile(mutableListOf(Pair(0,1),Pair(2,7),Pair(3,4),Pair(5,6))) ->
                return ImageVisual(cardImageLoader.frontImage(0,0))
            Tile(mutableListOf(Pair(0,7),Pair(1,4),Pair(2,3),Pair(5,6))) ->
                return ImageVisual(cardImageLoader.frontImage(1,0))
            Tile(mutableListOf(Pair(0,7),Pair(1,2),Pair(3,6),Pair(4,5))) ->
                return ImageVisual(cardImageLoader.frontImage(2,0))
            Tile(mutableListOf(Pair(0,5),Pair(1,2),Pair(3,4),Pair(6,7))) ->
                return ImageVisual(cardImageLoader.frontImage(3,0))
            Tile(mutableListOf(Pair(0,5),Pair(1,6),Pair(2,7),Pair(3,4))) ->
                return ImageVisual(cardImageLoader.frontImage(0,1))
            Tile(mutableListOf(Pair(0,3),Pair(1,4),Pair(2,7),Pair(5,6))) ->
                return ImageVisual(cardImageLoader.frontImage(1,1))
            Tile(mutableListOf(Pair(0,7),Pair(1,4),Pair(2,5),Pair(3,6))) ->
                return ImageVisual(cardImageLoader.frontImage(2,1))
            Tile(mutableListOf(Pair(0,5),Pair(1,2),Pair(3,6),Pair(4,7))) ->
                return ImageVisual(cardImageLoader.frontImage(3,1))
            Tile(mutableListOf(Pair(0,7),Pair(1,6),Pair(2,3),Pair(4,5))) ->
                return ImageVisual(cardImageLoader.frontImage(0,2))
            Tile(mutableListOf(Pair(0,3),Pair(1,2),Pair(4,5),Pair(6,7))) ->
                return ImageVisual(cardImageLoader.frontImage(1,2))
            Tile(mutableListOf(Pair(0,1),Pair(2,5),Pair(3,4),Pair(6,7))) ->
                return ImageVisual(cardImageLoader.frontImage(2,2))
            Tile(mutableListOf(Pair(0,1),Pair(2,3),Pair(4,7),Pair(5,6))) ->
                return ImageVisual(cardImageLoader.frontImage(3,2))
            Tile(mutableListOf(Pair(0,3),Pair(1,6),Pair(2,7),Pair(4,5))) ->
                return ImageVisual(cardImageLoader.frontImage(0,3))
            Tile(mutableListOf(Pair(0,3),Pair(1,4),Pair(2,5),Pair(6,7))) ->
                return ImageVisual(cardImageLoader.frontImage(1,3))
            Tile(mutableListOf(Pair(0,1),Pair(2,5),Pair(3,6),Pair(4,7))) ->
                return ImageVisual(cardImageLoader.frontImage(2,3))
            Tile(mutableListOf(Pair(0,5),Pair(1,6),Pair(2,3),Pair(4,7))) ->
                return ImageVisual(cardImageLoader.frontImage(3,3))
            Tile(mutableListOf(Pair(0,3),Pair(1,2),Pair(4,7),Pair(5,6))) ->
                return ImageVisual(cardImageLoader.frontImage(0,4))
            Tile(mutableListOf(Pair(0,7),Pair(1,6),Pair(2,5),Pair(3,4))) ->
                return ImageVisual(cardImageLoader.frontImage(1,4))
            Tile(mutableListOf(Pair(0,5),Pair(1,4),Pair(2,3),Pair(6,7))) ->
                return ImageVisual(cardImageLoader.frontImage(2,4))
            Tile(mutableListOf(Pair(0,1),Pair(2,7),Pair(3,6),Pair(4,5))) ->
                return ImageVisual(cardImageLoader.frontImage(3,4))
            Tile(mutableListOf(Pair(0,7),Pair(1,2),Pair(3,4),Pair(5,6))) ->
                return ImageVisual(cardImageLoader.frontImage(0,5))
            Tile(mutableListOf(Pair(0,5),Pair(1,4),Pair(2,7),Pair(3,6))) ->
                return ImageVisual(cardImageLoader.frontImage(1,5))
            Tile(mutableListOf(Pair(0,3),Pair(1,6),Pair(2,5),Pair(4,7))) ->
                return ImageVisual(cardImageLoader.frontImage(2,5))
            Tile(mutableListOf(Pair(0,1),Pair(2,3),Pair(4,5),Pair(6,7))) ->
                return ImageVisual(cardImageLoader.frontImage(3,5))
            else -> return tileBackImage
        }
    }

    /**
     * to turn the sound for placing a tile
     */
    override fun refreshAfterTryingPlaceTile() {
        gameService.playNopeSound()
    }



}