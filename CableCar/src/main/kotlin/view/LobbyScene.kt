package view

import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.CheckBox
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.components.uicomponents.TextField
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.CompoundVisual
import tools.aqua.bgw.visual.ImageVisual
import tools.aqua.bgw.visual.TextVisual
import java.awt.Color

/**
 * shows LobbyScene when clicked on Hot Seat Mode (Main Menu Scene)
 *
 * [playersJoined]: counter representing amount of players in Table
 * [realAISelection]: stores player input in addPlayer dialogue
 * [backToMainMenuSceneButton]: button to return to the main Menu
 * [shuffleTurnOrderButton], [shuffleTurnOrderCheckbox]: Checkbox for order shuffling option
 * [allowTileRotationButton], [allowTileRotationCheckbox]: Checkbox for tile rotation option
 * [playerLabel],[playerBoxLabel]: Table showing player Names on the left
 * [deletePlayerButtons]: deletes a player entry from the player table
 * [playerColorLabel]:  Color square appears in player Table to display picked color for player
 * [addPlayerButton]: Button for addPlayer dialogue to start. only appears if players < 6
 * [nameFields]: appears in corresponding line in player table for input in addPlayer dialogue
 * [aiPlayerButton], [aiPlayerButtonTrigger]: highlighted Button; if selected smartAI Checkbox appears
 * [hotseatPlayerButton], [hotseatPlayerButtonTrigger]: highlighted Button
 * [smartAI], [smartAICheckbox]: Checkbox for big smart AI option
 * [confirmButton]: Button to appear in addPlayer dialogue when necessary options are input
 * [startGameButton]: starts a game when all criteria are met
 * [quitButton]: exits the Game
 * [musicToggleButton], [soundToggleButton]: toggles music/sound on off
 */

class LobbyScene : MenuScene(1920, 1080) {

    private var playersJoined = 0

    private var realAISelection: Int? = null

    var playerType = mutableListOf(0,0,0,0,0,0)

    val backToMainMenuSceneButton = Button(width = 600, height = 100, posX = 100, posY = 900,
        visual = CompoundVisual(
            ColorVisual.WHITE.apply { transparency = 0.3 },
            TextVisual(font = Font(size = 60, color = Color.RED, family = "Calibri"), text = "Back to Main Menu")))

    private val shuffleTurnOrderButton = Button(width = 600, height = 100, posX = 1120, posY = 250,
        visual = CompoundVisual(
            ColorVisual.WHITE.apply { transparency = 0.3 },
            TextVisual(font = Font(size = 60, color = Color.RED, family = "Calibri"), text = "Shuffle Turn Order"))
    ).apply { onMouseClicked = { shuffleTurnOrderCheckbox.isChecked = !shuffleTurnOrderCheckbox.isChecked } }

    val shuffleTurnOrderCheckbox = CheckBox(1750, 283)

    private val allowTileRotationButton = Button(width = 600, height = 100, posX = 1120, posY = 380,
        visual = CompoundVisual(
            ColorVisual.WHITE.apply { transparency = 0.3 },
            TextVisual(font = Font(size = 60, color = Color.RED, family = "Calibri"), text = "Allow Tile Rotation"))
    ).apply { onMouseClicked = { allowTileRotationCheckbox.isChecked = !allowTileRotationCheckbox.isChecked } }

    val allowTileRotationCheckbox = CheckBox(1750, 413).apply { isChecked = true }

    private val playerLabel = Label(width = 300, height = 100, posX = 100, posY = 100,
        visual = CompoundVisual(
            ColorVisual.WHITE.apply { transparency = 0.1 },
            TextVisual(font = Font(size = 60, color = Color.BLUE, family = "Calibri"), text = "Players")))

