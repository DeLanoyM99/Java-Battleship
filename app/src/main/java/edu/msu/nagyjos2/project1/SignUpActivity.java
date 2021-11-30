package edu.msu.nagyjos2.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import edu.msu.nagyjos2.project1.Cloud.Cloud;

public class SignUpActivity extends AppCompatActivity {

    private EditText usernameField;
    private EditText passwordField;
    private EditText passwordConfirmField;

    private String username;
    private String password;
    private String passwordConfirm;

    /**
     * cloud object to perform networking calls
     */
    private Cloud cloud = new Cloud();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        usernameField = (EditText)findViewById(R.id.signupUsername);
        passwordField = (EditText)findViewById(R.id.signupPassword);
        passwordConfirmField = (EditText)findViewById(R.id.signupPasswordConfirm);
    }

    public void onSignup(View view) {
        // check to make sure password field are the same
        username = usernameField.getText().toString();
        password = passwordField.getText().toString();
        passwordConfirm = passwordConfirmField.getText().toString();

        if (!password.equals(passwordConfirm)) {
            Toast.makeText(this,
                    R.string.invalid_password_diff,
                    Toast.LENGTH_SHORT).show();
        }

        else if (password.length() <= 5) {
            Toast.makeText(this,
                    R.string.invalid_password_len,
                    Toast.LENGTH_SHORT).show();
        }

        else {
            /*
             * Create new thread to sign up user
             */
            new Thread(new Runnable() {

                @Override
                public void run() {
                    // create cloud object and get xml message
                    final boolean result = cloud.signup(username, password);

                    view.post(new Runnable() {

                        @Override
                        public void run() {
                            if (result) { // sign up success
                                signupSuccess(view);
                            } else { // username taken
                                Toast.makeText(view.getContext(),
                                        R.string.username_taken,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }
            }).start();
        }
    }

    private void signupSuccess(View view) {
        // success toast
        Toast.makeText(view.getContext(),
                R.string.signup_success,
                Toast.LENGTH_SHORT).show();

        // go back to main activity and let user login (top of activity stack)
        Intent main_act = new Intent(this, MainActivity.class);
        main_act.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(main_act);
    }




}