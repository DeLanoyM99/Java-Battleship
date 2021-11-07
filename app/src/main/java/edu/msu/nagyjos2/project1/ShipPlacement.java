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

public class ShipPlacement extends AppCompatActivity {

    private String player1_name;
    private String player2_name;
    private TextView PlayersTurn;
    private boolean isclick = false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ship_placement);

        player1_name = getIntent().getExtras().getString("Player1Name");
        player2_name = getIntent().getExtras().getString("Player2Name");
        PlayersTurn = (TextView) findViewById(R.id.PlayerTurnText);



        if(savedInstanceState != null) {
            // We have saved state
            getGameView().loadInstanceState(savedInstanceState);
        }
        int curr_player = getGameView().getCurrPlayer();
        if(curr_player == 1){
            if (player1_name.charAt(player1_name.length() - 1)== 's'){
                PlayersTurn.setText(player1_name + "'" + " Turn");
            }
            else{
                PlayersTurn.setText(player1_name + "'s" + " Turn");
            }
        }
        else{
            if (player2_name.charAt(player2_name.length() - 1)== 's'){
                PlayersTurn.setText(player2_name + "'" + " Turn");
            }
            else{
                PlayersTurn.setText(player2_name + "'s" + " Turn");
            }
           ;
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

    private GameView getGameView() { return this.findViewById(R.id.GameViewShip); }

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

    @SuppressLint("SetTextI18n")
    public void onDonePlacement (View view) {

        // check if player has all 4 boats placed
        // add pop-up dialog box if not all 4 boats placed
        int curr_player = getGameView().getCurrPlayer();

        if (getGameView().getNumShips(curr_player) < 4) {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(view.getContext());
            builder.setTitle("Boat Placement");
            builder.setMessage("Make sure you place all 4 boats before pressing done!");
            builder.setPositiveButton(android.R.string.ok, null);

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        else if (curr_player == 1) {
            getGameView().setCurrPlayer(2);
            if (player1_name.charAt(player2_name.length() - 1)== 's'){
                PlayersTurn.setText(player2_name + "'" + " Turn");
            }
            else{
                PlayersTurn.setText(player2_name + "'s" + " Turn");
            }

        }
        // if player 2 done, we go to game activity
        else {
            startGame();
        }
    }

    public void onMenuPlacement (View view) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(view.getContext());
        builder.setMessage("Click to place a ship inside a grid square. " +
                "Click the same square to remove the ship. " +
                "Place 4 total ships.");
        builder.setPositiveButton(android.R.string.ok, null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);

        getGameView().saveInstanceState(bundle);
    }

//    /**
//     * Save the puzzle to a bundle
//     * @param bundle The bundle we save to
//     */
//    public void saveInstanceState(Bundle bundle) {
//        getGameView().saveInstanceState(bundle);
//    }
//
//    /**
//     * Load the puzzle from a bundle
//     * @param bundle The bundle we save to
//     */
//    public void loadInstanceState(Bundle bundle) {
//        getGameView().loadInstanceState(bundle);
//    }

}