    val playerBoxLabel = mutableListOf(
        Label(width = 800, height = 80, posX = 100, posY = 250, visual = CompoundVisual(
            ColorVisual(63, 255, 63).apply { transparency = 0.3 },
            TextVisual(font = Font(size = 60, color = Color.BLACK, family = "Calibri"),
                text = "", alignment = Alignment.CENTER_LEFT, offsetX = 20))),
        Label(width = 800, height = 80, posX = 100, posY = 350, visual = CompoundVisual(
            ColorVisual.WHITE.apply { transparency = 0.3 },
            TextVisual(font = Font(size = 60, color = Color.BLACK, family = "Calibri"),
                text = "", alignment = Alignment.CENTER_LEFT, offsetX = 20))),
        Label(width = 800, height = 80, posX = 100, posY = 450, visual = CompoundVisual(
            ColorVisual.WHITE.apply { transparency = 0.3 },
            TextVisual(font = Font(size = 60, color = Color.BLACK, family = "Calibri"),
                text = "", alignment = Alignment.CENTER_LEFT, offsetX = 20))),
        Label(width = 800, height = 80, posX = 100, posY = 550, visual = CompoundVisual(
            ColorVisual.WHITE.apply { transparency = 0.3 },
            TextVisual(font = Font(size = 60, color = Color.BLACK, family = "Calibri"),
                text = "", alignment = Alignment.CENTER_LEFT, offsetX = 20))),
        Label(width = 800, height = 80, posX = 100, posY = 650, visual = CompoundVisual(
            ColorVisual.WHITE.apply { transparency = 0.3 },
            TextVisual(font = Font(size = 60, color = Color.BLACK, family = "Calibri"),
                text = "", alignment = Alignment.CENTER_LEFT, offsetX = 20))),
        Label(width = 800, height = 80, posX = 100, posY = 750, visual = CompoundVisual(
            ColorVisual.WHITE.apply { transparency = 0.3 },
            TextVisual(font = Font(size = 60, color = Color.BLACK, family = "Calibri"),
                text = "", alignment = Alignment.CENTER_LEFT, offsetX = 20))))

    private val deletePlayerButtons = listOf(
        Button(width = 80, height = 80, posX = 800.0, posY = 350.0,
            visual = TextVisual(text = "-", font = Font(size = 50, family = "Calibri"))
        ).apply { isDisabled = true; opacity = 0.0; onMouseClicked = { deletePlayer(1) } },
        Button(width = 80, height = 80, posX = 800.0, posY = 450.0,
            visual = TextVisual(text = "-", font = Font(size = 50, family = "Calibri"))
        ).apply { isDisabled = true; opacity = 0.0; onMouseClicked = { deletePlayer(2) } },
        Button(width = 80, height = 80, posX = 800.0, posY = 550.0,
            visual = TextVisual(text = "-", font = Font(size = 50, family = "Calibri"))
        ).apply { isDisabled = true; opacity = 0.0; onMouseClicked = { deletePlayer(3) } },
        Button(width = 80, height = 80, posX = 800.0, posY = 650.0,
            visual = TextVisual(text = "-", font = Font(size = 50, family = "Calibri"))
        ).apply { isDisabled = true; opacity = 0.0; onMouseClicked = { deletePlayer(4) } },
        Button(width = 80, height = 80, posX = 800.0, posY = 750.0,
            visual = TextVisual(text = "-", font = Font(size = 50, family = "Calibri"))
        ).apply { isDisabled = true; opacity = 0.0; onMouseClicked = { deletePlayer(5) } })

    private val playerColorLabel = mutableListOf(
        Label(width = 80, height = 80, posX = 700.0, posY = 250, visual = ColorVisual.YELLOW),
        Label(width = 80, height = 80, posX = 700.0, posY = 350, visual = ColorVisual.BLUE),
        Label(width = 80, height = 80, posX = 700.0, posY = 450, visual = ColorVisual.ORANGE),
        Label(width = 80, height = 80, posX = 700.0, posY = 550, visual = ColorVisual.GREEN),
        Label(width = 80, height = 80, posX = 700.0, posY = 650, visual = ColorVisual(100,0,100)),
        Label(width = 80, height = 80, posX = 700.0, posY = 750, visual = ColorVisual.BLACK))

    private val addPlayerButton = Button(width = 80, height = 80, posX = 120.0,
        visual = TextVisual(font = Font(size = 50, family = "Calibri"), text = "+")
    ).apply { onMouseClicked = { addPlayer() } }

