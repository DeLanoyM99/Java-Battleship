package edu.msu.nagyjos2.project1;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText name;
    private EditText password;

    private Button Login;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name =(EditText)findViewById(R.id.Player1NameText);
        password =(EditText)findViewById(R.id.Player2NameText);
        Login =(Button)findViewById(R.id.Login);
    }
}
