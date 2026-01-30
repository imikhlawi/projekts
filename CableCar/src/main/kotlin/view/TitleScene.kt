package view

import tools.aqua.bgw.animation.DelayAnimation
import tools.aqua.bgw.animation.FadeAnimation
import tools.aqua.bgw.animation.SequentialAnimation
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
 * Scene displayed as first in the game. nice animation to play in background of main menu/lobby Scene
 *
 * [trigger]: trigger to start fadeIn()
 * [gameLabel]: game Name
 * [pressAnyKeyLabel]: fade up and down Animation
 * [toMenuButton]: click anywhere or press any key to get to main menu
 */

class TitleScene : BoardGameScene(1920, 1080) {

    val trigger = Button(width = 1920, height = 1080, posX = 0, posY = 0,
        visual = ColorVisual(0,0,0))

    val gameLabel = Label(width = 1860, height = 441, posX = 30, posY = 100,
        /*font = Font(size = 300, color = Color.PINK, family = "Comic Sans MS"), text = "Carbel Car",*/
        visual = ImageVisual("game_banner.png")
    ).apply { opacity = 0.0 }

    private val pressAnyKeyLabel = Label(width = 600, height = 100, posX = 660, posY = 800,
        visual = CompoundVisual(
            ColorVisual.WHITE.apply { transparency = 0.3 },
            TextVisual(
                font = Font(size = 60, color = Color.RED, family = "Calibri", fontStyle = Font.FontStyle.ITALIC),
                text = "press any key..."
            ))).apply { opacity = 0.0 }

    val toMenuButton = Button(width = 1920, height = 1080).apply { opacity = 0.0 }.apply { isDisabled = true }

    init {
        background = ColorVisual(0, 0, 0)
        addComponents(gameLabel, pressAnyKeyLabel, toMenuButton, trigger)
    }

    /**
     * fades from black and starts fade in of elements
     */

    fun fadeIn() {
        backgroundHueShiftAnimation()
        playAnimation(
            FadeAnimation(trigger,1.0,0.0,1000).apply { onFinished = {
                trigger.isDisabled = true
                toMenuButton.isDisabled = false
                playAnimation(FadeAnimation(gameLabel,0.0,1.0,1000))
                pressAnyKeyLabelFadeAnimation()
            }}
        )
    }

    /**
     * hue Shift Animation by replacing scene background color visual over time
     */

    private fun backgroundHueShiftAnimation() {
        playAnimation(
            SequentialAnimation(
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(255,0,0) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(255,15,0) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(255,31,0) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(255,47,0) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(255,63,0) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(255,79,0) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(255,95,0) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(255,111,0) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(255,127,0) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(255,143,0) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(255,159,0) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(255,175,0) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(255,191,0) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(255,207,0) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(255,223,0) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(255,239,0) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(255,255,0) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(239,255,0) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(223,255,0) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(207,255,0) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(191,255,0) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(175,255,0) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(159,255,0) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(143,255,0) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(127,255,0) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(111,255,0) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(95,255,0) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(79,255,0) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(63,255,0) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(47,255,0) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(31,255,0) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(15,255,0) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(0,255,0) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(0,255,15) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(0,255,31) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(0,255,47) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(0,255,63) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(0,255,79) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(0,255,95) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(0,255,111) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(0,255,127) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(0,255,143) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(0,255,159) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(0,255,175) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(0,255,191) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(0,255,207) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(0,255,223) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(0,255,239) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(0,255,255) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(0,239,155) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(0,223,255) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(0,207,255) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(0,191,255) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(0,175,255) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(0,159,255) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(0,143,255) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(0,127,255) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(0,111,255) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(0,95,255) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(0,79,255) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(0,63,255) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(0,47,255) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(0,31,255) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(0,0,255) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(15,0,255) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(31,0,255) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(47,0,255) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(63,0,255) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(79,0,255) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(95,0,255) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(111,0,255) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(127,0,255) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(143,0,255) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(159,0,255) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(175,0,255) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(191,0,255) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(207,0,255) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(223,0,255) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(239,0,255) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(255,0,255) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(255,0,239) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(255,0,223) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(255,0,207) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(255,0,191) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(255,0,175) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(255,0,159) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(255,0,143) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(255,0,127) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(255,0,111) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(255,0,95) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(255,0,79) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(255,0,63) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(255,0,47) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(255,0,31) } },
                DelayAnimation(duration = 16).apply { onFinished = { background = ColorVisual(255,0,15) } }
            ).apply { onFinished = {backgroundHueShiftAnimation()}}
        )
    }

    /**
     * looping fade in and out animation for [pressAnyKeyLabel]
     */

    private fun pressAnyKeyLabelFadeAnimation() {
        playAnimation(
            SequentialAnimation(
                FadeAnimation(pressAnyKeyLabel, 0.0, 1.0,1000),
                FadeAnimation(pressAnyKeyLabel, 1.0, 0.0,1000)
            ).apply { onFinished = {pressAnyKeyLabelFadeAnimation()} }
        )
    }

}