    val nameFields = mutableListOf(
        TextField(width = 280, height = 80, posX = 100, posY = 350, prompt = "Enter Name",
            font = Font(size = 40, family = "Calibri"),
        ).apply { onKeyTyped = { playerConfigured() } },
        TextField(width = 280, height = 80, posX = 100, posY = 450, prompt = "Enter Name",
            font = Font(size = 40, family = "Calibri"),
        ).apply { onKeyTyped = { playerConfigured() } },
        TextField(width = 280, height = 80, posX = 100, posY = 550, prompt = "Enter Name",
            font = Font(size = 40, family = "Calibri"),
        ).apply { onKeyTyped = { playerConfigured() } },
        TextField(width = 280, height = 80, posX = 100, posY = 650, prompt = "Enter Name",
            font = Font(size = 40, family = "Calibri"),
        ).apply { onKeyTyped = { playerConfigured() } },
        TextField(width = 280, height = 80, posX = 100, posY = 750, prompt = "Enter Name",
            font = Font(size = 40, family = "Calibri"),
        ).apply { onKeyTyped = { playerConfigured() }})

    private val aiPlayerButton = Button(width = 270, height = 100, posX = 1120, posY = 550,
        visual = CompoundVisual(
            ColorVisual.WHITE.apply { transparency = 0.3 },
            TextVisual(font = Font(size = 60, color = Color.RED, family = "Calibri"), text = "AI")))

    private val aiPlayerButtonTrigger = Label(width = 270, height = 100, posX = 1120, posY = 550,
        visual = ColorVisual(63, 255, 63)
    ).apply {
        opacity = 0.0
        onMouseEntered = { if (realAISelection == null) opacity = 0.5 }
        onMouseExited = { if (realAISelection == null) opacity = 0.0 }
        onMouseClicked = { realAISelection = 0; realAISelected() }}

    private val hotseatPlayerButton = Button(width = 270, height = 100, posX = 1470, posY = 550,
        visual = CompoundVisual(
            ColorVisual.WHITE.apply { transparency = 0.3 },
            TextVisual(font = Font(size = 60, color = Color.RED, family = "Calibri"), text = "real")))

    private val hotseatPlayerButtonTrigger = Label(width = 270, height = 100, posX = 1470, posY = 550,
        visual = ColorVisual(63, 255, 63)
    ).apply {
        opacity = 0.0
        onMouseEntered = { if (realAISelection == null) opacity = 0.5 }
        onMouseExited = { if (realAISelection == null) opacity = 0.0 }
        onMouseClicked = { realAISelection = 1; realAISelected() }}

    private val smartAI = Button(width = 270, height = 100, posX = 1120, posY = 680,
        visual = CompoundVisual(
            ColorVisual.WHITE.apply { transparency = 0.3 },
            TextVisual(font = Font(size = 60, color = Color.RED, family = "Calibri"), text = "big smart"))
    ).apply {
        isDisabled = true; opacity = 0.0
        onMouseClicked = { smartAICheckbox.isChecked = !smartAICheckbox.isChecked }}

    private val smartAICheckbox = CheckBox(1420, 713).apply { isDisabled = true; opacity = 0.0 }

    private val confirmButton = Button(width = 400, height = 100, posX = 1120, posY = 850,
        visual = CompoundVisual(
            ColorVisual.WHITE.apply { transparency = 0.3 },
            TextVisual(font = Font(size = 60, color = Color.RED, family = "Calibri"), text = "Confirm"))
    ).apply { isDisabled = true; opacity = 0.0; onMouseClicked = { playerAddFinished() } }

    private val startGameButton = Button(width = 300, height = 100, posX = 800 , posY = 900,
        visual = CompoundVisual(
            ColorVisual.WHITE.apply { transparency = 0.3 },
            TextVisual(font = Font(size = 60, color = Color.RED, family = "Calibri"), text = "Start Game"))
    ).apply {
        isDisabled = true; opacity = 0.0
        onMouseClicked = {
            addComponents(isHostRealAIClickDisableLabel, isHostRealAIBG, hostRealButton, hostSmartButton, hostDumbButton)
            isHostRealAIClickDisableLabel.isDisabled = false; isHostRealAIClickDisableLabel.opacity = 1.0
            isHostRealAIBG.isDisabled = false; isHostRealAIBG.opacity = 1.0
            hostRealButton.isDisabled = false; hostRealButton.opacity = 1.0
            hostSmartButton.isDisabled = false; hostSmartButton.opacity = 1.0
            hostDumbButton.isDisabled = false; hostDumbButton.opacity = 1.0
        }
    }

