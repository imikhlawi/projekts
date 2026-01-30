package view

import service.AbstractRefreshingService

/**
 * This interface provides a mechanism for the service layer classes to communicate
 * (usually to the view classes) that certain changes have been made to the entity
 * layer, so that the user interface can be updated accordingly.
 *
 * Default (empty) implementations are provided for all methods, so that implementing
 * UI classes only need to react to events relevant to them.
 *
 * @see AbstractRefreshingService
 *
 */

interface Refreshable {

    /**
     * perform refreshes that are necessary after a new game started
     */
    fun refreshAfterStartGame() {}

    /**
     * perform refresh if draw stack empty
     */

    fun refreshAfterDrawStackEmpty() {}

    /**
     * perform refresh when network player joins in host lobby
     */

    fun refreshAfterPlayerJoinedInWaitSession(playerName:String){}

    /**
     * perform refresh when network player leaves in host lobby
     */

    fun refreshAfterPlayerLeftInWaitSession(playerName:String){}

    /**
     * workaround to route call to CCApplication
     */

    fun playNopeSoundInCCApp(){}

    /**
     * Refresh after current player placed tile or undo or redo. next player is called right beforehand
     */

    fun refreshAfterTurn(){}

    /**
     * refreshes after game is finished
     */

    fun refreshAfterGameFinished(){}

    /*/**
     * Refresh game config except player name and numbers
     */

    fun refreshAfterRestartGame(playerNames: MutableList<Player>){}*/

    /**
     * refreshes after trying to place a tile, if is not accept
     */
    fun refreshAfterTryingPlaceTile(){}
}