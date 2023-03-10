package edu.msu.nagyjos2.project1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import edu.msu.nagyjos2.project1.Cloud.Cloud;
import edu.msu.nagyjos2.project1.Cloud.Models.TurnResult;

public class GameActivity extends AppCompatActivity {

    private String player1_name;
    private String player2_name;
    private TextView PlayersTurn;
    private int hostId;
    private boolean isHost;
    private Animation animFadeIn;
    private Animation animFadeOut;
    private CountDownTimer timer;
    private boolean timerDone = false;


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
        timer = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                TextView time_remaining = findViewById(R.id.secondsRemaining);
                time_remaining.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                timerDone = true;
                checkForEnd();
            }
        }.start();

        animFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        animFadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);

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
        if (!isHost && getGameView().getCurrPlayer() == 1) {
            getGameView().setTurnCompleted(true);
            disableTouch();
            waitForTurn();
        }
    }

    public CountDownTimer getTimer() {
        return this.timer;
    }

    private GameActivity getGameActivity() { return this; }

    private void activateTouch() {
        Button bttn_surrender = findViewById(R.id.surrenderButton);
        bttn_surrender.setEnabled(true);

        RelativeLayout overlay = (RelativeLayout)findViewById(R.id.waitingOverlay);

        animFadeOut.reset();
        overlay.clearAnimation();
        overlay.startAnimation(animFadeOut);
        overlay.setVisibility(View.INVISIBLE);
    }

    private void disableTouch() {
        Button bttn_surrender = findViewById(R.id.surrenderButton);
        bttn_surrender.setEnabled(false);

        RelativeLayout overlay = (RelativeLayout)findViewById(R.id.waitingOverlay);

        overlay.setVisibility(View.VISIBLE);
        animFadeIn.reset();
        overlay.clearAnimation();
        overlay.startAnimation(animFadeIn);
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
                final TurnResult result = cloud.waitForTurn(hostId_final, curr_player);

                // could not contact server, failed
                if (result.getStatus().equals("fail")) {
                    getGameActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(getGameActivity(),
                                    "Server Error Occurred",
                                    Toast.LENGTH_SHORT).show();

                            // go back to main activity (top of activity stack)
                            Intent main_act = new Intent(getGameActivity(), MainActivity.class);
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
                    getGameActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            int curr_player = getGameView().getCurrPlayer();
                            activateTouch();

                            if (result.getSurrender().equals("yes")) {
                                Intent intent = new Intent(getGameActivity(), EndActivity.class);
                                intent.putExtra("HostID", String.valueOf(hostId));
                                if (getGameView().getCurrPlayer() == 1) {
                                    intent.putExtra("WinnerName", player2_name);
                                    intent.putExtra("LoserName", player1_name);
                                } else if (getGameView().getCurrPlayer() == 2) {
                                    intent.putExtra("WinnerName", player1_name);
                                    intent.putExtra("LoserName", player2_name);
                                }
                                timer.cancel();
                                startActivity(intent);
                                return;
                            }

                            if (curr_player == 2) { // guest moved, see if the guest won the game
                                getGameView().loadUpdatedBoard(1, result.getTiles());;

                                getGameView().setCurrPlayer(1); // set current player to host (player 1)
                                SetNameText(1);

                            }
                            else { // host moved, see if the host won the game
                                getGameView().loadUpdatedBoard(2, result.getTiles());

                                getGameView().setCurrPlayer(2); // set current player to guest
                                SetNameText(2);
                            }
                            getGameView().setTurnCompleted(false);
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

        if(getGameView().getNumShips(getGameView().getCurrPlayer()) == 0 || timerDone) {
            Intent intent = new Intent(this, EndActivity.class);
            intent.putExtra("HostID", String.valueOf(hostId));
            if (getGameView().getCurrPlayer() == 1) {
                intent.putExtra("WinnerName", player2_name);
                intent.putExtra("LoserName", player1_name);
            } else if (getGameView().getCurrPlayer() == 2) {
                intent.putExtra("WinnerName", player1_name);
                intent.putExtra("LoserName", player2_name);
            }
            timer.cancel();
            startActivity(intent);
        }

        ResetTimer();



    }

    public void updateBoard(final int current_player, final boolean surrender) {

        final String hostId_final = Integer.toString(hostId);
        BattleshipBoard board = getGameView().getPlayerBoard(current_player);

        new Thread(new Runnable() {

            final Cloud cloud = new Cloud();

            @Override
            public void run() {
                boolean result;
                if (surrender) {
                    result = cloud.updateBoard(hostId_final, board, true);
                }
                else {
                    result = cloud.updateBoard(hostId_final, board, false);
                }

                // could not contact server, failed
                if (!result) {
                    getGameActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(getGameActivity(),
                                    "Something went wrong",
                                    Toast.LENGTH_SHORT).show();

                            // go back to main activity (top of activity stack)
                            Intent main_act = new Intent(getGameActivity(), MainActivity.class);
                            main_act.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(main_act);
                        }
                    });
                } else {
                    // update complete, begin waiting
                    getGameActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (!surrender && getGameView().getNumShips(1 + getGameView().getCurrPlayer() % 2) != 0) {
                                waitForTurn();
                            }
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

    public void ResetTimer() {
        timer.cancel();
        timer = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                TextView time_remaining = findViewById(R.id.secondsRemaining);
                time_remaining.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                timerDone = true;
                checkForEnd();
            }
        }.start();
    }

    public void onDoneTurn (View view) {
        ResetTimer();

        // disable button until next player selects tile to hit
        Button done = getDoneButton();
        done.setEnabled(false);


        int nextPlayer = 1 + getGameView().getCurrPlayer() % 2;

        int curr_player = getGameView().getCurrPlayer();

        // check game won
        if(getGameView().getNumShips(nextPlayer) == 0) {
            Intent intent = new Intent(this, EndActivity.class);
            intent.putExtra("HostID", "");
            if (getGameView().getCurrPlayer() == 2) {
                updateBoard(1, false);
                intent.putExtra("WinnerName", player2_name);
                intent.putExtra("LoserName", player1_name);
            } else if (getGameView().getCurrPlayer() == 1) {
                updateBoard(2, false);
                intent.putExtra("WinnerName", player1_name);
                intent.putExtra("LoserName", player2_name);
            }
            timer.cancel();
            startActivity(intent);
        }

        else if (curr_player == 1) { // host done

            getGameView().setCurrPlayer(2);
            SetNameText(2);

            disableTouch();
            updateBoard(2, false);

        }

        else { // guest done

            getGameView().setCurrPlayer(1);
            SetNameText(1);

            disableTouch();
            updateBoard(1, false);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);

        getGameView().saveInstanceState(bundle);
    }

    /**
     * Delete the lobby & game
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


