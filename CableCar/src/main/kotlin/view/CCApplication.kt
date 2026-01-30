package view

import com.soywiz.korau.sound.SoundChannel
import com.soywiz.korau.sound.await
import com.soywiz.korau.sound.infinitePlaybackTimes
import com.soywiz.korau.sound.readMusic
import com.soywiz.korio.async.async
import com.soywiz.korio.file.std.resourcesVfs
import entity.Player
import kotlinx.coroutines.GlobalScope
import service.RootService
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.BoardGameApplication
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.CompoundVisual
import tools.aqua.bgw.visual.ImageVisual
import tools.aqua.bgw.visual.TextVisual
import java.awt.Color

/**
 * Main BoardGameApplication. contains all scenes and manages scene traversing and audio playback & toggle
 *
 * [creditsScene], [gameOverScene], [gameScene], [lobbyScene], [mainMenuScene],
 * [quickMenuGameScene], [titleScene]: Menu & GameScenes
 * [musicEnabled], [soundEnabled]: global flag for music/sound
 * [musicButtons], [soundButtons]: all Buttons distributed in all Scenes for updating
 * [nameEmptyCheck] writes the Host name in the Lobby Scene when not empty
 * */

class CCApplication : BoardGameApplication("Carbel Car Game"), Refreshable {

    private val rootService = RootService()

    private val creditsScene = CreditsScene().apply {
        fullscreenToggleButton.onMouseClicked = { toggleFullscreen() }
        backToTitleSceneButton.onMouseClicked = {
            timesClicked++
            when (timesClicked) {
                1 -> {
                    backToTitleSceneButton.apply { posX = 1400.0; posY = 300.0 }; playNopeSound()
                }

                2 -> {
                    backToTitleSceneButton.apply { posX = 700.0; posY = 200.0 }; playNopeSound()
                }

                3 -> {
                    backToTitleSceneButton.apply { posX = 1500.0; posY = 500.0 }; playNopeSound()
                }

                4 -> {
                    backToTitleSceneButton.apply { posX = 200.0; posY = 800.0 }; playNopeSound()
                }

                5 -> {
                    backToTitleSceneButton.apply { posX = 1400.0; posY = 200.0 }; playNopeSound()
                }

                6 -> {
                    backToTitleSceneButton.apply { posX = 100.0; posY = 930.0 }
                    timesClicked = 0
                    explicitlyShowTitleScene()
                }
            }
        }
        soundToggleButton.onMouseClicked = { toggleSound() }
        musicToggleButton.onMouseClicked = { toggleMusic() }
        moveAnimation.apply { onFinished = { explosion(); playExplosionSound() } }
        explosionAnimation.apply { onFinished = { explicitlyShowTitleScene(); tk.opacity = 0.0; } }
    }

    private val gameOverScene = GameOverScene(rootService).apply {
        fullscreenToggleButton.onMouseClicked = { toggleFullscreen() }
        soundToggleButton.onMouseClicked = { toggleSound() }
        musicToggleButton.onMouseClicked = { toggleMusic() }
        mainMenuButton.onMouseClicked = {
            explicitlyShowTitleScene()
            gameScene.resetScene()
            resetConfig()
        }
        quitButton.onMouseClicked = {
            hideMenuScene(3000)
            showMenuScene(confirmQuitMenuScene)
        }
    }

    private val gameScene = GameScene(rootService).apply {
        quickMenuButton.onMouseClicked = {
            hideMenuScene(3000)
            showAndStoreMenuScene(quickMenuGameScene, 3000)
        }
        startGameButton.onMouseClicked = {
            startGameButton.isDisabled = true; startGameButton.opacity = 0.0
            pleaseWaitLabel.isDisabled = false; pleaseWaitLabel.opacity = 1.0
            rootService.gameService.startNewGame(playerList, isLocalOnlyGame = false, isHostedGame = true,
                rotationAllowed = hostLobbyScene.allowTileRotationCheckbox.isChecked)
        }
    }

