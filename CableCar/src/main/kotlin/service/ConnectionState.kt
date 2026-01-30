package service

/**
 * enum to differentiate Connection statuses
 */

enum class ConnectionState {
    DISCONNECTED,
    CONNECTED,
    WAITING_FOR_JOIN,
    WAITING_FOR_HOST_CONFIRMATION,
    WAITING_FOR_PLAYERS,
    READY_FOR_GAME,
    WAITING_FOR_INIT,
    GAME_INITIALIZED,
    WAITING_FOR_TURN,
    PLAYING_TURN,
    WAITING_FOR_JOIN_CONFIRMATION
}