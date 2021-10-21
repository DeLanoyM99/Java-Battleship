package edu.msu.nagyjos2.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onStartGame(View view) {
        Intent intent = new Intent(this, ShipPlacement.class);
        startActivity(intent);
    }

    public void onSurrenderButtom(View view) {
        Intent intent = new Intent(this, EndActivity.class);
        startActivity(intent);
    }
}