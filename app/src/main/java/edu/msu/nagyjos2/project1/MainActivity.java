package edu.msu.nagyjos2.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    /**
     * Text input for player1
     */
    private EditText player1;
    /**
     * Text input for player2
     */
    private EditText player2;
    /**
     * result for player1
     */
    private String player1_result;
    /**
     * result for player2
     */
    private String player2_result;

    private boolean toast_test;

    @Override
    /**
     * Create the game
     * @param savedInstanceState The bundle we get
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        player1 = findViewById(R.id.Player1NameText);
        player2 = findViewById(R.id.Player2NameText);
    }

    /**
     * start the game button
     * @param view The view we get
     */
    public void onStartGame(View view) {
        toast_test = true;
        Intent intent = new Intent(this, ShipPlacement.class);
        player1_result = player1.getText().toString();
        player2_result = player2.getText().toString();
        if (player1_result.length() == 0) {
            player1_result = "Player 1";
        }
        if(player2_result.length() == 0) {
            player2_result = "Player 2";
        }

        if(player1_result.toLowerCase().equals(player2_result.toLowerCase())) {
            toast_test = false;
            /*
             * Create a thread to load the hatting from the cloud
             */
            new Thread(new Runnable() {

                @Override
                public void run() {
                    // Create a cloud object and get the XML

                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(view.getContext(), R.string.player_warning, Toast.LENGTH_SHORT).show();
                        }
                    });


                }

            }).start();
        }
        if (toast_test == true) {
            intent.putExtra("Player1Name", player1_result);
            intent.putExtra("Player2Name", player2_result);
            startActivity(intent);
            finish();
        }
    }


}