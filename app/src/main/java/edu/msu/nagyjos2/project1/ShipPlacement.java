package edu.msu.nagyjos2.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ShipPlacement extends AppCompatActivity {

    private String player1_name;
    private String player2_name;

    //GameView gameView = this.findViewById(R.id.GameView);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ship_placement);
        //gameView.setCurrentPlayer(1); --> do later
        player1_name = getIntent().getExtras().getString("Player1Name");
        player2_name = getIntent().getExtras().getString("Player2Name");
    }

    public void onDonePlacement (View view) {

        // check numships from BattleshipBoard
        // if num ships < 4
        // dialog
        // else intent
        AlertDialog.Builder builder =
                new AlertDialog.Builder(view.getContext());
        builder.setTitle("Boat Placement");
        builder.setMessage("Make sure you place all 4 boats before pressing done!");
        builder.setPositiveButton(android.R.string.ok, null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

//        Intent intent = new Intent(this, GameActivity.class);
//        startActivity(intent);
    }
}