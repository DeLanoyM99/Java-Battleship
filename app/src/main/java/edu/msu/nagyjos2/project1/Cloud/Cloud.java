package edu.msu.nagyjos2.project1.Cloud;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import edu.msu.nagyjos2.project1.Cloud.Models.Lobbies;
import edu.msu.nagyjos2.project1.Cloud.Models.CreateResult;
import edu.msu.nagyjos2.project1.Cloud.Models.DeleteResult;
import edu.msu.nagyjos2.project1.Cloud.Models.LobbyItem;
import edu.msu.nagyjos2.project1.Cloud.Models.LoginResult;
import edu.msu.nagyjos2.project1.Cloud.Models.SignupResult;
import edu.msu.nagyjos2.project1.R;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;


public class Cloud {

    public static final String BASE_URL = "https://webdev.cse.msu.edu/~nagyjos2/cse476/battleship/";
    public static final String LOGIN_PATH = "battleship-login.php";
    public static final String SIGNUP_PATH = "battleship-signup.php";
    public static final String CREATE_PATH = "lobby-create.php";
    public static final String DELETE_PATH = "lobby-delete.php";
    public static final String LOBBY_LOAD_PATH = "lobby-load.php";


    public static class LobbiesAdapter extends BaseAdapter {

        /**
         * The items we display in the list box. Initially this is
         * null until we get items from the server.
         */
        private Lobbies lobbies = new Lobbies(new ArrayList<LobbyItem>());

        public Lobbies getLobbies() { return lobbies; }

        /**
         * Constructor
         */
        public LobbiesAdapter(final View view) {
            // Create a thread to load the catalog
            new Thread(new Runnable() {

                @Override
                public void run() {
                    lobbies = LoadLobbies();
                    view.post(new Runnable() {

                        @Override
                        public void run() {
                            // Tell the adapter the data set has been changed
                            notifyDataSetChanged();
                        }

                    });
                }

            }).start();
        }

        public Lobbies LoadLobbies() {
            BattleshipNetwork service = retrofit.create(BattleshipNetwork.class);
            try {
                Response response = service.getLobbies().execute();

                // check if response failed
                if (!response.isSuccessful()) {
                    Log.e("getLobbies", "Failed to get the lobbies response code = " + response.code());
                    return new Lobbies(new ArrayList<LobbyItem>());
                }
                Lobbies newLobbies = (Lobbies) response.body();

                if (newLobbies.getItems() == null) {
                    newLobbies.setItems(new ArrayList<LobbyItem>());
                }

                return newLobbies;
            } catch (IOException | RuntimeException e) {
                Log.e("getCatalog", "Exception occurred while loading the lobbies", e);
                return new Lobbies(new ArrayList<LobbyItem>());
            }
        }

        @Override
        public int getCount() {
            return lobbies.getItems().size();
        }

        @Override
        public Object getItem(int position) {
            return lobbies.getItems().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lobby_item, parent, false);
            }

            TextView tv = (TextView) view.findViewById(R.id.lobbyNameText);
            tv.setText(lobbies.getItems().get(position).getName());


            return view;
        }
    }

    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .build();

    public boolean signup(final String user, final String password) {
        BattleshipNetwork service = retrofit.create(BattleshipNetwork.class);
        try {
            Response<SignupResult> response = service.signup(user, password).execute();

            // check if signup query failed
            if (!response.isSuccessful()) {
                Log.e("signup", "Failed to sign the user up, response code is = " + response.code());
                return false;
            }

            SignupResult result = response.body();

            // check if user could not be signed up
            if (result.getStatus().equals("no")) {
                if (result.getMsg().equals("user exists")) {
                    Log.e("signup", "user already exists");
                }
                else {
                    Log.e("signup", "insert failed");
                }
                return false;
            }

            return true;

        } catch (IOException | RuntimeException e) {
            Log.e("signup", "Exception occurred while signing up the user", e);
            return false;
        }
    }

    public String login(final String username, final String password) {
        BattleshipNetwork service = retrofit.create(BattleshipNetwork.class);
        try {
            Response<LoginResult> response = service.login(username, password).execute();

            // check if signup query failed
            if (!response.isSuccessful()) {
                Log.e("signup", "Failed to login the user, response code is = " + response.code());
                return "";
            }

            LoginResult result = response.body();

            // check if user could not be signed up
            if (result.getStatus().equals("no")) {
                if (result.getMsg().equals("user")) {
                    Log.e("signup", "user is not valid");
                }
                else {
                    Log.e("signup", "password is wrong");
                }
                return "";
            }

            return result.getUserId();

        } catch (IOException | RuntimeException e) {
            Log.e("signup", "Exception occurred while signing up the user", e);
            return "";
        }
    }

    public boolean lobbyCreate(final String hostID, final String name) {

        BattleshipNetwork service = retrofit.create(BattleshipNetwork.class);
        try {
            Response<CreateResult> response = service.createLobby(hostID, name).execute();
            // check if signup query failed
            if (!response.isSuccessful()) {
                Log.e("signup", "Failed to login the user, response code is = " + response.code());
                return false;
            }

            CreateResult result = response.body();
            // check if lobby could not be created
            if (result.getStatus().equals("no")) {
                if (result.getMessage().equals("insertfail")) {
                    Log.e("insert", "insert failed");
                }
                else {
                    Log.e("userid", "missing userid or name");
                }
                return false;
            }

            return true;

        } catch (IOException | RuntimeException e) {
            Log.e("signup", "Exception occurred while signing up the user", e);
            return false;
        }
    }

    public boolean lobbyDelete(final String id) {
        BattleshipNetwork service = retrofit.create(BattleshipNetwork.class);
        try {
            Response<DeleteResult> response = service.deleteLobby(id).execute();

            // check if request failed
            if (!response.isSuccessful()) {
                Log.e("OpenFromCloud", "Failed to load hat, response code is = " + response.code());
                return false;
            }

            DeleteResult result = response.body();
            if (!result.getStatus().equals("yes")) {
                Log.e("OpenFromCloud", "Failed to delete hat, message is = '" + result.getMessage() + "'");
                return false;
            }

            return true;

        } catch (IOException | RuntimeException e) {
            Log.e("OpenFromCloud", "Exception occurred while deleting hat!", e);
            return false;
        }
    }
}
