package view

import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.components.uicomponents.TextField
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.CompoundVisual
import tools.aqua.bgw.visual.ImageVisual
import tools.aqua.bgw.visual.TextVisual
import java.awt.Color

/**
 * main menu hub displayed after title scene
 *
 * [backToTitleSceneButton]: Button for people who like the title screen
 * [nameFieldLabel], [nameField]: player name input prompt
 * [nameErrorLabel], [closeNameErrorButton]: displays error message
 * [newGameLabel], [joinButton], [hostButton], [hotseatButton]:
 * new Game Buttons with check for a name being input via CCApplication
 * [creditsButton]: transition to Credits Scene
 * [quitButton]: exits the Game
 * [musicToggleButton], [soundToggleButton]: toggles music/sound on off
 */

class MainMenuScene : MenuScene(1920,1080) {

    private val menuLabel = Label(width = 600, height = 200, posX = 650, posY = 10,
        visual = TextVisual(font = Font(size = 200, color = Color.WHITE, family = "Calibri"),
            text = "MENU"))

    val debugGameSceneButton = Button(width = 400, height = 100, posX = 760, posY = 440,
        visual = CompoundVisual(
            ColorVisual.WHITE.apply { transparency = 0.3 },
            TextVisual(font = Font(size = 60, color = Color.RED, family = "Calibri"),
                text = "gameScene")))

    val debugGameEndSceneButton = Button(width = 400, height = 100, posX = 760, posY = 550,
        visual = CompoundVisual(
            ColorVisual.WHITE.apply { transparency = 0.3 },
            TextVisual(font = Font(size = 60, color = Color.RED, family = "Calibri"),
                text = "End GameScene")))


    val backToTitleSceneButton = Button(width = 400, height = 100, posX = 460, posY = 900,
        visual = CompoundVisual(
            ColorVisual.WHITE.apply { transparency = 0.3 },
            TextVisual(font = Font(size = 60, color = Color.RED, family = "Calibri"),
                text = "Back to Title")))

    private val nameFieldLabel = Label(width = 600, height = 100, posX = 200, posY = 250,
        visual = CompoundVisual(
            ColorVisual.WHITE.apply { transparency = 0.1 },
            TextVisual(font = Font(size = 60, color = Color.BLUE, family = "Calibri"),
                text = "Player Name")))

    val nameField = TextField(width = 400, height = 80, posX = 300, posY = 400,
        prompt = "Enter your Name",
        font = Font(size = 40, family = "Calibri"))

    private val nameErrorLabel = Label(width = 1200, height = 600, posX = 360, posY = 150,
        visual = CompoundVisual(
            ColorVisual.WHITE.apply { transparency = 0.8 },
            TextVisual(font = Font(size = 60, color = Color.BLUE, family = "Calibri"), offsetY = -200,
                text = "Enter Name Before Starting Game",))
    ).apply { isDisabled = true; opacity = 0.0; onMouseClicked = { nameErrorClose() } }

    private val closeNameErrorButton = Button(width = 300, height = 100, posX = 810, posY = 450,
        font = Font(size = 40, family = "Calibri"),
        visual = ColorVisual(255,40,40),
        text = "close",
    ).apply { isDisabled = true; opacity = 0.0; onMouseClicked = { nameErrorClose() } }

    private val newGameLabel = Label(width = 600, height = 100, posX = 1100, posY = 250,
        visual = CompoundVisual(
            ColorVisual.WHITE.apply { transparency = 0.1 },
            TextVisual(font = Font(size = 60, color = Color.BLUE, family = "Calibri"),
                text = "Start New Game")))

    val joinButton = Button(width = 400, height = 100, posX = 1200, posY = 400,
        visual = CompoundVisual(
            ColorVisual.WHITE.apply { transparency = 0.3 },
            TextVisual(font = Font(size = 60, color = Color.RED, family = "Calibri"),
                text = "Join Game")))

    val hostButton = Button(width = 400, height = 100, posX = 1200, posY = 550,
        visual = CompoundVisual(
            ColorVisual.WHITE.apply { transparency = 0.3 },
            TextVisual(font = Font(size = 60, color = Color.RED, family = "Calibri"),
                text = "Host Game")))

    val hotseatButton = Button(width = 400, height = 100, posX = 1200, posY = 700,
        visual = CompoundVisual(
            ColorVisual.WHITE.apply { transparency = 0.3 },
            TextVisual(font = Font(size = 60, color = Color.RED, family = "Calibri"),
                text = "Hotseat Mode")))

    val creditsButton = Button(width = 300, height = 100, posX = 80, posY = 900,
        visual = CompoundVisual(
            ColorVisual.WHITE.apply { transparency = 0.3 },
            TextVisual(font = Font(size = 60, color = Color.RED, family = "Calibri"),
                text = "Credits")))

    val quitButton = Button(width = 140, height = 140, posX = 1720, posY = 60,
        visual = ImageVisual("quit_button.png"))

    val musicToggleButton = Button(width = 140, height = 140, posX = 1720, posY = 880,
        visual = ImageVisual("music_enabled.png"))

    val soundToggleButton = Button(width = 140, height = 140, posX = 1520, posY = 880,
        visual = ImageVisual("sound_enabled.png"))

    val fullscreenToggleButton = Button(width = 140, height = 140, posX = 1320, posY = 880,
        visual = ImageVisual("fullscreen.png"))

    init{
        addComponents(
            nameField, nameFieldLabel,
            menuLabel,
            newGameLabel, joinButton, hostButton, hotseatButton,
            creditsButton, backToTitleSceneButton,
            quitButton, soundToggleButton, musicToggleButton, fullscreenToggleButton,
            nameErrorLabel, closeNameErrorButton,
        )
        background = ColorVisual(0,0,0)
        opacity = 0.3
    }

    /**
     * displays name error and disables new game buttons
     */

    fun nameErrorDisplay() {
        nameErrorLabel.opacity = 1.0
        closeNameErrorButton.opacity = 1.0
        nameErrorLabel.isDisabled = false
        closeNameErrorButton.isDisabled = false
        nameField.isDisabled = true
        joinButton.isDisabled = true
        hostButton.isDisabled = true
        hotseatButton.isDisabled = true
    }

    /**
     * closes error message and enables new game buttons again
     */

    private fun nameErrorClose() {
        nameErrorLabel.opacity = 0.0
        closeNameErrorButton.opacity = 0.0
        nameErrorLabel.isDisabled = true
        closeNameErrorButton.isDisabled = true
        nameField.isDisabled = false
        joinButton.isDisabled = false
        hostButton.isDisabled = false
        hotseatButton.isDisabled = false
    }

}