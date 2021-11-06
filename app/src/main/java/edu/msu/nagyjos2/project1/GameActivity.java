package edu.msu.nagyjos2.project1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private String player1_name;
    private String player2_name;
    private TextView PlayersTurn;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        String player1_boat_pos = getIntent().getExtras().getString("player1_boat_positions");
        String player2_boat_pos = getIntent().getExtras().getString("player2_boat_positions");
        player1_name = getIntent().getExtras().getString("Player1Name");
        player2_name = getIntent().getExtras().getString("Player2Name");

        getGameView().loadBoatPositions(player1_boat_pos, player2_boat_pos);

        Random random = new Random();
        int rand_player = random.nextInt(2); // generate random number between 0 - 1

        // start the game and setup the starting player
        getGameView().setGameStarted(true);

        // display the current players name to screen
        PlayersTurn = (TextView) findViewById(R.id.PlayersTurn);
        if (rand_player+1 == 1) {
            PlayersTurn.setText(player1_name + "'s" + " Turn");
        }
        else {
            PlayersTurn.setText(player2_name + "'s" + " Turn");
        }

        // set the current player (to display opponents board)
        getGameView().setCurrPlayer(rand_player + 1); // set random starting player (1 or 2)

        if(savedInstanceState != null) {
            // We have saved state
            getGameView().loadInstanceState(savedInstanceState);
        }

    }

    private GameView getGameView() { return this.findViewById(R.id.GameView); }

    public void onSurrenderButtom(View view) {
        Intent intent = new Intent(this, EndActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);

        getGameView().saveInstanceState(bundle);
    }
}