    private val hostLobbyScene = HostLobbyScene().apply {
        fullscreenToggleButton.onMouseClicked = { toggleFullscreen() }
        quitButton.onMouseClicked = {
            hideMenuScene(3000)
            showMenuScene(confirmQuitMenuScene)
        }
        soundToggleButton.onMouseClicked = { toggleSound() }
        musicToggleButton.onMouseClicked = { toggleMusic() }
        backToMainMenuSceneButton.onMouseClicked = {
            hideMenuScene(3000)
            showAndStoreMenuScene(mainMenuScene, 3000)
        }
        hostGameButton.onMouseClicked = {
            if (sessionIdTextField.text != "") {
                startHostedGame()
            } else {
                playNopeSound()
            }
        }
    }

    private val lobbyScene = LobbyScene().apply {
        quitButton.onMouseClicked = {
            hideMenuScene(3000)
            showMenuScene(confirmQuitMenuScene)
        }
        soundToggleButton.onMouseClicked = { toggleSound() }
        musicToggleButton.onMouseClicked = { toggleMusic() }
        fullscreenToggleButton.onMouseClicked = { toggleFullscreen() }
        backToMainMenuSceneButton.onMouseClicked = {
            hideMenuScene(3000)
            showAndStoreMenuScene(mainMenuScene, 3000)
        }
        hostRealButton.onMouseClicked = { playerType[0] = 0; startLobbyGame() }
        hostDumbButton.onMouseClicked = { playerType[0] = 1; startLobbyGame() }
        hostSmartButton.onMouseClicked = { playerType[0] = 2; startLobbyGame() }
    }

    private val mainMenuScene = MainMenuScene().apply {
        backToTitleSceneButton.onMouseClicked = { hideMenuScene(3000) }
        quitButton.onMouseClicked = {
            hideMenuScene(3000)
            showMenuScene(confirmQuitMenuScene)
        }
        soundToggleButton.onMouseClicked = { toggleSound() }
        musicToggleButton.onMouseClicked = { toggleMusic() }
        fullscreenToggleButton.onMouseClicked = { toggleFullscreen() }
        joinButton.onMouseClicked = { nameEmptyCheck(1) }
        hostButton.onMouseClicked = { nameEmptyCheck(2) }
        hotseatButton.onMouseClicked = { nameEmptyCheck(3) }
        creditsButton.onMouseClicked = {
            hideMenuScene(3000)
            explicitlyShowCreditsScene()
            if (musicEnabled) playCreditsMusic()
        }
        debugGameSceneButton.onMouseClicked = {
            this@CCApplication.rootService.gameService.startNewGame(
                listOf(
                    Player("Player1____________",null),
                    Player("Player2___________",null),
                    Player("Player3__________",null),
                    Player("Player4_________",null),
                    Player("Player5________",null),
                    Player("Player6______",null)
                ), isLocalOnlyGame = true, isHostedGame = false, rotationAllowed = true
            )
            hideMenuScene(3000)
            explicitlyShowGameScene()

        }
        debugGameEndSceneButton.onMouseClicked = { explicitlyShowGameOverScene() }
    }

    private val networkJoinScene = NetworkJoinScene().apply {
        fullscreenToggleButton.onMouseClicked = { toggleFullscreen() }
        quitButton.onMouseClicked = {
            hideMenuScene(3000)
            showMenuScene(confirmQuitMenuScene)
        }
        soundToggleButton.onMouseClicked = { toggleSound() }
        musicToggleButton.onMouseClicked = { toggleMusic() }
        backToMainMenuSceneButton.onMouseClicked = {
            hideMenuScene(3000)
            showAndStoreMenuScene(mainMenuScene, 3000)
        }
        joinGameButton.onMouseClicked = {
            if (sessionIDTextField.text != "") {
                hideMenuScene(3000)
                this@CCApplication.rootService.networkService.joinGame(
                    "cable22", mainMenuScene.nameField.text, sessionIDTextField.text
                )
                hideMenuScene()
                gameScene.joinGameWaitForPlayers(mainMenuScene.nameField.text,aiGameCheckBox.isChecked)
                explicitlyShowGameScene()
                gameScene.pleaseWaitLabel.opacity = 1.0; gameScene.pleaseWaitLabel.isDisabled = false
            } else {
                playNopeSound()
            }
        }
    }

