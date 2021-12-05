package edu.msu.nagyjos2.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import edu.msu.nagyjos2.project1.Cloud.Cloud;

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

                Toast.makeText(view.getContext(),
                        "Joined: " + lobbyName + "\nid: " + hostid,
                        Toast.LENGTH_SHORT).show();

                // join game here
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        boolean result = cloud.lobbyJoin(hostid, userId);

                        if (!result) {
                            Toast.makeText(view.getContext(), R.string.lobby_join_fail, Toast.LENGTH_SHORT).show();
                        }

                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(view.getContext(),
                                        "Joined: " + lobbyName + "\nid: " + hostid,
                                        Toast.LENGTH_SHORT).show();

                                // start ship placement here
                                // send hostid and guestid to ship placement
                                // host will create game, and receive the guestid as well
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
        dlg.setLobbyview(view);
        dlg.show(getSupportFragmentManager(), "create");
    }

    public void LoadLobbies() {
        list = (ListView) this.findViewById(R.id.listLobbies);

        // Create an adapter
        adapter = new Cloud.LobbiesAdapter(list);
        list.setAdapter(adapter);


    }
}