package edu.msu.nagyjos2.project1;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

/**
 * TODO: document your custom view class.
 */
public class GameView extends View {

    private BattleshipBoard player_1_Board;
    private BattleshipBoard player_2_Board; // need later on when we have 2 players
    private boolean gameStarted = false;  // will probably need this later
    private int currPlayer = 1; // to know whose turn it is and what board to show -> implement later
    private final static String CURRPLAYER = "GameView.currplayer";
    private final static String GAMESTATE = "GameView.gameStarted";
    private boolean hasAttacked = false;




    public GameView(Context context) {
        super(context);
        init(null, 0);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        player_1_Board = new BattleshipBoard(getContext(), this);
        player_2_Board = new BattleshipBoard(getContext(), this);
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean started) {
        gameStarted = started;
        player_1_Board.setGameStarted(started);
        player_2_Board.setGameStarted(started);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /*
        deciding which board to draw has 2 condition:
        1. whose turn it is (currPlayer)
        2. what part of the game we are at (ship placement or game)

        if we are in the actual game, display the opponents board, if we are in
        ship placement, display the current players board.
        */

        if ((!gameStarted && currPlayer == 1) || (gameStarted && currPlayer == 2)) {  // player 1 board draw
            player_1_Board.draw(canvas);
        }
        else {  // player 2 board draw
            player_2_Board.draw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!gameStarted) {
            return onPlacementTouch(event);
        }
        else {
            return onGameTouch(event);
        }
    }

    public boolean onGameTouch(MotionEvent event) {
        boolean handled;
        if (currPlayer == 1) { // player 1's turn
            handled = player_2_Board.onTouchEvent(this, event);
            if (player_2_Board.getNumBoats() == 0) {
                // player 1 winner

            }
        }
        else { // player 2's turn
            handled = player_1_Board.onTouchEvent(this, event);
            if (player_1_Board.getNumBoats() == 0) {
                // player 2 winner
            }
        }

        //((Button)findViewById(R.id.doneButton)).setEnabled(true);

        return handled;
    }

    public boolean onPlacementTouch(MotionEvent event) {
        if (currPlayer == 1) {
            return player_1_Board.onTouchEvent(this, event);
        }
        return player_2_Board.onTouchEvent(this, event);
    }

    public int getCurrPlayer() { return currPlayer; }

    public void setCurrPlayer(final int playerNum) {
        currPlayer = playerNum;
        invalidate();
    }

    public int getNumShips(final int playerNum) {
        assert(playerNum == 1 || playerNum == 2);
        if (playerNum == 1) {
            return player_1_Board.getNumBoats();
        }

        else {
            return player_2_Board.getNumBoats();
        }
    }

    public ArrayList<Integer> getBoatPositions() {
        ArrayList<Integer> player1_boats = player_1_Board.getBoatPositions();
        ArrayList<Integer> player2_boats = player_2_Board.getBoatPositions();
        player1_boats.addAll(player2_boats);
        return player1_boats;
    }

    public void loadBoatPositions(String player1_boats, String player2_boats) {
        // load in each players selected boats to their game boards
        // positions are coming in as strings, must convert
        try {
            for (String player1_pos : player1_boats.split(" ")) {
                int pos = Integer.parseInt(player1_pos);
                player_1_Board.loadBoatPosition(pos);
            }

            for (String player2_pos : player2_boats.split(" ")) {
                int pos = Integer.parseInt(player2_pos);
                player_2_Board.loadBoatPosition(pos);
            }
        } catch (NumberFormatException ex) {
            Log.d("Error: ", "Cannot convert in to string");
        }

    }

    /**
     * Save the puzzle to a bundle
     * @param bundle The bundle we save to
     */
    public void saveInstanceState(Bundle bundle) {
        bundle.putInt(CURRPLAYER, currPlayer); // save whose turn it is
        bundle.putBoolean(GAMESTATE, gameStarted); // save which game mode were in
        player_1_Board.saveInstanceState(bundle, 1); // save player 1's board
        player_2_Board.saveInstanceState(bundle, 2); // save player 2's board
    }

    /**
     * Load the puzzle from a bundle
     * @param bundle The bundle we save to
     */
    public void loadInstanceState(Bundle bundle) {
        currPlayer = bundle.getInt(CURRPLAYER); // load current player
        gameStarted = bundle.getBoolean(GAMESTATE); // load the game mode
        player_1_Board.loadInstanceState(bundle, 1); // re-load player 1's board
        player_2_Board.loadInstanceState(bundle, 2); // re-load player 2's board

        invalidate(); // draw correct board immediately
    }
}