    private val confirmQuitMenuScene = ConfirmQuitMenuScene().apply {
        yesButton.onMouseClicked = { exit() }
        noButton.onMouseClicked = {
            hideMenuScene()
            showAndStoreMenuScene(activeMenuScene!!, 3000)
        }

    }

    private val quickMenuGameScene = QuickMenuGameScene().apply {
        soundToggleButton.onMouseClicked = { toggleSound() }
        musicToggleButton.onMouseClicked = { toggleMusic() }
        fullscreenToggleButton.onMouseClicked = { toggleFullscreen() }
        exitQuitMenuSceneButton.onMouseClicked = { hideMenuScene(3000) }
        quitButton.onMouseClicked = {
            hideMenuScene(3000)
            showMenuScene(confirmQuitMenuScene)
        }
    }

    private val titleScene = TitleScene().apply {
        toMenuButton.onKeyPressed = { showAndStoreMenuScene(mainMenuScene, 3000) }
        toMenuButton.onMouseClicked = { showAndStoreMenuScene(mainMenuScene, 3000) }
        trigger.onMouseEntered = { playTitleMusic(); fadeIn() }
    }

    private var activeMenuScene: MenuScene? = null

    private var musicChannel: SoundChannel? = null
    private var soundChannel: SoundChannel? = null

    private var musicEnabled = true
    private var soundEnabled = true

    private val musicButtons = listOf(
        mainMenuScene.musicToggleButton,
        lobbyScene.musicToggleButton,
        quickMenuGameScene.musicToggleButton,
        creditsScene.musicToggleButton,
        gameOverScene.musicToggleButton,
        networkJoinScene.musicToggleButton,
        hostLobbyScene.musicToggleButton
    )
    private val soundButtons = listOf(
        mainMenuScene.soundToggleButton,
        lobbyScene.soundToggleButton,
        quickMenuGameScene.soundToggleButton,
        creditsScene.soundToggleButton,
        gameOverScene.soundToggleButton,
        networkJoinScene.soundToggleButton,
        hostLobbyScene.soundToggleButton
    )

    private val fullscreenButtons = listOf(
        mainMenuScene.fullscreenToggleButton,
        lobbyScene.fullscreenToggleButton,
        quickMenuGameScene.fullscreenToggleButton,
        creditsScene.fullscreenToggleButton,
        gameOverScene.fullscreenToggleButton,
        networkJoinScene.fullscreenToggleButton,
        hostLobbyScene.fullscreenToggleButton
    )

    private val musicButtonEnableImage = ImageVisual("music_enabled.png")
    private val musicButtonDisableImage = ImageVisual("music_disabled.png")
    private val soundButtonEnableImage = ImageVisual("sound_enabled.png")
    private val soundButtonDisableImage = ImageVisual("sound_disabled.png")
    private val fullscreenImage = ImageVisual("fullscreen.png")
    private val windowedImage = ImageVisual("windowed.png")


    init {
        rootService.addRefreshables(
            gameScene,
            this,
            gameOverScene
        )
        this.showGameScene(titleScene)
        isFullScreen = false
        icon = ImageVisual("icon.png")
    }

    /**
     * checks if name is input in mainMenuScene and passes it to the first entry in player table in lobbyScene
     * for scene transition
     */

    private fun nameEmptyCheck(case: Int) {
        if (mainMenuScene.nameField.text != "") {
            when (case) {
                1 -> {  //join network
                    hideMenuScene(3000)
                    showAndStoreMenuScene(networkJoinScene, 3000)
                }

                2 -> {  //host network
                    hideMenuScene(3000)
                    showAndStoreMenuScene(hostLobbyScene, 3000)
                }

                else -> {
                    hideMenuScene(3000)
                    lobbyScene.playerBoxLabel[0].visual = CompoundVisual(
                        ColorVisual(63, 255, 63).apply { transparency = 0.3 }, TextVisual(
                            font = Font(size = 60, color = Color.BLACK, family = "Calibri"),
                            text = mainMenuScene.nameField.text,
                            alignment = Alignment.CENTER_LEFT,
                            offsetX = 20
                        )
                    )
                    showAndStoreMenuScene(lobbyScene, 3000)
                }
            }
        } else {
            mainMenuScene.nameErrorDisplay()
            playNopeSound()
        }

    }

