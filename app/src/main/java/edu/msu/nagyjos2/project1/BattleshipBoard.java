package edu.msu.nagyjos2.project1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Class representing a Battleship Board
 */
public class BattleshipBoard {
    /**
     * The name of the bundle keys to save the state of the board
     */
    private final static String BOAT = "BattleshipTile.hasBoat";
    private final static String HIT = "BattleshipTile.isHit";
    private final static String NUM_BOATS = "BattleshipBoard.numboats";
    private static final String GRID_SCALE = "BattleshipBoard.gridScale";

    /**
     * max and min for the scale of the board
     */
    private static final float MIN_SCALE = 0.9f;
    private static final float MAX_SCALE = 2.0f;

    /**
     * The percentage of the minimum of width or height that is
     * used for the board. Min size is 0.9, max size is 2.0.
     * ex: width is 300px, so the board length on all sides will be
     * gridScale * 300.
     */
    private float gridScale = 0.9f;

    /**
     * The length of the board for each of the sides in pixels
     */
    private int boardLength;

    /**
     * Left margin in pixels
     */
    private int marginX;

    /**
     * Top margin in pixels
     */
    private int marginY;

    /**
     * Paint for filling the area the board is in
     */
    private Paint fillPaint;

    /**
     * Paint for outlining the grid area of the board
     */
    private Paint gridPaint;

    /**
     * The view this board will be drawn in
     */
    private GameView gameView;

    /**
     * Collection of tile objects representing a tile on the grid
     */
    public ArrayList<BattleshipTile> tiles = new ArrayList<BattleshipTile>(16);

    /**
     * The number of boats currently on the board
     */
    private int numBoats;

    /**
     * Two touches (at max) that the user may have
     */
    private Touch touch1 = new Touch();
    private Touch touch2 = new Touch();

    /**
     * Set to true when the user places 2 touches on the screen. Helps to not confuse
     * single touch handling with zoom handling.
     */
    private boolean isZooming = false;

    /**
     * The games current playing status: True when the game starts, False when in battleship setup mode
     */
    public boolean gameStarted = false;

    /* ================================= Constructor =================================== */

    public BattleshipBoard(Context context, GameView view) {
        gameView = view;

        // Create paint for filling the area the board is in
        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setColor(0xff62C3FB);

        // create grid line paint
        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setColor(0xff008ad9);
        gridPaint.setStrokeWidth(3);

        for (int i = 0; i < 16; i++) {
            tiles.add(new BattleshipTile(context, i));
        }

    }

    /* =============================== Setters and Getters =================================== */

    /**
     * sets the game to started (now in game activity)
     * @param started true or false
     */
    public void setGameStarted(boolean started) {
        gameStarted = started;
    }

    /**
     * gets the current number of boats on the board (does not count hit boats)
     * @return integer number of boats
     */
    public int getNumBoats() {
        return numBoats;
    }

    /**
     * set the number of boats
     * @param num integer number of boats
     */
    public void setNumBoats(int num) { numBoats = num; }

    /**
     * Gets all the current boat positions into an array
     * @return Array of boat positions
     */
    public ArrayList<Integer> getBoatPositions() {
        ArrayList<Integer> pos_arr = new ArrayList<Integer>();

        for (int i = 0; i < 16; i++) {
            if (tiles.get(i).hasBoat()) {
                pos_arr.add(i);
            }
        }
        return pos_arr;
    }

    /**
     * Sets a boat on a tile
     * @param pos the board position
     */
    public void setBoatPosition(int pos) {
        // place a boat down at this tile (if it has been set)
        if (!tiles.get(pos).hasBoat()) {
            tiles.get(pos).toggleShip();
        }
    }

    /* ================================ Function Methods =================================== */

