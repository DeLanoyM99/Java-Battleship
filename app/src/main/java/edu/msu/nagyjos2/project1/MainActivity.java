package edu.msu.nagyjos2.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private EditText player1;
    private EditText player2;
    private String player1_result;
    private String player2_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        player1 = findViewById(R.id.Player1NameText);
        player2 = findViewById(R.id.Player2NameText);
    }

    public void onStartGame(View view) {
        Intent intent = new Intent(this, ShipPlacement.class);
        player1_result = player1.getText().toString();
        player2_result = player2.getText().toString();
        intent.putExtra("Player1Name", player1_result);
        intent.putExtra("Player2Name", player2_result);
        startActivity(intent);
        finish();
    }

    public void onSurrenderButtom(View view) {
        Intent intent = new Intent(this, EndActivity.class);
        startActivity(intent);
    }
    public void onHelpDialogue(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }
}