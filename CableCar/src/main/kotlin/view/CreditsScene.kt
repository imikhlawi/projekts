package view

import tools.aqua.bgw.animation.DelayAnimation
import tools.aqua.bgw.animation.FadeAnimation
import tools.aqua.bgw.animation.MovementAnimation
import tools.aqua.bgw.animation.SequentialAnimation
import tools.aqua.bgw.components.ComponentView
import tools.aqua.bgw.components.gamecomponentviews.TokenView
import tools.aqua.bgw.components.layoutviews.Pane
import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.*
import java.awt.Color

/**
 */

class CreditsScene : BoardGameScene(1920,1080) {

    private val creditsLabel = Label( visual = ImageVisual("game_banner.png"), width = 1920, height = 455, posY = 312)

    private val memberNames = listOf(
        "Anastasiia Korzhylova", "Mohamed Aziz Feriani", "Leonard Sander", "Ashma Poudyal", "Hang Nguyen Nguyen",
        "Jonah Hadt", "Tobias Grabert", "Altayebabdalsalam I. M. Ikhlawi", "Till Schallau")

    private val memberJobs = listOf(
        "KI, Projektleitung", "KI, Dokumentation", "GUI, Code-Qualität", "GUI, Code-Qualität", "GUI, Dokumentation",
        "Service, Issues", "Service, unser Mann in der Task Force", "Service, Tests", "Gruppenleiter"
    )

    private val memberQuotes = listOf(
        "\"Karriereziel: mind. die Hälfte\ndavon verdienen,was Netflix\ndieser Flugbegleiterin zahlt\"","",
        "\"Alles hat ein Ende.\nNur die Wurst hat 2.\"","\"Now that my Code runs,\nI don't need Therapy anymore.\"","",
        "\"Microsoft ist nicht schlecht,\nsie machen nur ein beschissenes OS.\"",
        "\"Nur noch ein Tag,\ndann ist morgen.\"","","\"Das Sopra ist voll cool\""
    )


    private val anastasiiaImage = TokenView(width = 300, height = 330.5 , posX = 1500, posY = 1600,
        visual = ImageVisual("anastasiia_credits.jpeg"))

    private val ikhlawiImage = TokenView(width = 300, height = 246, posX = 400, posY = 7000,
        visual = ImageVisual("ikhlawi_credits.jpg"))

    private val memberImages = listOf(
        ImageVisual("_DSC0521_.jpg"), ImageVisual("_DSC0624_.jpg"), ImageVisual("_DSC0587_.jpg"),
        ImageVisual("_DSC0579-gigapixel-standard-scale-2_00x__.jpg"), ImageVisual("_DSC0540_.jpg"),
        ImageVisual("_DSC0504_.jpg"), ImageVisual("_DSC0603_.jpg"),
        ImageVisual("_DSC0527-gigapixel-standard-scale-2_00x_.jpg"), ImageVisual("_DSC0616_.jpg"))

    private val memberImageTokenViews = mutableListOf<TokenView>()

    private val movePane = Pane<ComponentView>(width = 1920, height = 1080)

    val backToTitleSceneButton = Button(width = 400, height = 100, posX = 100, posY = 930,
        visual = CompoundVisual(
            ColorVisual.WHITE.apply { transparency = 0.3 },
            TextVisual(font = Font(size = 60, color = Color.RED, family = "Calibri"), text = "Back to Title")))

    var timesClicked : Int = 0

    val musicToggleButton = Button(width = 140, height = 140, posX = 1720, posY = 880,
        visual = ImageVisual("music_enabled.png"))

    val soundToggleButton = Button(width = 140, height = 140, posX = 1520, posY = 880,
        visual = ImageVisual("sound_enabled.png"))

    val fullscreenToggleButton = Button(width = 140, height = 140, posX = 1320, posY = 880,
        visual = ImageVisual("fullscreen.png"))

    private val nameFont = Font(size = 60, color = Color.WHITE, family = "Calibri")
    private val jobFont = Font(size = 40, color = Color.WHITE, family = "Calibri")
    private val quoteFont = Font(size = 50, color = Color.WHITE, family = "Calibri", fontStyle = Font.FontStyle.ITALIC)

    val moveAnimation = MovementAnimation(componentView = movePane, byY = -8800, duration = 80000)

