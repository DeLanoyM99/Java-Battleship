package edu.msu.nagyjos2.project1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

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
     * The length of each of the tiles sides
     */
    private int tileLength;

    public BattleshipBoard(Context context, GameView view) {
        gameView = view;

        // Create paint for filling the area the board is in
        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setColor(0xff62C3FB);

        // create grid line paint
        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setColor(0xff008ad9);
        gridPaint.setStrokeWidth(3);

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
        tileLength = boardLength / 4;

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

        int tile_num = 0;
        for (BattleshipTile tile : tiles) {
            tile.draw(canvas, marginX, marginY, tileLength, tile_num);
        }

    }

}
