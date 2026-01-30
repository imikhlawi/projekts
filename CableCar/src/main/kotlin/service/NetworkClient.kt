package service


//import edu.udo.cs.sopra.ntf.GameInitMessage
//import edu.udo.cs.sopra.ntf.TurnMessage
import tools.aqua.bgw.core.BoardGameApplication
import tools.aqua.bgw.net.client.BoardGameClient
import tools.aqua.bgw.net.client.NetworkLogging
//import tools.aqua.bgw.net.common.annotations.GameActionReceiver
import tools.aqua.bgw.net.common.notification.PlayerJoinedNotification
import tools.aqua.bgw.net.common.notification.PlayerLeftNotification
import tools.aqua.bgw.net.common.response.CreateGameResponse
import tools.aqua.bgw.net.common.response.CreateGameResponseStatus
import tools.aqua.bgw.net.common.response.JoinGameResponse
import tools.aqua.bgw.net.common.response.JoinGameResponseStatus

/**
 * Network client: to handle player actions in network game
 *
 * @property networkService
 * @constructor
 *
 * @param playerName
 * @param host
 * @param secret
 */

class NetworkClient(playerName: String,
                    host: String,
                    secret: String,
                    var networkService: NetworkService,
) : BoardGameClient(playerName, host, secret, networkLoggingBehavior = NetworkLogging.VERBOSE) {

//    var sessionID: String? = null

    override fun onJoinGameResponse(response: JoinGameResponse) {
        if (networkService.connectionState == ConnectionState.WAITING_FOR_JOIN_CONFIRMATION) {
            println("received unexpected join confirmation")
        }

        when (response.status) {
            JoinGameResponseStatus.SUCCESS -> {
                networkService.updateConnectionState(ConnectionState.WAITING_FOR_INIT)
                println("Successfully joined game.")
                println("The host's greeting is \"${response.message}\".")
            }
            JoinGameResponseStatus.ALREADY_ASSOCIATED_WITH_GAME ->
                error("You are already in a game.")

            JoinGameResponseStatus.INVALID_SESSION_ID ->
                error("SessionID invalid.")

            JoinGameResponseStatus.PLAYER_NAME_ALREADY_TAKEN ->
                error("Player name already exists.")

            JoinGameResponseStatus.SERVER_ERROR ->
                error(response)
            else -> disconnect()
        }
    }

    override fun onCreateGameResponse(response: CreateGameResponse) {
        if (networkService.connectionState != ConnectionState.WAITING_FOR_HOST_CONFIRMATION) {
            println("unexpected host confirmation")
        }

        when (response.status) {
            CreateGameResponseStatus.SUCCESS -> {
                println("Successfully created game.")
                networkService.updateConnectionState(ConnectionState.WAITING_FOR_PLAYERS)
            }

            CreateGameResponseStatus.ALREADY_ASSOCIATED_WITH_GAME ->
                error("Leave current game first.")

            CreateGameResponseStatus.SESSION_WITH_ID_ALREADY_EXISTS ->
                error("Session id already exists.")

            CreateGameResponseStatus.GAME_ID_DOES_NOT_EXIST ->
                error("GameID does not exist.")

            CreateGameResponseStatus.SERVER_ERROR ->
                error(response)
            else -> disconnect()
        }
    }

    override fun onPlayerJoined(notification: PlayerJoinedNotification) {
        if(networkService.connectionState != ConnectionState.WAITING_FOR_INIT) {
            if (networkService.connectionState != ConnectionState.WAITING_FOR_PLAYERS) {
                println("unexpected player joined notification")
                return
            }


            println("${notification.sender} joined the game.")
            println("${notification.sender}: ${notification.message}")

            networkService.joinedPlayers.add(notification.sender)

            BoardGameApplication.runOnGUIThread(Runnable { networkService.onAllRefreshables {
                refreshAfterPlayerJoinedInWaitSession(notification.sender) } })

            if (networkService.joinedPlayers.size > 6) {
                networkService.updateConnectionState(ConnectionState.WAITING_FOR_PLAYERS)
                println("Too many players are connected to the game. Currently Connected Players: " +
                        "${networkService.joinedPlayers.size}")
                return
            } else if (networkService.joinedPlayers.size > 2) {
                networkService.updateConnectionState(ConnectionState.READY_FOR_GAME)
            }



        } else {
            println("A Player joined the remotely hosted game")
        }
    }

    override fun onPlayerLeft(notification: PlayerLeftNotification) {
        if (networkService.connectionState != ConnectionState.WAITING_FOR_INIT) {
            if (networkService.connectionState == ConnectionState.WAITING_FOR_PLAYERS) {

                BoardGameApplication.runOnGUIThread(Runnable { networkService.onAllRefreshables {
                    refreshAfterPlayerLeftInWaitSession(notification.sender) } })

                networkService.joinedPlayers.remove(notification.sender)

                if (networkService.joinedPlayers.size < 2) {
                    networkService.updateConnectionState(ConnectionState.WAITING_FOR_PLAYERS)
                }
                println("Player: ${notification.sender} has left the game. Current Players: " +
                        "${networkService.joinedPlayers.size}")
            }

            if(networkService.connectionState == ConnectionState.GAME_INITIALIZED || networkService.connectionState ==
                ConnectionState.PLAYING_TURN || networkService.connectionState == ConnectionState.WAITING_FOR_TURN) {
                println("a player has left the game. disconnecting.")
                disconnect()
            }
        }
    }

//    /**
//     * On init received
//     *
//     * @param message
//     * @param sender
//     */
//    @GameActionReceiver
//    fun onInitReceived(message: GameInitMessage, sender: String) {
//        if (networkService.connectionState == ConnectionState.WAITING_FOR_INIT) {
//            BoardGameApplication.runOnGUIThread {
//                networkService.startNewJoinedGame(message)
//            }
//        }
//    }
//
//    /**
//     * On turn message received
//     *
//     * @param message
//     * @param sender
//     */
//    @GameActionReceiver
//    fun onTurnMessageReceived(message: TurnMessage, sender: String) {
//        BoardGameApplication.runOnGUIThread {
//            if (sender == networkService.rootService.currentGame!!.currentTurn.
//                players[networkService.rootService.currentGame!!.currentTurn.currentPlayerIndex].name) {
//                // check gameState
//                // check placedTiles
//                val ePlacedTilesSize = networkService.rootService.currentGame!!.currentTurn.gameField.tiles.size
//                val mPlacedTilesSize = message.gameStateVerificationInfo.placedTiles.size
//                if (ePlacedTilesSize != mPlacedTilesSize) {
//                    println("PlacedTiles in Entity is not same size as PlacedTiles in Received TurnMessage." +
//                            " Size of Entity PlacedTiles:" +
//                            " $ePlacedTilesSize, Size of TurnMessage PlacedTiles: $mPlacedTilesSize")
//                } else {
//                    for (i in 0 until message.gameStateVerificationInfo.placedTiles.size) {
//                        val eID = networkService.rootService.currentGame!!.currentTurn.gameField.tiles[i].id
//                        val mID = message.gameStateVerificationInfo.placedTiles[i].id
//                        if (eID != mID) {
//                            println("PlacedTiles with Index: $i does not have the same ID." +
//                                    " ID of Entity Tile: $eID, ID of TurnMessage Tile: $mID")
//                        }
//                    }
//                }
//                // check TileStack/Supply
//                val eTileStackSize = networkService.rootService.currentGame!!.currentTurn.
//                                        gameField.tileStack.tiles.size
//                val mTileStackSize = message.gameStateVerificationInfo.supply.size
//                if (eTileStackSize != mTileStackSize) {
//                    println("TileStack in Entity is not the same size as Supply in Received TurnMessage." +
//                            " Size of Entity TileStack: $eTileStackSize, " +
//                            "Size of TurnMessage TileStack: $mTileStackSize")
//                } else {
//                    for (i in 0 until mTileStackSize) {
//                        val eStackID = networkService.rootService.currentGame!!.currentTurn.
//                                        gameField.tileStack.tiles[i].id
//                        val mStackID = message.gameStateVerificationInfo.supply[i]
//                        if (eStackID != mStackID) {
//                            println("TileID in TileStack does not equal ID in Received TurnMessage. " +
//                                    "Index: $i, Entity ID: $eStackID, TurnMessage ID: $mStackID")
//                        }
//                    }
//                }
//                // check playerScores
//                val ePlayers = networkService.rootService.currentGame!!.currentTurn.players
//                for (i in 0 until ePlayers.size) {
//                    if (ePlayers[i].score != message.gameStateVerificationInfo.playerScores[i]) {
//                        println("PlayerScore of ${ePlayers[i].name} " +
//                                "does not equal score from Received TurnMessage. " +
//                                "Entity Score: ${ePlayers[i].score}," +
//                                " TurnMessage Score: ${message.gameStateVerificationInfo.playerScores[i]}")
//                    }
//                }
//
//                // placeTile
//                networkService.rootService.playerActionService.
//                    placeTile(!message.fromSupply, message.posX,
//                        message.posY, message.rotation/90, true)
//
//            } else {
//                println("Received TurnMessage from $sender, even though it is not their turn.")
//            }
//            //next player gets called by playerActionServices
//        }
//    }


}