    /**
     * Draws the board in the view. Each board is drawn as a 4 by 4 grid, each grid tile
     * is represented as a "tile" object. Each tile is drawn based on the current state of the
     * game.
     * @param canvas drawing object
     */
    public void draw(Canvas canvas) {
        int wid = canvas.getWidth();
        int hit = canvas.getHeight();

        // Determine the minimum of the two dimensions (helps to layout puzzle in landscape and portrait)
        int minDim = Math.min(wid, hit);

        boardLength = (int)(minDim * gridScale);

        // Compute the margins so we center the board
        marginX = (wid - boardLength) / 2;
        marginY = (hit - boardLength) / 2;

        //
        // Draw the outline of the board
        //
        canvas.drawRect(marginX, // left boundary of rectangle
                marginY, // top boundary of rectangle
                marginX + boardLength, // right boundary of rectangle
                marginY + boardLength, // bottom boundary of rectangle
                fillPaint); // paint color

        //
        // draw the grid
        //
        int tileLength = boardLength / 4;

        canvas.save();
        canvas.translate(marginX, marginY);

        // draw horizontal lines
        canvas.drawLine(0, tileLength, boardLength, tileLength, gridPaint);
        canvas.drawLine(0, tileLength * 2, boardLength, tileLength * 2, gridPaint);
        canvas.drawLine(0, tileLength * 3, boardLength, tileLength * 3, gridPaint);

        // draw vertical lines
        canvas.drawLine(tileLength, 0, tileLength, boardLength, gridPaint);
        canvas.drawLine(tileLength * 2, 0, tileLength * 2, boardLength, gridPaint);
        canvas.drawLine(tileLength * 3, 0, tileLength * 3, boardLength, gridPaint);

        canvas.restore();

        for (BattleshipTile tile : tiles) {
            if (!gameStarted) {
                tile.drawPlaceMode(canvas, marginX, marginY, tileLength);
            }
            else {
                tile.drawGameMode(canvas, marginX, marginY, tileLength);
            }
        }

    }

    /**
     * Handle a release of a touch message on game board. Places ships and attacks
     * @param relX x location for the touch release, relative to the board - 0 to 1
     * @param relY y location for the touch release, relative to the board - 0 to 1
     * @return true if the touch is handled
     */
    private void onReleased(float relX, float relY) {

        int row = (int)Math.floor(relX * 4);
        int col = (int)Math.floor(relY * 4);

        int boardPos = row + col * 4;

        // handle touch when game in setup mode
        if ((!gameStarted && numBoats < 4) || (!gameStarted && tiles.get(boardPos).hasBoat())) {
            if(tiles.get(boardPos).toggleShip()){
                numBoats += 1;
            }
            else {
                numBoats -= 1;
            }
        }

        // tile selected for attack
        else if (gameStarted && !gameView.getTurnCompleted()){
            if(!tiles.get(boardPos).isTileHit()) { // make sure tile is not already hit

                tiles.get(boardPos).setTileHit(); // draw hit boat
                gameView.setTurnCompleted(true); // tile selected, signal to view to prevent further hits
                gameView.setDoneButton(true); // activate done button (allow user to press it)

                if (tiles.get(boardPos).hasBoat()) { // hit a boat
                    numBoats -= 1;
                }
            }
            else { // tile already hit before, send message to user to select another tile
                Toast.makeText(gameView.getContext(), R.string.tile_already_hit, Toast.LENGTH_LONG).show();

            }
        }
    }

    /**
     * Touch class to hold data for a touch event
     */
    private class Touch {

        /**
         * id of a touch, -1 if touch is not active
         */
        public int id = -1;

        /**
         * current x location of a touch
         */
        public float x = 0;

        /**
         * current y location of a touch
         */
        public float y = 0;

        /**
         * last x location of a touch
         */
        public float lastX = 0;

        /**
         * last y location of a touch
         */
        public float lastY = 0;

        /**
         * copies the current locations to last. Used after a touch has been handled.
         */
        public void copyToLast() {
            lastX = x;
            lastY = y;
        }
    }

    /**
     * Gets the positions for a single touch, updates the current locations and old locations
     * @param event the touch event
     */
    private void getPositions(MotionEvent event) {
        for(int i=0;  i<event.getPointerCount();  i++) {

            // Get the pointer id
            int id = event.getPointerId(i);

            // Convert to board coordinates
            float x = (event.getX(i) - marginX) / gridScale;
            float y = (event.getY(i) - marginY) / gridScale;

            if(id == touch1.id) {
                touch1.copyToLast();
                touch1.x = x;
                touch1.y = y;
            } else if(id == touch2.id) {
                touch2.copyToLast();
                touch2.x = x;
                touch2.y = y;
            }
        }
        gameView.invalidate();
    }

