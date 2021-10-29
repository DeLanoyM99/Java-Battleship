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

import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private String player1_name;
    private String player2_name;
    private TextView PlayersTurn;

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
        builder.setTitle("Click a square to attack, if you miss a circle will appear. " +
                        "If you hit an opponet's ship, an x will appear.");
        builder.setPositiveButton(android.R.string.ok, null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

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

    }

    private GameView getGameView() { return this.findViewById(R.id.GameView); }

    public void onSurrenderButtom(View view) {
        Intent intent = new Intent(this, EndActivity.class);
        startActivity(intent);
    }
}