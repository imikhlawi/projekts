package view

import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.CheckBox
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
 * menu scene to let player configure their hosted network game before starting
 */

class HostLobbyScene: MenuScene(1920, 1080) {

    private val hostGameLabel = Label(width = 960, height = 200, posY = 40,
        visual = TextVisual(font = Font(size = 100, color = Color.WHITE, family = "Calibri"),
            text = "HOST YOUR GAME"))

    val backToMainMenuSceneButton = Button(width = 600, height = 100, posX = 100, posY = 880,
        visual = CompoundVisual(
            ColorVisual.WHITE.apply { transparency = 0.3 },
            TextVisual(font = Font(size = 60, color = Color.RED, family = "Calibri"), text = "Back to Main Menu")))

    //Music / sound
    val musicToggleButton = Button(width = 140, height = 140, posX = 1520, posY = 60,
        visual = ImageVisual("music_enabled.png"))

    val soundToggleButton = Button(width = 140, height = 140, posX = 1320, posY = 60,
        visual = ImageVisual("sound_enabled.png"))

    val fullscreenToggleButton = Button(width = 140, height = 140, posX = 1120, posY = 60,
        visual = ImageVisual("fullscreen.png"))

   //KI Turnier
    private val allowKITurnier = Button(width = 600, height = 100, posX = 100, posY = 400,
        visual = CompoundVisual(
            ColorVisual.WHITE.apply { transparency = 0.3 },
            TextVisual(font = Font(size = 60, color = Color.RED, family = "Calibri"), text = "AI Tournament Mode"))
    ).apply { onMouseClicked = { allowKITurnierCheckbox.isChecked = !allowKITurnierCheckbox.isChecked } }

    val allowKITurnierCheckbox = CheckBox(posX = 730, posY = 433)

    //Shuffle Player
    /*private val allowShufflePlayerOrderButton = Button(width = 600, height = 100, posX = 100, posY = 450,
        visual = CompoundVisual(
            ColorVisual.WHITE.apply { transparency = 0.3 },
            TextVisual(font = Font(size = 60, color = Color.RED, family = "Calibri"), text = "Shuffle Player Order"))
    ).apply { onMouseClicked = { allowShufflePlayerOrderCheckbox.isChecked = !allowShufflePlayerOrderCheckbox.isChecked } }

    val allowShufflePlayerOrderCheckbox = CheckBox(posX = 730, posY = 483)*/

    //Tile Rotation

    private val allowTileRotationButton = Button(width = 600, height = 100, posX = 100, posY = 550,
        visual = CompoundVisual(
            ColorVisual.WHITE.apply { transparency = 0.3 },
            TextVisual(font = Font(size = 60, color = Color.RED, family = "Calibri"), text = "Tile Rotation"))
    ).apply { onMouseClicked = { allowTileRotationCheckbox.isChecked = !allowTileRotationCheckbox.isChecked } }

    val allowTileRotationCheckbox = CheckBox(posX = 730, posY = 583).apply { isChecked = true }

    //Session ID
    private val sessionIdLabel = Label(width = 300, height = 100, posX = 1180, posY = 450,
        visual = CompoundVisual(
            ColorVisual.WHITE.apply { transparency = 0.1 },
            TextVisual(font = Font(size = 60, color = Color.BLUE, family = "Calibri"), text = "SessionID")))

    val sessionIdTextField = TextField(width = 300, height = 80, posX = 1180, posY = 600,
        prompt = "Choose SessionID", font = Font(size = 36, family = "Calibri")
    ).apply { onKeyTyped = { parametersInput() } }

    // Secret
    /*private val secretLabel = Label(width = 300, height = 100, posX = 1450, posY = 400,
        visual = CompoundVisual(
            ColorVisual.WHITE.apply { transparency = 0.1 },
            TextVisual(font = Font(size = 60, color = Color.BLUE, family = "Calibri"), text = "Secret")))

    val secretTextField = TextField(width = 300, height = 80, posX = 1450, posY = 550,
        prompt = "Choose Secret", font = Font(size = 40, family = "Calibri")
    ).apply { onKeyTyped = { parametersInput() } }*/

    val quitButton = Button(width = 140, height = 140, posX = 1720, posY = 60,
        visual = ImageVisual("quit_button.png"))

    val hostGameButton = Button(width = 400, height = 100, posX = 1240, posY = 800,
        visual = CompoundVisual(
            ColorVisual.WHITE.apply { transparency = 0.3 },
            TextVisual(font = Font(size = 60, color = Color.RED, family = "Calibri"), text = "Host"))
    ).apply { isDisabled = true; opacity = 0.0; onMouseClicked = { isDisabled = true; opacity = 0.0 } }

    init{
        addComponents(
            backToMainMenuSceneButton,
            soundToggleButton, musicToggleButton, fullscreenToggleButton,
            hostGameLabel,allowKITurnier,allowKITurnierCheckbox,
            allowTileRotationButton,allowTileRotationCheckbox,
            sessionIdLabel,sessionIdTextField,
            quitButton,hostGameButton
        )
        background = ColorVisual(0,0,0)
        opacity = 0.3
    }

    private fun parametersInput() {
        if (hostGameButton.isDisabled &&
            sessionIdTextField.text != "") {
            hostGameButton.isDisabled = false; hostGameButton.opacity = 1.0
        }
    }

}