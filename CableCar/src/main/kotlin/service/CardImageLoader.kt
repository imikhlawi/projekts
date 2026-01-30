package service

import entity.Color
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

private const val TILES_FILE = "/tiles_assembled.png"
private const val TILES_FILE_IMG_HEIGHT = 800
private const val TILES_FILE_IMG_WIDTH = 800

private const val STATION_CARDS_FILE = "/cardStation.png"
private const val STATION_CARDS_FILE_IMG_HEIGHT = 1080
private const val STATION_CARDS_FILE_IMG_WIDTH = 1080

/**
 * Provides access to the src/main/resources/card_deck.png file that contains all card images
 * in a raster.
 */
class CardImageLoader {

    /**
     * The full raster image containing the suits as rows (plus one special row for blank/back)
     * and values as columns (starting with the ace).
     */

    private val tilesImage : BufferedImage = ImageIO.read(CardImageLoader::class.java.getResource(TILES_FILE))

    private val stationsImage : BufferedImage = ImageIO.read(CardImageLoader::class.java.getResource(STATION_CARDS_FILE))

    /**
     * returns rotated colored car image
     */

    fun stationImage(color: Color, isConnect: Boolean): BufferedImage {
        return if (isConnect) {
            getImageByCoordinates(stationsImage, color.ordinal, 1)
        } else {
            getImageByCoordinates(stationsImage, color.ordinal, 0)
        }
    }

    /**
     * provides indexed texture for tile
     */

    fun frontImage(x: Int,y: Int) = getImageByCoordinates(tilesImage, x, y)


    /**
     * retrieves from the full raster image [image] the corresponding sub-image
     * for the given column [x] and row [y]
     *
     * @param x column in the raster image, starting at 0
     * @param y row in the raster image, starting at 0
     *
     */

    private fun getImageByCoordinates (image: BufferedImage, x: Int, y: Int) : BufferedImage {
        return if (image == tilesImage) image.getSubimage(
            x * TILES_FILE_IMG_WIDTH,y * TILES_FILE_IMG_HEIGHT, TILES_FILE_IMG_WIDTH, TILES_FILE_IMG_HEIGHT)
        else image.getSubimage(
            x * STATION_CARDS_FILE_IMG_WIDTH,y * STATION_CARDS_FILE_IMG_HEIGHT,
            STATION_CARDS_FILE_IMG_WIDTH, STATION_CARDS_FILE_IMG_HEIGHT)
    }

}
