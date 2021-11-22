package edu.msu.nagyjos2.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private final static String LOGIN = "login";
    private final static String USERNAME = "username";
    private final static String PASSWORD = "password";
    private final static String REMEMBER = "remember";

    /**
     * Text input for player1
     */
    private EditText player1;
    /**
     * Text input for player2
     */
    private EditText player2;
    /**
     * The preferences checkbox
     */
    private CheckBox rememberCheck;
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
        rememberCheck = findViewById(R.id.SavePreferencesCheckBox);

        // check preferences
        SharedPreferences settings =
                getSharedPreferences(LOGIN, MODE_PRIVATE);

        if (settings.getBoolean(REMEMBER, false)) { // see if there was a save
            String username = settings.getString(USERNAME, "");
            String password = settings.getString(PASSWORD, "");

            player1.setText(username);
            player2.setText(password);
            rememberCheck.setChecked(true);
        }
        else {
            rememberCheck.setChecked(false);
        }
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

        onRemember(view);

        intent.putExtra("Player1Name", player1_result);
        intent.putExtra("Player2Name", player2_result);
        startActivity(intent);
    }

    /**
     * save the user name and password to device preferences (or not if unchecked)
     * @param view the checkbox view
     */
    public void onRemember(View view) {
        if (!player1_result.equals("") && !player2_result.equals("")) {

            SharedPreferences settings =
                    getSharedPreferences(LOGIN, MODE_PRIVATE);

            SharedPreferences.Editor editor = settings.edit();

            editor.putString(USERNAME, player1_result);
            editor.putString(PASSWORD, player2_result);
            editor.putBoolean(REMEMBER, rememberCheck.isChecked());
            editor.commit();
        }
    }


}