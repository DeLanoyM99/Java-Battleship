package edu.msu.nagyjos2.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import edu.msu.nagyjos2.project1.Cloud.Cloud;
import edu.msu.nagyjos2.project1.Cloud.Models.JoinResult;

public class LobbyActivity extends AppCompatActivity {

    private String username;
    private String userId;
    private ListView list;
    private Cloud.LobbiesAdapter adapter;
    private Cloud cloud = new Cloud();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        username = getIntent().getExtras().getString("Username");
        userId = getIntent().getExtras().getString("UserId");
        LoadLobbies();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                final String hostid = adapter.getLobbies().getItems().get(position).getId();
                final String lobbyName = adapter.getLobbies().getItems().get(position).getName();

                // join game here
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        JoinResult result = cloud.lobbyJoin(hostid, userId);

                        if (result.getStatus().equals("no")) {
                            Toast.makeText(view.getContext(), R.string.lobby_join_fail, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        final String hostname = result.getHostname();
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(view.getContext(),
                                        "Joined: " + lobbyName,
                                        Toast.LENGTH_SHORT).show();

                                // start ship placement here
                                Intent intent = new Intent(view.getContext(), ShipPlacement.class);
                                intent.putExtra("hostId", hostid);
                                intent.putExtra("guestId", userId);
                                intent.putExtra("guestName", username);
                                intent.putExtra("hostName", hostname);
                                startActivity(intent);
                            }
                        });
                    }
                }).start();
            }
        });


    }

    public void onCreateLobby(View view) {
        CreateDlg dlg = new CreateDlg();
        dlg.setHostId(userId);
        dlg.setHostName(username);
        dlg.setLobbyview(view);
        dlg.show(getSupportFragmentManager(), "create");
    }

    public void onRefresh(View view) {
        LoadLobbies();
    }

    public void LoadLobbies() {
        list = (ListView) this.findViewById(R.id.listLobbies);

        // Create an adapter
        adapter = new Cloud.LobbiesAdapter(list);
        list.setAdapter(adapter);


    }
}