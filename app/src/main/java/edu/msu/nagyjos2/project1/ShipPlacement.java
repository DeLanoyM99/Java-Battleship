package edu.msu.nagyjos2.project1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class ShipPlacement extends AppCompatActivity {

    private final static String ONEDONE = "GameView.oneDone";

    private String player1_name;
    private String player2_name;
    private TextView PlayersTurn;
    private boolean oneDone = false; // true if one player has set their boats


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ship_placement);

        player1_name = getIntent().getExtras().getString("Player1Name");
        player2_name = getIntent().getExtras().getString("Player2Name");
        PlayersTurn = findViewById(R.id.PlayerTurnText);

        if(savedInstanceState != null) {
            // We have saved state, load current player
            getGameView().loadInstanceState(savedInstanceState);
            oneDone = savedInstanceState.getBoolean(ONEDONE);

            int curr_player = getGameView().getCurrPlayer();
            SetNameText(curr_player);
        }
        else { // first time creating the activity, generate random player
            Random random = new Random();
            int curr_player = random.nextInt(2) + 1; // generate random number between 0 - 1
            getGameView().setCurrPlayer(curr_player); // set current random player
            SetNameText(curr_player); // set the name for the player
        }
    }

    public boolean isOneDone() {
        return oneDone;
    }

    public void setOneDone(boolean oneDone) {
        this.oneDone = oneDone;
    }

    private GameView getGameView() { return this.findViewById(R.id.GameViewShip); }

    @SuppressLint("SetTextI18n")
    private void SetNameText(int current_player) {

        if (player1_name.charAt(player1_name.length() - 1)== 's' && current_player == 1){
            PlayersTurn.setText(player1_name + "'" + " Turn");
        }
        else if (player2_name.charAt(player2_name.length() - 1)== 's' && current_player == 2){
            PlayersTurn.setText(player2_name + "'" + " Turn");
        }
        else{
            if (current_player == 1) {
                PlayersTurn.setText(player1_name + "'s" + " Turn");
            }
            else {
                PlayersTurn.setText(player2_name + "'s" + " Turn");
            }
        }
    }

    @Override
    public void onBackPressed() {
        // check to make sure user wants to return to main screen
        BackButtonDlg checkDlg = new BackButtonDlg();
        checkDlg.show(getSupportFragmentManager(), "check");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_help, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_help:
                onMenuPlacement(getGameView());
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Creates a dialog box for the help option
     * @param view the game view
     */
    public void onMenuPlacement (View view) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(view.getContext());
        builder.setMessage(getString(R.string.placement_instructions));
        builder.setPositiveButton(android.R.string.ok, null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Starts the game activity. Passes each boards information to the next activity
     */
    private void startGame() {
        Intent intent = new Intent(this, GameActivity.class);

        // add in information about each board to send to game activity.
        // game activity needs to know boat positions (4) for each board

        // the boat positions, first 4 elements are player 1's boats, next 4 are player 2's
        ArrayList<Integer> boat_positions = getGameView().getBoatPositions();
        StringBuilder player1_boats = new StringBuilder();
        StringBuilder player2_boats = new StringBuilder();

        for (int i = 0; i < 4; i++) {
            player1_boats.append(boat_positions.get(i).toString() + " ");
        }

        for (int i = 4; i < 7; i++) {
            player2_boats.append(boat_positions.get(i).toString() + " ");
        }
        player2_boats.append(boat_positions.get(7).toString()); // no ending space

        intent.putExtra("player1_boat_positions", player1_boats.toString());
        intent.putExtra("player2_boat_positions", player2_boats.toString());
        intent.putExtra("Player1Name", player1_name);
        intent.putExtra("Player2Name", player2_name);
        startActivity(intent);
    }

    /**
     * Handles a done button press. checks to see if both players have finished placing boats, and
     * starts the game activity if they are.
     * @param view the game view
     */
    @SuppressLint("SetTextI18n")
    public void onDonePlacement (View view) {

        // check if player has all 4 boats placed
        // add pop-up dialog box if not all 4 boats placed
        int curr_player = getGameView().getCurrPlayer();

        if (getGameView().getNumShips(curr_player) < 4) {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(view.getContext());
            builder.setTitle(getString(R.string.placement_done_title));
            builder.setMessage(getString(R.string.placement_done_message));
            builder.setPositiveButton(android.R.string.ok, null);

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        else if (curr_player == 1 && !isOneDone()) { // if player 1 goes first
            setOneDone(true);
            getGameView().setCurrPlayer(2);
            SetNameText(2);
        }

        else if (curr_player == 2 && !isOneDone()) { // if player 2 goes first
            setOneDone(true);
            getGameView().setCurrPlayer(1);
            SetNameText(1);
        }
        // all players done, we go to game activity
        else {
            startGame();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);

        bundle.putBoolean(ONEDONE, oneDone);
        getGameView().saveInstanceState(bundle);
    }

}