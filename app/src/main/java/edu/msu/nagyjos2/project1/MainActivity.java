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
        new Thread(new Runnable() {

            @Override
            public void run() {
                // create cloud object and get xml message
                username = usernameView.getText().toString().trim();
                password = passwordView.getText().toString().trim();
                final String id = cloud.login(username, password);

                view.post(new Runnable() {

                    @Override
                    public void run() {
                        if (id != "") { // user is valid
                            GoToLobby(id, view);
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
     * go to the lobby (after account logged in)
     * @param view The view we get
     */
    private void GoToLobby(String id, View view) {
        Intent intent = new Intent(this, LobbyActivity.class);

        onRemember(view);

        intent.putExtra("Username", username);
        intent.putExtra( "UserId", id);
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