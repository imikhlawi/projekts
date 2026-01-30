package view

import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.CompoundVisual
import tools.aqua.bgw.visual.ImageVisual
import tools.aqua.bgw.visual.TextVisual
import java.awt.Color

/**
 * menu scene accessible from game scene for quick settings and quitting
 */

class QuickMenuGameScene : MenuScene(600, 1080) {

    private val menuLabel = Label(width = 600, height = 100, posX = 0, posY = 140,
        font = Font(size = 100, color = Color.BLUE, family = "Calibri"),
        text = "Quick Menu")

    val fullscreenToggleButton = Button(width = 140, height = 140, posX = 230, posY = 18,
        visual = ImageVisual("fullscreen.png"))

    private val soundToggleLabel = Label(width = 600, height = 100, posX = 0, posY = 300,
        visual = CompoundVisual(
            ColorVisual.WHITE.apply { transparency = 0.5 },
            TextVisual(
                font = Font(size = 55, color = Color.BLUE, family = "Calibri", fontStyle = Font.FontStyle.ITALIC),
                text = "Sound on/off")))

    val soundToggleButton = Button(width = 140, height = 140, posX = 230, posY = 400,
        visual = ImageVisual("sound_enabled.png"))

    private val musicToggleLabel = Label(width = 600, height = 100, posX = 0, posY = 600,
        visual = CompoundVisual(
            ColorVisual.WHITE.apply { transparency = 0.5 },
            TextVisual(
                font = Font(size = 55, color = Color.BLUE, family = "Calibri", fontStyle = Font.FontStyle.ITALIC),
                text = "Music on/off")))

    val musicToggleButton = Button(width = 140, height = 140, posX = 230, posY = 700,
        visual = ImageVisual("music_enabled.png") )

    val exitQuitMenuSceneButton = Button(width = 80, height = 80, posX = 470, posY = 50,
        visual = ImageVisual("Quit.png"))

    val quitButton = Button(width = 400, height = 160, posX = 100, posY = 880,
        visual = CompoundVisual(
            ColorVisual(255,192,192),
            TextVisual(
                font = Font(size = 80, color = Color.RED, family = "Calibri", fontStyle = Font.FontStyle.ITALIC),
                text = "Ragequit")))

    init {
        addComponents(
            menuLabel,
            soundToggleLabel, soundToggleButton, musicToggleLabel, musicToggleButton, fullscreenToggleButton,
            exitQuitMenuSceneButton, quitButton
        )
        background = ColorVisual(128,255,128)
        opacity = 0.4
    }

}