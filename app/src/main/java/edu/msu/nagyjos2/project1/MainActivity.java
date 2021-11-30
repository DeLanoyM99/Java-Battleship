package edu.msu.nagyjos2.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import edu.msu.nagyjos2.project1.Cloud.Cloud;

public class MainActivity extends AppCompatActivity {

    private final static String LOGIN = "login";
    private final static String USERNAME = "username";
    private final static String PASSWORD = "password";
    private final static String REMEMBER = "remember";

    /**
     * Text input for player1
     */
    private EditText usernameView;
    /**
     * Text input for player2
     */
    private EditText passwordView;
    /**
     * The preferences checkbox
     */
    private CheckBox rememberCheck;
    /**
     * result for player1
     */
    private String username;
    /**
     * result for player2
     */
    private String password;

    /**
     * cloud object to perform networking calls
     */
    private Cloud cloud = new Cloud();

    @Override
    /**
     * Create the game
     * @param savedInstanceState The bundle we get
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usernameView = findViewById(R.id.usernameTextField);
        passwordView = findViewById(R.id.passwordTextField);
        rememberCheck = findViewById(R.id.SavePreferencesCheckBox);

        // check preferences
        SharedPreferences settings =
                getSharedPreferences(LOGIN, MODE_PRIVATE);

        if (settings.getBoolean(REMEMBER, false)) { // see if there was a save
            String username = settings.getString(USERNAME, "");
            String password = settings.getString(PASSWORD, "");

            usernameView.setText(username);
            passwordView.setText(password);
            rememberCheck.setChecked(true);
        }
        else {
            rememberCheck.setChecked(false);
        }
    }

    /**
     * send the user to the sign up activity
     * @param view
     */
    public void onSignup(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    /**
     * login the user, sends user to lobby activity if valid
     * @param view
     */
    public void onLogin(View view) {
        /*
         * Create new thread to check if user is valid
         */
        MainActivity main = this;
        new Thread(new Runnable() {

            @Override
            public void run() {
                // create cloud object and get xml message
                final boolean result = cloud.login(usernameView.getText().toString(), passwordView.getText().toString());

                view.post(new Runnable() {

                    @Override
                    public void run() {
                        if (result) { // user is valid
                            Intent intent = new Intent(main, LobbyActivity.class);
                            startActivity(intent);
                        }
                        else { // user or password invalid
                            Toast.makeText(view.getContext(),
                                    R.string.invalid_login,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        }).start();
    }

    /**
     *  *****FUNCTION IS DEPRECATED*****
     *
     * start the game button
     * @param view The view we get
     */
    public void onStartGame(View view) {
        Intent intent = new Intent(this, ShipPlacement.class);
        String name1 = usernameView.getText().toString();
        String name2 = passwordView.getText().toString();
        username = name1.equals("") ? "Player 1" : name1; // set name with defaults
        password = name2.equals("") ? "Player 2" : name2;

        onRemember(view);

        intent.putExtra("Player1Name", username);
        intent.putExtra("Player2Name", password);
        startActivity(intent);
    }

    /**
     * save the user name and password to device preferences (or not if unchecked)
     * @param view the checkbox view
     */
    public void onRemember(View view) {
        if (!username.equals("") && !password.equals("")) {

            SharedPreferences settings =
                    getSharedPreferences(LOGIN, MODE_PRIVATE);

            SharedPreferences.Editor editor = settings.edit();

            editor.putString(USERNAME, username);
            editor.putString(PASSWORD, password);
            editor.putBoolean(REMEMBER, rememberCheck.isChecked());
            editor.commit();
        }
    }


}