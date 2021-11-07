package edu.msu.nagyjos2.project1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

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


    /**
     * The name of the bundle keys to save the puzzle
     */
    private final static String BOAT1 = "BattleshipTile.hasBoat1";
    private final static String HIT1 = "BattleshipTile.isHit1";
    private final static String NUMBOATS1 = "BattleshipBoard.numboats1";
    private final static String BOAT2 = "BattleshipTile.hasBoat2";
    private final static String HIT2 = "BattleshipTile.isHit2";
    private final static String NUMBOATS2 = "BattleshipBoard.numboats2";
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
        else if (gameStarted){
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
    public void saveInstanceState(Bundle bundle, int playerNum) {
        //getBoatPositions();
        boolean [] boat = new boolean[tiles.size()];
        boolean [] hit = new boolean[tiles.size()];

        for (int i=0; i<tiles.size(); i++){
            boat[i] = tiles.get(i).hasBoat();
            hit[i] = tiles.get(i).isTileHit();
        }

        if (playerNum == 1) {
            bundle.putBooleanArray(BOAT1, boat);
            bundle.putBooleanArray(HIT1, hit);
            bundle.putInt(NUMBOATS1, numBoats);
        }
        else {
            bundle.putBooleanArray(BOAT2, boat);
            bundle.putBooleanArray(HIT2, hit);
            bundle.putInt(NUMBOATS2, numBoats);
        }
        //bundle.putIntegerArrayList(BOATPOS, boatPos);

    }

    /**
     * Read the puzzle from a bundle
     * @param bundle The bundle we save to
     */
    public void loadInstanceState(Bundle bundle, int playerNum) {
        boolean [] boat;
        boolean [] hit;
        if (playerNum == 1) {
            numBoats = bundle.getInt(NUMBOATS1);
            boat = bundle.getBooleanArray(BOAT1);
            hit = bundle.getBooleanArray(HIT1);
        }
        else {
            numBoats = bundle.getInt(NUMBOATS2);
            boat = bundle.getBooleanArray(BOAT2);
            hit = bundle.getBooleanArray(HIT2);
        }
        //boatPos = bundle.getIntegerArrayList(BOATPOS);
        for (int i=0; i<tiles.size(); i++){
            tiles.get(i).setHasBoat(boat[i]);
            tiles.get(i).setHit(hit[i]);
        }
    }

}
