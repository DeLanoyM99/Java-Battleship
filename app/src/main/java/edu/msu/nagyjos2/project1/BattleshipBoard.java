package edu.msu.nagyjos2.project1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class BattleshipBoard {

    /**
     * The percentage of the minimum of width or height that is
     * used for the board. Min size is 0.5, max size is 2.0.
     * ex: width is 300px, so the board length on all sides will be
     * SCALE_IN_VIEW * 300.
     */
    private float gridScale = 0.9f;

    private static final float MIN_SCALE = 0.5f;
    private static final float MAX_SCALE = 2.0f;
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

    private GameView gameView; // might need this later for saving

    /**
     * Collection of tile objects representing a tile on the grid
     */
    public ArrayList<BattleshipTile> tiles = new ArrayList<BattleshipTile>(16);

    /**
     * The number of boats currently on the board
     */
    private int numBoats;

    private Touch touch1 = new Touch();
    private Touch touch2 = new Touch();
    private boolean isZooming = false;


    /**
     * The name of the bundle keys to save the puzzle
     */
    private final static String BOAT1 = "BattleshipTile.hasBoat1";
    private final static String HIT1 = "BattleshipTile.isHit1";
    private final static String NUMBOATS1 = "BattleshipBoard.numboats1";
    private final static String BOAT2 = "BattleshipTile.hasBoat2";
    private final static String HIT2 = "BattleshipTile.isHit2";
    private final static String NUMBOATS2 = "BattleshipBoard.numboats2";
    private static final String GRIDSCALE1 = "BattleshipBoard.gridScale1";
    private static final String GRIDSCALE2 = "BattleshipBoard.gridScale2";

    private final static String BOAT = "BattleshipTile.hasBoat";
    private final static String HIT = "BattleshipTile.isHit";
    private final static String NUMBOATS = "BattleshipBoard.numboats";
    private static final String GRIDSCALE = "BattleshipBoard.gridScale";


    /**
     * The games current playing status: True when the game starts, False when in battleship setup mode
     */
    public boolean gameStarted = false;

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

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean started) {
        gameStarted = started;
    }

    public int getNumBoats() {
        return numBoats;
    }

    public void setNumBoats(int num) { numBoats = num; }


    public ArrayList<Integer> getBoatPositions() {
        ArrayList<Integer> pos_arr = new ArrayList<Integer>();

        for (int i = 0; i < 16; i++) {
            if (tiles.get(i).hasBoat()) {
                pos_arr.add(i);
            }
        }
        return pos_arr;
    }

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

    private class Touch {

        public int id = -1;
        public float x = 0;
        public float y = 0;
        public float lastX = 0;
        public float lastY = 0;
        public float dX = 0;
        public float dY = 0;

        public void copyToLast() {
            lastX = x;
            lastY = y;
        }
    }

    private void getPositions(MotionEvent event) {
        for(int i=0;  i<event.getPointerCount();  i++) {

            // Get the pointer id
            int id = event.getPointerId(i);

            // Convert to image coordinates
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

    private float length(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return (float)Math.sqrt(dx * dx + dy * dy);
    }

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

    public void loadBoatPosition(int pos) {

        // place a boat down at this tile (if it has been set)
        if (!tiles.get(pos).hasBoat()) {
            tiles.get(pos).toggleShip();
        }
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
        bundle.putInt(NUMBOATS + playerNum, numBoats);
        bundle.putFloat(GRIDSCALE + playerNum, gridScale);
    }

    /**
     * Read the puzzle from a bundle
     * @param bundle The bundle we save to
     */
    public void loadInstanceState(Bundle bundle, int playerNum) {
        boolean [] boat;
        boolean [] hit;

        numBoats = bundle.getInt(NUMBOATS + playerNum);
        boat = bundle.getBooleanArray(BOAT + playerNum);
        hit = bundle.getBooleanArray(HIT + playerNum);
        gridScale = bundle.getFloat(GRIDSCALE + playerNum);

        for (int i=0; i<tiles.size(); i++){
            tiles.get(i).setHasBoat(boat[i]);
            tiles.get(i).setHit(hit[i]);
        }
    }

}
