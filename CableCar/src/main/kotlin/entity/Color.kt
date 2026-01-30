package entity

import tools.aqua.bgw.visual.ColorVisual
/**
 * The Color enum class is used to define a fixed set of values, representing the different colors in the game.
 * Each color has a corresponding RGB value defined in the ColorVisual class.
 *
 * @property YELLOW the color yellow
 * @property BLUE the color blue
 * @property ORANGE the color orange
 * @property GREEN the color green
 * @property PURPLE the color purple
 * @property BLACK the color black
 *
 */
enum class Color {
    YELLOW,
    BLUE,
    ORANGE,
    GREEN,
    PURPLE,
    BLACK
    ;
    /**
    toRGB() is used to return the string representation of the Color
     */
    override fun toString() = when(this) {
        YELLOW -> "Yellow"
        BLUE -> "Blue"
        ORANGE -> "Orange"
        GREEN -> "Green"
        PURPLE -> "Purple"
        BLACK -> "Black"
    }
/**
    toRGB() is used to return the RGB values of the color
    */
    fun toRGB() = when(this) {
        YELLOW -> ColorVisual.YELLOW
        BLUE -> ColorVisual.BLUE
        ORANGE -> ColorVisual.ORANGE
        GREEN -> ColorVisual.GREEN
        PURPLE -> ColorVisual(183,0,255)
        BLACK -> ColorVisual.BLACK
    }
}