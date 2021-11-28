package edu.msu.nagyjos2.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class SignUpActivity extends AppCompatActivity {

    private EditText name;
    private EditText password;

    private Button SignUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        name =(EditText)findViewById(R.id.signup_name);
        password =(EditText)findViewById(R.id.signup_password);
        SignUp =(Button)findViewById(R.id.SignUp);
    }




}