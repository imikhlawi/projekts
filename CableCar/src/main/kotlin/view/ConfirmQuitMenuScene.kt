package view

import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual
import java.awt.Color

/**
 * menu scene that pops up when quit button is pressed to confirm player input. if no is pressed last menuscene is
 * showed and if yes is pressed the application terminates
 */

class ConfirmQuitMenuScene: MenuScene(1920,1080)  {

    private val popUpText = Label(width = 1920, height = 400,  posY = 100,
        font = Font(size = 150, color = Color.RED, family = "Calibri"),
        text = "Are you sure about that?")

    val yesButton = Button(width = 300, height = 100, posX = 300, posY = 600,
        visual = ColorVisual.WHITE.apply { transparency = 0.7 },
        font = Font(size = 60, color = Color.RED, family = "Calibri"),
        text = "Yes")

    val noButton = Button(width = 300, height = 100, posX = 1330, posY = 600,
        visual = ColorVisual.WHITE.apply { transparency = 0.7 },
        font = Font(size = 60, color = Color.GREEN, family = "Calibri"),
        text = "No")

    private val backgroundLabel = Label(width = 1600, height = 900, posX = 160, posY = 90,
        visual = ColorVisual.WHITE).apply { opacity = 0.3 }

    private val theRockLabel = Label(width = 469, height = 469, posX = 725, posY = 450,
        visual = ImageVisual("quit_confirm.png"))

   init {
       opacity = 0.0
       addComponents(backgroundLabel, popUpText, yesButton, noButton, theRockLabel)
   }

}