    /**
     * initializes hotseat mode game
     */

    private fun startLobbyGame() {
        lobbyScene.removeComponents(lobbyScene.isHostRealAIClickDisableLabel, lobbyScene.isHostRealAIBG,
            lobbyScene.hostRealButton, lobbyScene.hostSmartButton, lobbyScene.hostDumbButton)
        lobbyScene.isHostRealAIClickDisableLabel.isDisabled = true
        lobbyScene.isHostRealAIClickDisableLabel.opacity = 0.0
        lobbyScene.isHostRealAIBG.isDisabled = true; lobbyScene.isHostRealAIBG.opacity = 0.0
        lobbyScene.hostRealButton.isDisabled = true; lobbyScene.hostRealButton.opacity = 0.0
        lobbyScene.hostSmartButton.isDisabled = true; lobbyScene.hostSmartButton.opacity = 0.0
        lobbyScene.hostDumbButton.isDisabled = true; lobbyScene.hostDumbButton.opacity = 0.0

        val tempPlayerList = listOfNotNull(
            Player(mainMenuScene.nameField.text, if(lobbyScene.playerType[0] == 0) null
            else lobbyScene.playerType[0] != 1),
            Player(lobbyScene.nameFields[0].text, if(lobbyScene.playerType[1] == 0) null
            else lobbyScene.playerType[1] != 1),
            if (lobbyScene.nameFields[1].text != "") Player(lobbyScene.nameFields[1].text,
                if(lobbyScene.playerType[2] == 0) null else lobbyScene.playerType[2] != 1) else null,
            if (lobbyScene.nameFields[2].text != "") Player(lobbyScene.nameFields[2].text,
                if(lobbyScene.playerType[3] == 0) null else lobbyScene.playerType[3] != 1) else null,
            if (lobbyScene.nameFields[3].text != "") Player(lobbyScene.nameFields[3].text,
                if(lobbyScene.playerType[4] == 0) null else lobbyScene.playerType[4] != 1) else null,
            if (lobbyScene.nameFields[4].text != "") Player(lobbyScene.nameFields[4].text,
                if(lobbyScene.playerType[5] == 0) null else lobbyScene.playerType[5] != 1) else null)

        if (lobbyScene.shuffleTurnOrderCheckbox.isChecked) {
            this@CCApplication.rootService.gameService.startNewGame(
                tempPlayerList.shuffled(), isLocalOnlyGame = true, isHostedGame = false,
                rotationAllowed = lobbyScene.allowTileRotationCheckbox.isChecked
            )
        } else {
            this@CCApplication.rootService.gameService.startNewGame(
                tempPlayerList, isLocalOnlyGame = true, isHostedGame = false,
                rotationAllowed = lobbyScene.allowTileRotationCheckbox.isChecked
            )
        }

        hideMenuScene(3000)
        explicitlyShowGameScene()
    }

    /**
     * starts game in host mode
     */

    private fun startHostedGame() {
        this@CCApplication.rootService.networkService.hostGame(
            "cable22", mainMenuScene.nameField.text, hostLobbyScene.sessionIdTextField.text)
        gameScene.hostGameWaitForPlayers(mainMenuScene.nameField.text,hostLobbyScene.allowKITurnierCheckbox.isChecked)

        hideMenuScene(3000); explicitlyShowGameScene()
    }

    /**
     * ugh
     */

    override fun playNopeSoundInCCApp() {
        playNopeSound()
    }

    /**
     * updates all Buttons in all Scenes with updated visual
     */

    private fun toggleMusic() {
        musicEnabled = !musicEnabled
        for (button in musicButtons) {
            if (!musicEnabled) {
                button.visual = musicButtonDisableImage
                if (musicChannel != null) musicChannel!!.volume = 0.0
            } else {
                button.visual = musicButtonEnableImage
                if (musicChannel != null) musicChannel!!.volume = 0.7
            }
        }
    }

    /**
     * analog to toggleMusic()
     */

    private fun toggleSound() {
        soundEnabled = !soundEnabled
        for (button in soundButtons) {
            if (!soundEnabled) {
                button.visual = soundButtonDisableImage
                if (soundChannel != null) soundChannel!!.volume = 0.0
            } else {
                button.visual = soundButtonEnableImage
                if (soundChannel != null) soundChannel!!.volume = 1.0
            }
        }
    }

