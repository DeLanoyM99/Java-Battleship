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

import java.util.Random;

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
        isHost = getIntent().getExtras().getString("isHost").equals("yes");
        hostId = Integer.parseInt(getIntent().getExtras().getString("hostId"));

        getGameView().loadBoatPositions(player1_boat_pos, player2_boat_pos);

        // start the game and setup the starting player
        getGameView().setGameStarted(true);

        // display the current players name to screen
        PlayersTurn = (TextView) findViewById(R.id.PlayersTurn);
        SetNameText(1);

        // set the current player (to display opponents board)
        getGameView().setCurrPlayer(1); // set random starting player (1 or 2)

        if(savedInstanceState != null) {
            // We have saved state
            getGameView().loadInstanceState(savedInstanceState);
            SetNameText(getGameView().getCurrPlayer());
        }
        if (!isHost) {
            disableTouch();
            waitForUpdate();
        }
    }


    private void activateTouch() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void disableTouch() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void update(final int current_player) {}

    public void waitForUpdate() {}

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

        int nextPlayer = 1 + getGameView().getCurrPlayer() % 2;
        int curr_player = getGameView().getCurrPlayer();

        // check game won
        if(getGameView().getNumShips(nextPlayer) == 0) {
            Intent intent = new Intent(this, EndActivity.class);
            if (getGameView().getCurrPlayer() == 2) {
                intent.putExtra("WinnerName", player2_name);
                intent.putExtra("LoserName", player1_name);
            } else if (getGameView().getCurrPlayer() == 1) {
                intent.putExtra("WinnerName", player1_name);
                intent.putExtra("LoserName", player2_name);
            }
            startActivity(intent);
        }
        else if (curr_player == 1) { // host done - set player to 2 and bring up waiting dlg
            if (isHost) {
                // update host board
                update(curr_player);
                disableTouch();
            }
            else {
                activateTouch();
            }
            getGameView().setCurrPlayer(2);
            SetNameText(2);
            getGameView().setTurnCompleted(false);
        }

        else { // guest done - set player to 1 and bring up waiting dlg
            if (isHost) {
                activateTouch();
            }
            else {
                // update guest board
                update(curr_player);
                disableTouch();
            }
            getGameView().setCurrPlayer(1);
            SetNameText(1);
            getGameView().setTurnCompleted(false);
        }

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);

        getGameView().saveInstanceState(bundle);
    }
}