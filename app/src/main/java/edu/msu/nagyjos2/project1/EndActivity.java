package edu.msu.nagyjos2.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import edu.msu.nagyjos2.project1.Cloud.Cloud;

public class EndActivity extends AppCompatActivity {

    /**
     * The name of winner
     */
    private String winner_name;
    /**
     * The name of loser
     */
    private String loser_name;
    /**
     * winner text
     */
    private TextView winner;
    /**
     * loser text
     */
    private TextView loser;

    /**
     * host id
     */
    private String hostid;

    //delete this
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);
        winner_name = getIntent().getExtras().getString("WinnerName");
        loser_name = getIntent().getExtras().getString("LoserName");
        hostid = getIntent().getExtras().getString("HostID");
        winner = (TextView) findViewById(R.id.winnerText);
        String winner_text = getString(R.string.end_winner);
        winner.setText(winner_name + " " + winner_text);
        loser = (TextView) findViewById(R.id.loserText);
        String loser_text = getString(R.string.end_loser);
        loser.setText(loser_text + " " + loser_name);
        delete(hostid);
    }

    /**
     * Create a new game
     */
    public void onNewGame(View view) {
        // go back to main activity (top of activity stack)
        Intent main_act = new Intent(this, MainActivity.class);
        main_act.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(main_act);
    }

    @Override
    /**
     * Press the back button
     */
    public void onBackPressed() {
        // back button sends to main screen
        onNewGame(null);
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