    /**
     * analog to toggleMusic()
     */

    private fun toggleFullscreen() {
        isFullScreen = !isFullScreen
        for (button in fullscreenButtons) {
            if (isFullScreen) {
                button.visual = windowedImage
            } else {
                button.visual = fullscreenImage
            }
        }
    }

    /**
     * playback of music in credits scene via KorAU audio library
     */

    private fun playTitleMusic() {
        if (musicChannel != null) {
            musicChannel!!.stop()
        }
        if (musicEnabled) {
            GlobalScope.async {
                val music = resourcesVfs["title_music.wav"].readMusic()
                musicChannel = music.play(infinitePlaybackTimes)
                musicChannel!!.await()
            }
        }
    }

    /**
     * playback of music in credits scene via KorAU audio library
     */

    private fun playCreditsMusic() {
        if (musicChannel != null) {
            musicChannel!!.stop()
        }
        if (musicEnabled) {
            GlobalScope.async {
                val music = resourcesVfs["credits_music.wav"].readMusic()
                musicChannel = music.play(infinitePlaybackTimes)
                musicChannel!!.await()
            }
        }
    }

    /**
     * playback of music in game scene via KorAU audio library
     */

    private fun playGameSceneMusic() {
        if (musicChannel != null) {
            musicChannel!!.stop()
        }
        if (musicEnabled) {
            GlobalScope.async {
                val music = resourcesVfs["game_scene_music.wav"].readMusic()
                musicChannel = music.play(infinitePlaybackTimes)
                musicChannel!!.await()
            }
        }
    }

    /**
     * playback of music in game scene via KorAU audio library
     */

    private fun playGameOverSceneMusic() {
        if (musicChannel != null) {
            musicChannel!!.stop()
        }
        if (musicEnabled) {
            GlobalScope.async {
                val music = resourcesVfs["game_over_scene.wav"].readMusic()
                musicChannel = music.play(infinitePlaybackTimes)
                musicChannel!!.await()
            }
        }
    }

    /**
     * playback of sound via KorAU audio library
     */

    private fun playNopeSound() {
        if (soundChannel != null) {
            soundChannel!!.stop()
        }
        if (soundEnabled) {
            GlobalScope.async {
                val sound = resourcesVfs["nope_sound_effect.wav"].readMusic()
                soundChannel = sound.play()
                soundChannel!!.await()
            }
        }
    }

    /**
     * playback of sound via KorAU audio library
     */

    private fun playExplosionSound() {
        if (soundChannel != null) {
            soundChannel!!.stop()
        }
        if (soundEnabled) {
            GlobalScope.async {
                val sound = resourcesVfs["explosion_sound_effect.wav"].readMusic()
                soundChannel = sound.play()
                soundChannel!!.await()
            }
        }
    }

    /**
     * workaround for kotlin compiler warning
     */

    private fun explicitlyShowCreditsScene() {
        showGameScene(creditsScene); creditsScene.trigger()
    }

    /**
     * workaround for kotlin compiler warning
     */

    private fun explicitlyShowGameScene() {
        playGameSceneMusic()
        showGameScene(gameScene)
    }

    /**
     * workaround for kotlin compiler warning
     */

    private fun explicitlyShowGameOverScene() {
        hideMenuScene(3000); showGameScene(gameOverScene)
        playGameOverSceneMusic()
    }

    /**
     * workaround for kotlin compiler warning
     */

    private fun explicitlyShowTitleScene() {
        showGameScene(titleScene)
        playTitleMusic()
        titleScene.gameLabel.opacity = 1.0
        titleScene.trigger.opacity = 0.0
        repaint()
    }

    /**
     * when a MenuScene is called it needs to be saved for confirmQuit MenuScene
     * to revert to formerly displayed MenuScene
     */

    private fun showAndStoreMenuScene(menuScene: MenuScene, fadeTime: Int) {
        activeMenuScene = menuScene
        showMenuScene(menuScene, fadeTime)
    }

    /**
     * calls game over scene
     */

    override fun refreshAfterGameFinished() {
        explicitlyShowGameOverScene()
    }

}



