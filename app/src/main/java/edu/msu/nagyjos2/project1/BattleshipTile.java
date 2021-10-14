package edu.msu.nagyjos2.project1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

public class BattleshipTile {

    /**
     * The image for the boat in case it has one
     */
    private Bitmap boat;

    /**
     * The image for the hit marker to draw when the player selects this tile to hit
     */
    private Bitmap hitMarker;

    /**
     * Boolean indicating whether this tile has been hit or not
     */
    private boolean isHit = false;

    /**
     * Boolean indicating whether the tile has a boat
     */
    private boolean hasBoat = false;

    public BattleshipTile(Context context) {

    }

    /**
     * Draw the puzzle piece
     * @param canvas Canvas we are drawing on
     * @param BoardStartX where the top left corner x of the board is relative to the screen
     * @param BoardStartY where the top left corner y of the board is relative to the screen
     * @param tileLength Length of the puzzles sides
     * @param tileNum tile number indicating where on the board we draw it:
     *                0  1  2  3
     *                4  5  6  7
     *                8  9  10 11
     *                12 13 14 15
     */
    public void draw(Canvas canvas, int BoardStartX, int BoardStartY, int tileLength, int tileNum) {

        if (isHit) { // draw boat or hit marker on tile if the tile has been hit

            // set up drawing position here

            if (hasBoat) {

                // draw boat here

            }
            else {

                // draw hit marker here
            }
        }

    }

    public boolean isBoatHit() {
        return isHit && hasBoat;
    }
}
