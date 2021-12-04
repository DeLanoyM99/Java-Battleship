package edu.msu.nagyjos2.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class LobbyActivity extends AppCompatActivity {

    private String username;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        username = getIntent().getExtras().getString("Username");
        userId = getIntent().getExtras().getString("UserId");
    }

    public void onCreateLobby(View view) {
        CreateDlg dlg = new CreateDlg();
        dlg.setHostId(userId);
        dlg.show(getSupportFragmentManager(), "create");
    }
}