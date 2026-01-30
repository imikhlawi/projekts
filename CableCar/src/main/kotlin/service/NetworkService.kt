package service

import edu.udo.cs.sopra.ntf.*
import entity.*
import entity.Tile

/**
 * Network service: class to handle network connections for online play
 *
 * @property rootService
 * @constructor Create empty Network service
 */

class NetworkService(var rootService: RootService): AbstractRefreshingService() {
    var connectionState: ConnectionState = ConnectionState.DISCONNECTED
    private val SERVER_ADDRESS = "sopra.cs.tu-dortmund.de:80/bgw-net/connect"
    private val GAME_ID = "CableCar"

    private var client: NetworkClient? = null
    var playerName = ""

    var joinedPlayers = mutableListOf<String>()


    /**
     * Host game
     *
     * @param secret
     * @param playerName
     * @param sessionID
     */
    fun hostGame(secret: String, playerName: String, sessionID: String) {
        if(connect(secret, playerName)) {
            client?.createGame(GAME_ID, sessionID, "Hallo von Gruppe 10")
            updateConnectionState(ConnectionState.WAITING_FOR_HOST_CONFIRMATION)
            this.playerName = playerName
            rootService.gameService.isHostedGame = true
        }
    }

    /**
     * Join game
     *
     * @param secret
     * @param playerName
     * @param sessionID
     */
    fun joinGame(secret: String, playerName: String, sessionID: String) {
        if(connect(secret, playerName)) {
            client?.joinGame(sessionID, "Hallo von Gruppe 10.")
            this.playerName = playerName
            rootService.gameService.isHostedGame = true
        }
    }


    /**
     * Opens a connection to the Server via creating the client
     */
    private fun connect(secret: String, playerName: String): Boolean {
        require(connectionState == ConnectionState.DISCONNECTED && client == null)
        { "already connected to another game" }

        require(secret.isNotBlank()) { "server secret must be given" }
        require(playerName.isNotBlank()) { "player name must be given" }

        val newClient =
            NetworkClient(
                playerName = playerName,
                host = SERVER_ADDRESS,
                secret = secret,
                networkService = this
            )

        if(newClient.connect()) {
            this.client = newClient
            updateConnectionState(ConnectionState.CONNECTED)
            return true
        }
        return false
    }

    /**
     * Start new hosted game
     *
     * @param hostPlayerName
     * @param rotationAllowed
     * @param drawStack
     */
    fun startNewHostedGame(hostPlayerName: String, rotationAllowed: Boolean, drawStack: List<Tile>) {
        val playerInfoList = mutableListOf<PlayerInfo>()
        playerInfoList.add(PlayerInfo(hostPlayerName))
        for (player in joinedPlayers) {
            playerInfoList.add(PlayerInfo(player))
        }

        // player list for local game
        joinedPlayers.add(0, hostPlayerName)

        val tileStack = mutableListOf<edu.udo.cs.sopra.ntf.Tile>()

        for (tile in drawStack) {
            val connectionInfo = mutableListOf<ConnectionInfo>()
            for (c in tile.originalPorts)
                connectionInfo.add(ConnectionInfo(c.first, c.second))
            tileStack.add(
                edu.udo.cs.sopra.ntf.Tile(
                    tile.id,
                    connectionInfo
                )
            )
        }


        val gameInitMessage = GameInitMessage(
            rotationAllowed = rotationAllowed,
            players = playerInfoList,
            tileSupply = tileStack
        )
        updateConnectionState(ConnectionState.READY_FOR_GAME)
        sendGameInitMessage(gameInitMessage)

    }

    /**
     * Start new joined game
     *
     * @param message
     */
    fun startNewJoinedGame(message: GameInitMessage) {


        rootService.currentGame = Game(
            Turn(
                GameField(
                    mutableListOf(),
                    mutableListOf(),
                    TileStack(mutableListOf())
                ), mutableListOf()
            )
        )

        rootService.gameService.readTileCSV()

        val tileStack = mutableListOf<Tile>()
        // create tileStack from supplied list
        for (tileIndex in message.tileSupply) {
            tileStack.add(rootService.gameService.tileLookUp[tileIndex.id])
        }
        // add tileStack to entity
        rootService.currentGame!!.currentTurn.gameField.tileStack.tiles = tileStack
        // add players to local entity and joined players
        joinedPlayers = mutableListOf()

        for (player in message.players) {
            rootService.currentGame!!.currentTurn.players.add(Player(player.name,null))
            joinedPlayers.add(player.name)
        }

        rootService.gameService.isHostedGame = false
        rootService.gameService.isLocalOnlyGame = false

        rootService.gameService.distributeTiles()
        rootService.gameService.playersToPositions()
        updateConnectionState(ConnectionState.GAME_INITIALIZED)

        onAllRefreshables { refreshAfterStartGame() }
    }

    /**
     * Send game init message
     *
     * @param message
     */
    fun sendGameInitMessage(message: GameInitMessage) {
        // send GameInitMessage

        if (connectionState != ConnectionState.READY_FOR_GAME) {
            if (connectionState == ConnectionState.WAITING_FOR_PLAYERS) {
                println("Not enough or too many Players have joined the Game. Current Amount: ${joinedPlayers.size}")
            }
            return
        }
        client?.sendGameActionMessage(message)
        updateConnectionState(ConnectionState.GAME_INITIALIZED)
    }

    /**
     * Send turn message
     *
     * @param message
     */
    fun sendTurnMessage(message: TurnMessage) {
        client?.sendGameActionMessage(message)
    }

    /**
     * Update connection state
     *
     * @param newState
     */
    fun updateConnectionState(newState: ConnectionState) {
        connectionState = newState
    }

    /**
     * Disconnect
     *
     */
    fun disconnect() {
        client?.disconnect()
        println("disconnecting.")
        updateConnectionState(ConnectionState.DISCONNECTED)
    }

}