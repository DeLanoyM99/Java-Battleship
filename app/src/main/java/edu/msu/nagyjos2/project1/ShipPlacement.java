package edu.msu.nagyjos2.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ShipPlacement extends AppCompatActivity {

    private String player1_name;
    private String player2_name;
    private TextView PlayersTurn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ship_placement);

        //gameView.setCurrentPlayer(1);

        player1_name = getIntent().getExtras().getString("Player1Name");
        player2_name = getIntent().getExtras().getString("Player2Name");
        PlayersTurn = (TextView) findViewById(R.id.PlayerTurnText);
        PlayersTurn.setText(player1_name + "\'s" + " turn");
    }

    private GameView getGameView() { return this.findViewById(R.id.GameViewShip); }

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
            PlayersTurn.setText(player2_name + "\'s" + " turn");
        }
        // if player 2 done, we go to game activity
        else {
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
        }



//
    }
}