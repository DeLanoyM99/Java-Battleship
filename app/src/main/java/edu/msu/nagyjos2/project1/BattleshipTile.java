package edu.msu.nagyjos2.project1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class BattleshipTile {

    /**
     * The image for the boat in case it has one
     */
    private Bitmap boat;

    /**
     * The image for the circle marker to draw when the player selects this tile to hit
     */
    private Bitmap circleMarker;

    /**
     * The image for the hit marker to draw when the player selects this tile to hit and has a boat
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

    /**
     * int number of tile
     */
    private int tileNum;

    /**
     * create the battleship tile
     * @param context context we get
     * @param tileNumber number of battle ship
     */
    public BattleshipTile(Context context, int tileNumber) {
        tileNum = tileNumber;
        boat = BitmapFactory.decodeResource(context.getResources(), R.drawable.ship);
        circleMarker = BitmapFactory.decodeResource(context.getResources(), R.drawable.missmarker);
        hitMarker = BitmapFactory.decodeResource(context.getResources(), R.drawable.hitmarker);
    }

    /**
     * create location of board
     * @param BoardStartX X location of board
     * @param BoardStartY y location of board
     * @param tileLength length of battle title
     */
    private void setUpdraw(Canvas canvas, int BoardStartX, int BoardStartY, int tileLength) {
        int startX = BoardStartX + tileLength * (tileNum % 4) + tileLength / 2;
        int startY = BoardStartY + tileLength * (tileNum / 4) + tileLength / 2;
        float scaleFactor = (float)((0.7 * tileLength) / boat.getWidth());

        canvas.save();

        // Convert x,y to pixels and add the margin, then draw
        canvas.translate(startX, startY);

        // Scale it to the right size
        canvas.scale(scaleFactor, scaleFactor);

        // center of the boat at 0, 0
        canvas.translate(-boat.getWidth() / 2f, -boat.getHeight() / 2f);
    }

    /**
     * Draw the tile
     * @param canvas Canvas we are drawing on
     * @param BoardStartX where the top left corner x of the board is relative to the screen
     * @param BoardStartY where the top left corner y of the board is relative to the screen
     * @param tileLength Length of the puzzles sides
     * tile locations:
     *                    0  1  2  3
     *                    4  5  6  7
     *                    8  9  10 11k
     *                    12 13 14 15
     */
    public void drawGameMode(Canvas canvas, int BoardStartX, int BoardStartY, int tileLength) {

        if (isHit) { // draw only if the tile has been hit
            setUpdraw(canvas, BoardStartX, BoardStartY, tileLength);
            if (hasBoat) {
                canvas.drawBitmap(boat, 0, 0, null);

                canvas.translate((boat.getWidth() / 2f) - (hitMarker.getWidth() / 2f),
                        (boat.getHeight() / 2f) - (hitMarker.getHeight() / 2f));
                canvas.drawBitmap(hitMarker, 0, 0, null);
            }
            else {
                canvas.translate((boat.getWidth() / 2f) - (circleMarker.getWidth() / 2f),
                        (boat.getHeight() / 2f) - (circleMarker.getHeight() / 2f));
                canvas.drawBitmap(circleMarker, 0, 0, null);
            }
            canvas.restore();
        }

    }

    public void drawPlaceMode(Canvas canvas, int BoardStartX, int BoardStartY, int tileLength) {

        if (hasBoat) {
            setUpdraw(canvas, BoardStartX, BoardStartY, tileLength);

            // Draw the bitmap
            canvas.drawBitmap(boat, 0, 0, null);

            canvas.restore();
        }

    }

    /**
     * adds or removes a ship
     * returns true if ship is placed
     * @return true if placed ship, false if removed ship
     */
    public boolean toggleShip() {
        hasBoat = !hasBoat;
        return hasBoat;
    }

    public void setHit(boolean hit) {
        isHit = hit;
    }

    public void setHasBoat(boolean hasBoat) {
        this.hasBoat = hasBoat;
    }

    public boolean isTileHit() { return isHit; }

    public void setTileHit() { if (!isHit) { isHit = true; } }

    public boolean hasBoat() { return hasBoat; }
}
