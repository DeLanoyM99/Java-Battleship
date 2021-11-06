package edu.msu.nagyjos2.project1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class BattleshipBoard {

    /**
     * The percentage of the minimum of width or height that is
     * used for the board
     * ex: width is 300px, so the board length on all sides will be
     * SCALE_IN_VIEW * 300.
     */
    final static float SCALE_IN_VIEW = 0.9f;

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

    private ArrayList<Integer> boatPos;

    public ArrayList<Integer> getBoatPos() {
        return boatPos;
    }

    public void setBoatPos(ArrayList<Integer> bp){
        boatPos = bp;
    }

    /**
     * The name of the bundle keys to save the puzzle
     */
    private final static String BOAT = "BattleshipTile.hasBoat";
    private final static String HIT = "BattleshipTile.isHit";
    private final static String NUMBOATS = "BattleshipBoard.numboats";
    private final static String BOATPOS = "BattleshipBoard.boatpositions";


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

    public ArrayList<Integer> getBoatPositions() {
        ArrayList<Integer> pos_arr = new ArrayList<Integer>();

        for (int i = 0; i < 16; i++) {
            if (tiles.get(i).hasBoat()) {
                pos_arr.add(i);
            }
        }
        boatPos = pos_arr;
        return pos_arr;
    }

    public void draw(Canvas canvas) {
        int wid = canvas.getWidth();
        int hit = canvas.getHeight();

        // Determine the minimum of the two dimensions (helps to layout puzzle in landscape and portrait)
        int minDim = Math.min(wid, hit);

        boardLength = (int)(minDim * SCALE_IN_VIEW);

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
     * Handle a release of a touch message.
     * @param relX x location for the touch release, relative to the board - 0 to 1
     * @param relY y location for the touch release, relative to the board - 0 to 1
     * @return true if the touch is handled
     */
    private boolean onReleased(float relX, float relY) {

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
        else {
            if(!tiles.get(boardPos).isTileHit()) { // make sure tile is not already hit
                tiles.get(boardPos).setTileHit();
                if (tiles.get(boardPos).hasBoat()) { // hit a boat
                    numBoats -= 1;
                }
            }
            else { // tile already hit before, send message to user to select another tile

            }
        }

        gameView.invalidate();


        return true;
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

        // check if touch is within board boundaries, if so, perform touch operations
        if ( (0 <= relX && relX <= 1) && (0 <= relY && relY <= 1) ) {
            switch (event.getActionMasked()) {

                case MotionEvent.ACTION_DOWN:
                    return true;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    return onReleased(relX, relY);
            }
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
    public void saveInstanceState(Bundle bundle) {
        getBoatPositions();
        boolean [] boat = new boolean[tiles.size()];
        boolean [] hit = new boolean[tiles.size()];
        int numboats = numBoats;

        for (int i=0; i<tiles.size(); i++){
            BattleshipTile tile = tiles.get(i);
            boat[i] = tile.hasBoat();
            hit[i] = tile.isTileHit();
        }
        bundle.putIntegerArrayList(BOATPOS, boatPos);
        bundle.putBooleanArray(BOAT, boat);
        bundle.putBooleanArray(HIT, hit);
        bundle.putInt(NUMBOATS, numboats);
    }

    /**
     * Read the puzzle from a bundle
     * @param bundle The bundle we save to
     */
    public void loadInstanceState(Bundle bundle) {
        boolean [] boat = bundle.getBooleanArray(BOAT);
        boolean [] hit = bundle.getBooleanArray(HIT);
        numBoats = bundle.getInt(NUMBOATS);
        boatPos = bundle.getIntegerArrayList(BOATPOS);
        for (int i=0; i<tiles.size(); i++){
            BattleshipTile tile = tiles.get(i);
            tile.setHasBoat(boat[i]);
            tile.setHit(hit[i]);
        }
    }

}
