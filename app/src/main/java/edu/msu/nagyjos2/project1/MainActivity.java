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
        Intent intent = new Intent(this, ShipPlacement.class);
        String name1 = player1.getText().toString();
        String name2 = player2.getText().toString();
        player1_result = name1.equals("") ? "Player 1" : name1; // set name with defaults
        player2_result = name2.equals("") ? "Player 2" : name2;

        if (player1_result.equals(player2_result)) { // names cannot be the same
            Toast.makeText(view.getContext(), R.string.player_warning, Toast.LENGTH_SHORT).show();
        }

        else {
            intent.putExtra("Player1Name", player1_result);
            intent.putExtra("Player2Name", player2_result);
            startActivity(intent);
        }
    }


}