    /**
     * calculate length between 2 touches
     * @param x1 touch1 x
     * @param y1 touch1 y
     * @param x2 touch2 x
     * @param y2 touch2 y
     * @return float for length relative to board
     */
    private float length(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return (float)Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Scales the board based on the touches displacement. (if scale is between min and max)
     */
    private void scaleBoard() {
        float length1 = length(touch1.lastX, touch1.lastY, touch2.lastX, touch2.lastY);
        float length2 = length(touch1.x, touch1.y, touch2.x, touch2.y);

        float scale = gridScale * (length2 / length1);

        if (scale <= MAX_SCALE && scale >= MIN_SCALE) {
            gridScale = scale;
        }
    }

    /**
     * Handle a touch event from the view.
     * @param view The view that is the source of the touch
     * @param event The motion event describing the touch
     * @return true if the touch is handled.
     */
    public boolean onTouchEvent(View view, MotionEvent event) {

        float relX = (event.getX() - marginX) / boardLength;
        float relY = (event.getY() - marginY) / boardLength;

        int id = event.getPointerId(event.getActionIndex());

        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN: // first finger down
                touch1.id = id;
                touch2.id = -1;
                getPositions(event);
                touch1.copyToLast();
                isZooming = false;
                return true;

            case MotionEvent.ACTION_POINTER_DOWN: // second finger down
                if(touch1.id >= 0 && touch2.id < 0) {
                    touch2.id = id;
                    getPositions(event);
                    touch2.copyToLast();
                    isZooming = true;
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP: // last touch released
                if (id == touch2.id) {
                    touch2.id = -1;
                }
                else if (id == touch1.id) {
                    // Make what was touch2 now be touch1 by
                    // swapping the objects.
                    Touch t = touch1;
                    touch1 = touch2;
                    touch2 = t;
                    touch2.id = -1;
                }

                // handle touch (if it is on board and we are not zooming
                if (!isZooming && (0 <= relX && relX <= 1) && (0 <= relY && relY <= 1)) {
                    onReleased(relX, relY);
                    gameView.invalidate();
                }
                return true;

            case MotionEvent.ACTION_CANCEL: // both fingers released
                touch1.id = -1;
                touch2.id = -1;
                gameView.invalidate();
                return true;

            case MotionEvent.ACTION_POINTER_UP: //one finger lifted, one remains on
                return true;

            case MotionEvent.ACTION_MOVE:
                getPositions(event);
                if (touch2.id >= 0) {
                    scaleBoard();
                }
                return true;
        }
        return false;
    }

    /**
     * Save the puzzle to a bundle
     * @param bundle The bundle we save to
     */
    public void saveInstanceState(Bundle bundle, int playerNum) {
        boolean [] boat = new boolean[tiles.size()];
        boolean [] hit = new boolean[tiles.size()];

        for (int i=0; i<tiles.size(); i++){
            boat[i] = tiles.get(i).hasBoat();
            hit[i] = tiles.get(i).isTileHit();
        }

        bundle.putBooleanArray(BOAT + playerNum, boat);
        bundle.putBooleanArray(HIT + playerNum, hit);
        bundle.putInt(NUM_BOATS + playerNum, numBoats);
        bundle.putFloat(GRID_SCALE + playerNum, gridScale);
    }

    /**
     * Read the puzzle from a bundle
     * @param bundle The bundle we save to
     */
    public void loadInstanceState(Bundle bundle, int playerNum) {
        boolean [] boat;
        boolean [] hit;

        numBoats = bundle.getInt(NUM_BOATS + playerNum);
        boat = bundle.getBooleanArray(BOAT + playerNum);
        hit = bundle.getBooleanArray(HIT + playerNum);
        gridScale = bundle.getFloat(GRID_SCALE + playerNum);

        for (int i=0; i<tiles.size(); i++){
            tiles.get(i).setHasBoat(boat[i]);
            tiles.get(i).setHit(hit[i]);
        }
    }

}