    val isHostRealAIClickDisableLabel = Label(width = 1920, height = 1080, visual = ColorVisual(0,0,0,0)
    ).apply { isDisabled = true; opacity = 0.0 }

    val isHostRealAIBG = Label(width = 1920, height = 500, posY = 290, visual = CompoundVisual(
        ColorVisual(160,160,160).apply { transparency = 0.7 },
        TextVisual(font = Font(size = 60, color = Color.BLACK, family = "Calibri"), text = "Is Host (1st Player) AI?",
            offsetY = - 180))
    ).apply { isDisabled = true; opacity = 0.0 }

    val hostRealButton = Button(width = 300, height = 100, posX = 510, posY = 600, visual = CompoundVisual(
        ColorVisual.WHITE.apply { transparency = 0.3 },
        TextVisual(font = Font(size = 60, color = Color.RED, family = "Calibri"), text = "Real"))
    ).apply { isDisabled = true; opacity = 0.0 }

    val hostDumbButton = Button(width = 300, height = 100, posX = 810, posY = 600, visual = CompoundVisual(
        ColorVisual.WHITE.apply { transparency = 0.3 },
        TextVisual(font = Font(size = 60, color = Color.RED, family = "Calibri"), text = "Dumb AI"))
    ).apply { isDisabled = true; opacity = 0.0 }

    val hostSmartButton = Button(width = 300, height = 100, posX = 1110, posY = 600, visual = CompoundVisual(
        ColorVisual.WHITE.apply { transparency = 0.3 },
        TextVisual(font = Font(size = 60, color = Color.RED, family = "Calibri"), text = "Smart AI"))
    ).apply { isDisabled = true; opacity = 0.0 }

    val quitButton = Button(width = 140, height = 140, posX = 1720, posY = 60,
        visual = ImageVisual("quit_button.png"))

    val musicToggleButton = Button(width = 140, height = 140, posX = 1520, posY = 60,
        visual = ImageVisual("music_enabled.png"))

    val soundToggleButton = Button(width = 140, height = 140, posX = 1320, posY = 60,
        visual = ImageVisual("sound_enabled.png"))

    val fullscreenToggleButton = Button(width = 140, height = 140, posX = 1120, posY = 60,
        visual = ImageVisual("fullscreen.png"))

    init {

        addComponents(
            backToMainMenuSceneButton, playerLabel, quitButton, musicToggleButton, soundToggleButton,
            fullscreenToggleButton,
            playerBoxLabel[0], playerBoxLabel[1], playerBoxLabel[2],
            playerBoxLabel[3], playerBoxLabel[4], playerBoxLabel[5],
            shuffleTurnOrderButton, shuffleTurnOrderCheckbox, allowTileRotationButton, allowTileRotationCheckbox,
            deletePlayerButtons[0],deletePlayerButtons[1],deletePlayerButtons[2],
            deletePlayerButtons[3],deletePlayerButtons[4],
            startGameButton,

        )

        background = ColorVisual(0,0,0)
        opacity = 0.3
        ready()

    }

    /**
     * stores color selection after removing selection buttons and displays it in player table
     */

    private fun ready() {

        addComponents(playerColorLabel[playersJoined])

        playersJoined++
        //println("now there are $playersJoined Players in the lobby")

        if (playersJoined > 1) {
            deletePlayerButtons[playersJoined-2].isDisabled = false
            deletePlayerButtons[playersJoined-2].opacity = 1.0
            startGameButton.isDisabled = false
            startGameButton.opacity = 1.0
        }

        if (playersJoined < 6) {
            addComponents(addPlayerButton)
            addPlayerButton.apply { posY = 250.0 + 100.0 * playersJoined }
        }
    }

    /**
     * addPlayer dialogue start
     */

    private fun addPlayer() {
        addComponents(
            aiPlayerButton, aiPlayerButtonTrigger, hotseatPlayerButton, hotseatPlayerButtonTrigger,
            smartAI, smartAICheckbox,
            nameFields[playersJoined - 1],
            confirmButton
        )

        removeComponents(addPlayerButton)
        startGameButton.isDisabled = true; startGameButton.opacity = 0.0
        confirmButton.isDisabled = true; confirmButton.opacity = 0.0
        if (playersJoined > 1) {
            deletePlayerButtons[playersJoined-2].isDisabled = true; deletePlayerButtons[playersJoined-2].opacity = 0.0
        }
    }