    private val explosionImageVisualList = pathToImageVisuals("explosion_png/",112)
    private var i = 0
    val tk = TokenView(visual = explosionImageVisualList[i],width = 1158, height = 1080, posX = 381, posY = 0)
    val explosionAnimation = SequentialAnimation(
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }},
        DelayAnimation(duration = 17).apply { onFinished = {
            i += 1; tk.visual = explosionImageVisualList[i]; println("frame$i") }}
    )

    private val copyrightLabel = Label(text = "Falls du nen Kartoffel PC hast,\ngehst du jetzt besser." +
            "\n\n\nCopyright 2023", font = Font(size = 50, color = Color.WHITE, family = "Calibri"),
        width = 1920, height = 600, posY = 9050)

    init {


        for(i in 0..8) {
            if (i%2 == 0) {
                memberImageTokenViews += TokenView(visual = memberImages[i], width = 1000, height = 750,
                    posX = 40, posY = 1120+800*i).apply { opacity = 0.0 }
                movePane.addAll(
                    memberImageTokenViews[i],
                    Label(text = memberNames[i], width = 800, height = 200, posX = 1080, posY = 1100+800*i,
                        font = nameFont, alignment = Alignment.CENTER_LEFT),
                    Label(text = memberJobs[i], width = 800, height = 200, posX = 1080, posY = 1180+800*i,
                        font = jobFont, alignment = Alignment.CENTER_RIGHT),
                    Label(text = memberQuotes[i], width = 800, height = 200, posX = 1080, posY = 1400+800*i,
                        font = quoteFont),
                )
            } else {
                memberImageTokenViews += TokenView(visual = memberImages[i], width = 1000, height = 750,
                    posX = 880, posY = 1120+800*i).apply { opacity = 0.0 }
                movePane.addAll(
                    memberImageTokenViews[i],
                    Label(text = memberNames[i], width = 800, height = 200, posX = 40, posY = 1100+800*i,
                        font = nameFont, alignment = Alignment.CENTER_RIGHT),
                    Label(text = memberJobs[i], width = 800, height = 200, posX = 40, posY = 1180+800*i,
                        font = jobFont, alignment = Alignment.CENTER_LEFT),
                    Label(text = memberQuotes[i], width = 800, height = 200, posX = 40, posY = 1400+800*i,
                        font = quoteFont),
                )
            }
        }



        movePane.add(Label(text = "Music by Kevin MacLeod, bensound, Hans Zimmer, NetworkMusicEnsemble",
            font = Font(size = 50, color = Color.WHITE, family = "Calibri"), width = 1920, height = 200, posY = 8600))
        movePane.add(copyrightLabel)


        movePane.addAll(creditsLabel,anastasiiaImage,ikhlawiImage)

        addComponents(tk)

        background = ColorVisual(0,0,0)
        addComponents(movePane, backToTitleSceneButton, soundToggleButton, musicToggleButton, fullscreenToggleButton)
    }

    private fun pathToImageVisuals(path: String, length : Int) :List<Visual>{
        val output = mutableListOf<ImageVisual>()

        for (i in 1..length+1) output.add(ImageVisual("$path$i.png"))

        return output.toList()
    }

    /**
     * called when scene is shown
     */

    fun trigger() {
        println("trigger credits roll animation")
        copyrightLabel.opacity = 1.0
        tk.opacity = 0.0
        i = 0
        creditsLabel.opacity = 1.0; memberImageTokenViews[0].opacity = 1.0; memberImageTokenViews[1].opacity = 1.0
        playAnimation(moveAnimation)
        playAnimation(FadeAnimation(creditsLabel, 0.0, 1.0, duration = 1000))

        playAnimation(DelayAnimation(duration = 18000).apply { onFinished = { creditsLabel.opacity = 0.0
            memberImageTokenViews[2].opacity = 1.0; memberImageTokenViews[3].opacity = 1.0 }})
        playAnimation(DelayAnimation(duration = 31000).apply { onFinished = { memberImageTokenViews[0].opacity = 0.0
            memberImageTokenViews[1].opacity = 0.0; memberImageTokenViews[4].opacity = 1.0
            memberImageTokenViews[5].opacity = 1.0 }})
        playAnimation(DelayAnimation(duration = 40000).apply { onFinished = { memberImageTokenViews[2].opacity = 0.0
            memberImageTokenViews[3].opacity = 0.0; memberImageTokenViews[6].opacity = 1.0
            memberImageTokenViews[7].opacity = 1.0; memberImageTokenViews[8].opacity = 1.0 }})
    }

    /**
     * plays Explosion Animation
     */

    fun explosion(){
        playAnimation(FadeAnimation(copyrightLabel, 1.0, 0.0, duration = 500))
        memberImageTokenViews.forEach { it.opacity = 0.0 }
        tk.opacity = 1.0
        playAnimation(explosionAnimation)
    }


}


