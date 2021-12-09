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
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.msu.nagyjos2.project1.Cloud.Cloud;
import edu.msu.nagyjos2.project1.Cloud.Models.CreateResult;
import edu.msu.nagyjos2.project1.Cloud.Models.Tile;
import edu.msu.nagyjos2.project1.Cloud.Models.TurnResult;

public class ShipPlacement extends AppCompatActivity {

    private final static String ONEDONE = "GameView.oneDone";

    private String hostName = "host"; // default value;
    private String guestName = "guest";  // default value;
    private int hostId;
    private int guestId ;
    private boolean isHost;
    private TextView PlayersTurn;
    private boolean oneDone = false; // true if one player has set their boats


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ship_placement);

        hostId = Integer.parseInt(getIntent().getExtras().getString("hostId"));
        guestId = Integer.parseInt(getIntent().getExtras().getString("guestId"));
        hostName = getIntent().getExtras().getString("hostName");
        guestName = getIntent().getExtras().getString("guestName");
        isHost = getIntent().getExtras().getString("isHost").equals("yes");
        PlayersTurn = findViewById(R.id.PlayerTurnText);

        if(savedInstanceState != null) {
            // We have saved state, load current player
            getGameView().loadInstanceState(savedInstanceState);
            oneDone = savedInstanceState.getBoolean(ONEDONE);

            int curr_player = getGameView().getCurrPlayer();
            SetNameText(curr_player);
        }
        else { // first time creating the activity
            getGameView().setCurrPlayer(1); // set current player to host (player 1)
            SetNameText(1); // set the name for the host
        }

        if (!isHost) {
            disableTouch();
            waitForTurn();
        }
    }

    public boolean isOneDone() {
        return oneDone;
    }

    public void setOneDone(boolean oneDone) {
        this.oneDone = oneDone;
    }

    private GameView getGameView() { return this.findViewById(R.id.GameViewShip); }

    private ShipPlacement getActivity() { return this; }

    @SuppressLint("SetTextI18n")
    private void SetNameText(int current_player) {

        if (hostName.charAt(hostName.length() - 1)== 's' && current_player == 1){
            PlayersTurn.setText(hostName + "'" + " Turn");
        }
        else if (guestName.charAt(guestName.length() - 1)== 's' && current_player == 2){
            PlayersTurn.setText(guestName + "'" + " Turn");
        }
        else{
            if (current_player == 1) {
                PlayersTurn.setText(hostName + "'s" + " Turn");
            }
            else {
                PlayersTurn.setText(guestName + "'s" + " Turn");
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

    private void activateTouch() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void disableTouch() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void waitForTurn() {

        final String hostId_final = Integer.toString(hostId);
        new Thread(new Runnable() {

            final Cloud cloud = new Cloud();

            @Override
            public void run() {
                // get the opponents board and load into board class

                while(true) {
                    TurnResult result = cloud.waitForTurn(hostId_final);

                    // could not contact server, failed
                    if (result.getStatus().equals("fail")) {
                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                Toast.makeText(getActivity(),
                                        "Something went wrong",
                                        Toast.LENGTH_SHORT).show();

                                // go back to main activity (top of activity stack)
                                Intent main_act = new Intent(getActivity(), MainActivity.class);
                                main_act.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(main_act);
                            }
                        });
                    }
                    // still waiting
                    else if (result.getStatus().equals("no")) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }
                    // opponents turn is over
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            int curr_player = getGameView().getCurrPlayer();
                            getGameView().loadUpdatedBoard(curr_player, result.getTiles());
                            activateTouch();

                            if (curr_player == 2) {
                                startGame();
                            }
                            else {
                                getGameView().setCurrPlayer(2); // set current player to host (player 1)
                                SetNameText(2); // set the name for the host
                            }
                        }
                    });

                    return;
                }
            }
        }).start();
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
        intent.putExtra("Player1Name", hostName);
        intent.putExtra("Player2Name", guestName);
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

        else if (curr_player == 1) { // host done - set player to 2 and bring up waiting dlg
            if (isHost) {
                disableTouch();
                waitForTurn();
            }
            else {
                activateTouch();
            }
            getGameView().setCurrPlayer(2);
            SetNameText(2);
        }

        else { // guest done - send to game activity
            if (isHost) {
                activateTouch();
            }

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