    /**
     * method for highlighting real/AI Buttons and (dis)appearing smartAI Checkbox
     */

    private fun realAISelected() {
        playerConfigured()
        if (realAISelection == 0) {
            hotseatPlayerButtonTrigger.opacity = 0.0
            aiPlayerButtonTrigger.opacity = 0.5
            if (smartAI.isDisabled) {
                smartAI.isDisabled = false; smartAI.opacity = 1.0
                smartAICheckbox.isDisabled = false; smartAICheckbox.opacity = 1.0
            }
        } else {
            hotseatPlayerButtonTrigger.opacity = 0.5
            aiPlayerButtonTrigger.opacity = 0.0
            if (!smartAI.isDisabled) {
                smartAI.isDisabled = true; smartAI.opacity = 0.0
                smartAICheckbox.isDisabled = true; smartAICheckbox.opacity = 0.0
            }
        }
    }

    /**
     * necessary conditions check for confirmButton to appear
     */

    private fun playerConfigured() {
        if (confirmButton.isDisabled &&
            nameFields[playersJoined - 1].text != "" && realAISelection != null) {
            confirmButton.isDisabled = false; confirmButton.opacity = 1.0
        }
    }

    /**
     * after confirmButton clicked: input name with player type appended display in player table with green entry.
     * reset and removing components for addPlayer dialogue
     * call of colorPicker to finish add/configure Player dialogue loop
     */

    private fun playerAddFinished() {
        if (nameFields[playersJoined - 1].text != "") {
            playerBoxLabel[playersJoined].visual = CompoundVisual(
                ColorVisual(63, 255, 63).apply { transparency = 0.3 },
                TextVisual(font = Font(size = 60, color = Color.BLACK, family = "Calibri"),
                    text = (nameFields[playersJoined - 1].text +
                            if (realAISelection == 0)
                                if (smartAICheckbox.isChecked) " (AI)"
                                else " (dumb)"
                            else ""),
                    alignment = Alignment.CENTER_LEFT, offsetX = 20))

            aiPlayerButtonTrigger.opacity = 0.0; hotseatPlayerButtonTrigger.opacity = 0.0
            smartAICheckbox.opacity = 0.0; smartAICheckbox.isDisabled = true
            smartAI.opacity = 0.0; smartAI.isDisabled = true
            playerType[playersJoined] = if (realAISelection == 0) if (smartAICheckbox.isChecked) 2 else 1 else 0
            realAISelection = null
            smartAICheckbox.isChecked = false
            removeComponents(
                aiPlayerButton, aiPlayerButtonTrigger, hotseatPlayerButton, hotseatPlayerButtonTrigger,
                smartAI, smartAICheckbox,
                nameFields[playersJoined - 1],
                confirmButton
            )
            ready()
        } else {
            confirmButton.isDisabled = true; confirmButton.opacity = 0.0
        }

    }

    /**
     * clears playerName from the Table containing players Name
     */

    private fun deletePlayer(delPos : Int) {

        if (playersJoined > 1) {

            playerBoxLabel[playersJoined - 1].visual = CompoundVisual(
                ColorVisual.WHITE.apply { transparency = 0.3 },
                TextVisual(font = Font(size = 60, color = Color.BLACK, family = "Calibri"), text = ""))

            nameFields[delPos-1].text = ""
            playerType[delPos] = 0

            removeComponents(nameFields[delPos - 1], playerColorLabel[delPos])

            deletePlayerButtons[delPos - 1].isDisabled = true; deletePlayerButtons[delPos - 1].opacity = 0.0

            playersJoined--

            addPlayerButton.apply { posY = playerBoxLabel[playersJoined].posY }
            if (playersJoined > 1) {
                deletePlayerButtons[delPos-2].isDisabled = false; deletePlayerButtons[delPos-2].opacity = 1.0 }

            println("players joined after deletion $playersJoined")

            if (playersJoined == 5) {
                addComponents(addPlayerButton)
                addPlayerButton.apply { posY = 250.0 + 100.0 * playersJoined+1 }
            }

            if (playersJoined == 1) { startGameButton.isDisabled = true; startGameButton.opacity = 0.0 }

        }
    }

}
