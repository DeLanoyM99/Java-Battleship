package edu.msu.nagyjos2.project1;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import edu.msu.nagyjos2.project1.Cloud.Models.Tile;

/**
 * The main view of the battleship game. Provides support for 2 players.
 */
public class GameView extends View {

    /**
     * State parameters to save/load data
     */
    private final static String CURRPLAYER = "GameView.currplayer";
    private final static String GAMESTATE = "GameView.gameStarted";
    private final static String TURNSTATE = "GameView.turnCompleted";

    /**
     * Current Game supports 2 boards (2 players)
     */
    private BattleshipBoard player_1_Board;
    private BattleshipBoard player_2_Board;

    /**
     * True is the game has started (game activity)
     */
    private boolean gameStarted = false;

    /**
     * The current player (1 or 2). Used to know whose turn it is and what board to show
     */
    private int currPlayer = 1;

    /**
     * True is the current players turn is completed. Used in game activity to prevent player
     * from attacking more than 1 tile
     */
    private boolean turnCompleted = false;


    /* ================================= Constructors =================================== */

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

    /* =============================== Setters and Getters =================================== */

    /**
     * Tells the view and each board that the game has started (changes how things are drawn)
     * @param started True is the game is started
     */
    public void setGameStarted(boolean started) {
        gameStarted = started;
        player_1_Board.setGameStarted(started);
        player_2_Board.setGameStarted(started);
    }

    /**
     * Activates the done button outside this view in the game activity (only called in
     * game activity). The done button is enabled when a player has attacked a tile.
     * @param bool True is button should be enabled
     */
    public void setDoneButton(boolean bool) {
        ((GameActivity)getContext()).findViewById(R.id.doneButton).setEnabled(bool);
    }

    /**
     * get the current player
     * @return 1 or 2
     */
    public int getCurrPlayer() { return currPlayer; }

    /**
     * set the current player. Meaning we also need to invalidate to draw the correct board
     * @param playerNum 1 or 2
     */
    public void setCurrPlayer(int playerNum) {
        currPlayer = playerNum;
        invalidate();
    }

    /**
     * Sets to True whenever a player finishes their turn in game activity
     * @param completed True or False
     */
    public void setTurnCompleted(boolean completed) { turnCompleted = completed; }

    /**
     * gets turn completed
     * @return True or False
     */
    public boolean getTurnCompleted() { return turnCompleted; }

    /**
     * Gets the number of ships the provided player currently has available (hit ships do not count)
     * @param playerNum 1 or 2
     * @return
     */
    public int getNumShips(int playerNum) {
        return (playerNum == 1) ? player_1_Board.getNumBoats() : player_2_Board.getNumBoats();
    }

    /* ================================ Function Methods =================================== */

    /**
     * deciding which board to draw has 2 condition:
     *         1. whose turn it is (currPlayer)
     *         2. what part of the game we are at (ship placement or game)
     *
     *         if we are in the actual game, display the opponents board, if we are in
     *         ship placement, display the current players board.
     *
     * @param canvas the drawing object
     */
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if ((!gameStarted && currPlayer == 1) || (gameStarted && currPlayer == 2)) {  // player 1 board draw
            player_1_Board.draw(canvas);
        }
        else {  // player 2 board draw
            player_2_Board.draw(canvas);
        }
    }

    /**
     * Deciding which board was touched has same principles as draw function
     * @param event the touch event
     * @return true if touch event was handled (always)
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if ((!gameStarted && currPlayer == 1) || (gameStarted && currPlayer == 2)) {
            return player_1_Board.onTouchEvent(this, event);
        }
        else {
            return player_2_Board.onTouchEvent(this, event);
        }
    }

    /**
     * Gets the boat positions for each board. These are the boats set in ship placement
     * and are used in Game Activity. This function assists with gathering all the boats
     * into 1 array to carry over the data.
     * @return Array of all boat positions. First 4 are player 1 boats, next 4 are player 2 boats
     */
    public ArrayList<Integer> getBoatPositions() {
        ArrayList<Integer> player1_boats = player_1_Board.getBoatPositions();
        ArrayList<Integer> player2_boats = player_2_Board.getBoatPositions();
        player1_boats.addAll(player2_boats);
        return player1_boats;
    }

    /**
     * Takes 2 string sequences of integers. Each sequence is 4 integers long, representing
     * the board position the boats were placed on. This function loads the data from ship
     * placement back into the boards for game activity.
     * @param player1_boats String of player 1 boats
     * @param player2_boats String of player 2 boats
     */
    public void loadBoatPositions(String player1_boats, String player2_boats) {
        // load in each players selected boats to their game boards
        // positions are coming in as strings, must convert
        try {
            for (String player1_pos : player1_boats.split(" ")) {
                int pos = Integer.parseInt(player1_pos);
                player_1_Board.setBoatPosition(pos);
            }
            player_1_Board.setNumBoats(4);

            for (String player2_pos : player2_boats.split(" ")) {
                int pos = Integer.parseInt(player2_pos);
                player_2_Board.setBoatPosition(pos);
            }
            player_2_Board.setNumBoats(4);

        } catch (NumberFormatException ex) {
            Log.d("Error: ", "Cannot convert int to string");
        }

    }

    /**
     * Save the puzzle to a bundle
     * @param bundle The bundle we save to
     */
    public void saveInstanceState(Bundle bundle) {
        bundle.putInt(CURRPLAYER, currPlayer); // save whose turn it is
        bundle.putBoolean(GAMESTATE, gameStarted); // save which game mode were in
        bundle.putBoolean(TURNSTATE, turnCompleted); // save whether the player has hit a tile yet

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
        player_1_Board.setGameStarted(gameStarted);
        player_2_Board.setGameStarted(gameStarted);
        turnCompleted = bundle.getBoolean(TURNSTATE); // load turn completed status

        // set the button (make sure we are in game activity)
        if (gameStarted) {
            setDoneButton(turnCompleted);
        }

        player_1_Board.loadInstanceState(bundle, 1); // re-load player 1's board
        player_2_Board.loadInstanceState(bundle, 2); // re-load player 2's board

        invalidate(); // draw correct board immediately
    }

    /* ================================ Network Functions =================================== */

    public void loadUpdatedBoard(int playerNum, List<Tile> tiles) {

    }
}