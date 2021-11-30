package edu.msu.nagyjos2.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class LobbyActivity extends AppCompatActivity {

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        username = getIntent().getExtras().getString("Username");
    }
}