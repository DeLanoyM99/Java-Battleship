package edu.msu.nagyjos2.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private String player1_name;
    private String player2_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        String player1_boat_pos = getIntent().getExtras().getString("player1_boat_positions");
        String player2_boat_pos = getIntent().getExtras().getString("player2_boat_positions");
        player1_name = getIntent().getExtras().getString("player1_name");
        player2_name = getIntent().getExtras().getString("player2_name");

        getGameView().loadBoatPositions(player1_boat_pos, player2_boat_pos);

        Random random = new Random();
        int rand_player = random.nextInt(2); // generate random number between 0 - 1

        // start the game and setup the starting player
        getGameView().setGameStarted(true);
        getGameView().setCurrPlayer(rand_player + 1); // set random starting player (1 or 2)

    }

    private GameView getGameView() { return this.findViewById(R.id.GameView); }

    public void onSurrenderButtom(View view) {
        Intent intent = new Intent(this, EndActivity.class);
        startActivity(intent);
    }
}