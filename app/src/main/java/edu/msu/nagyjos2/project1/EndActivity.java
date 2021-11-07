package edu.msu.nagyjos2.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class EndActivity extends AppCompatActivity {

    private String winner_name;
    private String loser_name;
    private TextView winner;
    private TextView loser;
    //delete this
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);
        winner_name = getIntent().getExtras().getString("WinnerName");
        loser_name = getIntent().getExtras().getString("LoserName");
        winner = (TextView) findViewById(R.id.winnerText);
        String winner_text = getString(R.string.end_winner);
        winner.setText(winner_name + " " + winner_text);
        loser = (TextView) findViewById(R.id.loserText);
        String loser_text = getString(R.string.end_loser);
        loser.setText(loser_text + " " + loser_name);

    }

    public void onNewGame(View view) {
        // go back to main activity (top of activity stack)
        Intent main_act = new Intent(this, MainActivity.class);
        main_act.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(main_act);
    }

    @Override
    public void onBackPressed() {
        // back button sends to main screen
        onNewGame(null);
    }
}