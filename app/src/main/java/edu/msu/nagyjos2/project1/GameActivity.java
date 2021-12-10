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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import edu.msu.nagyjos2.project1.Cloud.Cloud;
import edu.msu.nagyjos2.project1.Cloud.Models.TurnResult;

public class GameActivity extends AppCompatActivity {

    private String player1_name;
    private String player2_name;
    private TextView PlayersTurn;
    private int hostId;
    private boolean isHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        getDoneButton().setEnabled(false);
        String player1_boat_pos = getIntent().getExtras().getString("player1_boat_positions");
        String player2_boat_pos = getIntent().getExtras().getString("player2_boat_positions");
        player1_name = getIntent().getExtras().getString("Player1Name");
        player2_name = getIntent().getExtras().getString("Player2Name");
        isHost = getIntent().getExtras().getBoolean("host");
        hostId = getIntent().getExtras().getInt("idhost");



        getGameView().loadBoatPositions(player1_boat_pos, player2_boat_pos);



        // start the game and setup the starting player
        getGameView().setGameStarted(true);

        // display the current players name to screen
        PlayersTurn = (TextView) findViewById(R.id.PlayersTurn);
        SetNameText(1);

        // set the current player (to display opponents board)
        getGameView().setCurrPlayer(1); //

        if(savedInstanceState != null) {
            // We have saved state
            getGameView().loadInstanceState(savedInstanceState);
            SetNameText(getGameView().getCurrPlayer());
        }
        if (!isHost) {
            disableTouch();
            waitForTurn();
        }



    }

    private GameActivity getActivity() { return this; }

    private void activateTouch() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void disableTouch() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void waitForTurn() {
    final String hostId_final = Integer.toString(hostId);
    final String curr_player = Integer.toString(getGameView().getCurrPlayer());
        new Thread(new Runnable() {

        final Cloud cloud = new Cloud();

        @Override
        public void run() {
            // get the opponents board and load into board class

            while(true) {
                TurnResult result = cloud.waitForTurn(hostId_final, curr_player);

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
                } else {
                    // opponents turn is over
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            int curr_player = getGameView().getCurrPlayer();
                            activateTouch();

                            if (curr_player == 2) {
                                getGameView().setCurrPlayer(1); // set current player to host (player 1)
                                SetNameText(1); // set the name for the host
                                getGameView().loadUpdatedBoard(1, result.getTiles());
                            }
                            else {
                                getGameView().setCurrPlayer(2); // set current player to host (player 1)
                                SetNameText(2); // set the name for the host
                                getGameView().loadUpdatedBoard(2, result.getTiles());
                            }

                            checkForEnd();

                        }
                    });
                }

                return;
            }
        }
    }).start();
}

    public void checkForEnd() {

        if(getGameView().getNumShips(getGameView().getCurrPlayer()) == 0) {
            Intent intent = new Intent(this, EndActivity.class);
            if (getGameView().getCurrPlayer() == 1) {
                intent.putExtra("WinnerName", player2_name);
                intent.putExtra("LoserName", player1_name);
            } else if (getGameView().getCurrPlayer() == 2) {
                intent.putExtra("WinnerName", player1_name);
                intent.putExtra("LoserName", player2_name);
            }
            startActivity(intent);
        }


    }

    public void updateBoard(final int current_player) {

        final String hostId_final = Integer.toString(hostId);
        BattleshipBoard board = getGameView().getPlayerBoard(current_player);

        new Thread(new Runnable() {

            final Cloud cloud = new Cloud();

            @Override
            public void run() {
                boolean result = cloud.updateBoard(hostId_final, board);

                // could not contact server, failed
                if (!result) {
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
                } else {
                    // update complete, begin waiting
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            waitForTurn();
                        }
                    });
                }
            }
        }).start();
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

    public void onMenuPlacement (View view) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(view.getContext());
        builder.setMessage(getString(R.string.game_instructions));
        builder.setPositiveButton(android.R.string.ok, null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        // check to make sure user wants to return to main screen
        BackButtonDlg checkDlg = new BackButtonDlg();
        checkDlg.show(getSupportFragmentManager(), "check");
    }

    public void onSurrenderButton(View view) {
        SurrenderDlg dlg = new SurrenderDlg();
        dlg.show(getSupportFragmentManager(), "surrender");
    }

    public Button getDoneButton() {return this.findViewById(R.id.doneButton); }

    public GameView getGameView() { return this.findViewById(R.id.GameView); }

    public String getPlayer1Name() { return player1_name; }

    public String getPlayer2Name() { return player2_name; }

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

    public void onDoneTurn (View view) {
        // disable button until next player selects tile to hit
        Button done = getDoneButton();
        done.setEnabled(false);
        getGameView().setTurnCompleted(false);

        int nextPlayer = 1 + getGameView().getCurrPlayer() % 2;

        int curr_player = getGameView().getCurrPlayer();

        // check game won
        if(getGameView().getNumShips(nextPlayer) == 0) {
            delete(String.valueOf(hostId));
            Intent intent = new Intent(this, EndActivity.class);
            if (getGameView().getCurrPlayer() == 2) {
                updateBoard(1);
                intent.putExtra("WinnerName", player2_name);
                intent.putExtra("LoserName", player1_name);
            } else if (getGameView().getCurrPlayer() == 1) {
                updateBoard(2);
                intent.putExtra("WinnerName", player1_name);
                intent.putExtra("LoserName", player2_name);
            }
            startActivity(intent);
        }

        else if (curr_player == 1) { // host done - set player to 2 and bring up waiting dlg

            getGameView().setCurrPlayer(2);
            SetNameText(2);

            disableTouch();
            updateBoard(2);



        }

        else { // guest done - send to game activity

            getGameView().setCurrPlayer(1);
            SetNameText(1);

            disableTouch();
            updateBoard(1);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);

        getGameView().saveInstanceState(bundle);
    }

    /**
     * Delete the lobby
     * @param hostId id it will remove from table
     */
    private void delete(final String hostId) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                Cloud cloud = new Cloud();
                boolean ok = cloud.lobbyDelete(hostId);
                boolean okGame = cloud.gameDelete(hostId);
            }

        }).start();
    }
}


