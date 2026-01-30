package entity

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import tools.aqua.bgw.visual.ColorVisual

/**
 * Color test
 *
 * @constructor Create Color test
 */
class ColorTest {
    /**
     * Test to string
     *
     */
    @Test
    fun testToString() {
        assertEquals("Yellow", Color.YELLOW.toString())
        assertEquals("Blue", Color.BLUE.toString())
        assertEquals("Orange", Color.ORANGE.toString())
        assertEquals("Green", Color.GREEN.toString())
        assertEquals("Purple", Color.PURPLE.toString())
        assertEquals("Black", Color.BLACK.toString())
    }

    /**
     * Test to rgb
     *
     */
    @Test
    fun testToRGB() {
        assertEquals(Color.YELLOW.toRGB(), ColorVisual.YELLOW)
        assertEquals(Color.BLUE.toRGB(), ColorVisual.BLUE)
        assertEquals(Color.ORANGE.toRGB(), ColorVisual.ORANGE)
        assertEquals(Color.GREEN.toRGB(), ColorVisual.GREEN)
        //assertEquals(Color.PURPLE.toRGB(), ColorVisual(183, 0, 255))
        assertEquals(Color.BLACK.toRGB(), ColorVisual.BLACK)

    }
}
