package service

import entity.Game
import view.Refreshable

/**
 * The RootService class is the root of all services in the application.
 * It serves as a central point of access to all other services.
 *
 * @property currentGame : The current instance of the Game that the application is currently running.
 * @property gameService : The service responsible for managing the game itself, such as starting new games,
 * undo/redo actions, finding winners, and so on.
 * @property networkService : The service responsible for managing the network communication of the application.
 * @property playerActionService : The service responsible for managing the players actions,
 * such as placing tiles and rotating them.
 */
class RootService {

    var currentGame: Game? = null

    var gameService: GameService = GameService(this)
    var networkService = NetworkService(this)
    var playerActionService: PlayerActionService = PlayerActionService(this)

    /**
     * Adds the provided [newRefreshable] to all services connected
     * to this root service
     */
    fun addRefreshable(newRefreshable: Refreshable) {
        gameService.addRefreshable(newRefreshable)
        networkService.addRefreshable(newRefreshable)
        playerActionService.addRefreshable(newRefreshable)
    }

    /**
     * Adds each of the provided [newRefreshables] to all services
     * connected to this root service
     */
    fun addRefreshables(vararg newRefreshables: Refreshable) {
        newRefreshables.forEach { addRefreshable(it) }
    }
}
