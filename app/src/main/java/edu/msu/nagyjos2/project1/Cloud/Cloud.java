package edu.msu.nagyjos2.project1.Cloud;

import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

import edu.msu.nagyjos2.project1.BattleshipBoard;
import edu.msu.nagyjos2.project1.Cloud.Models.JoinResult;
import edu.msu.nagyjos2.project1.Cloud.Models.Lobbies;
import edu.msu.nagyjos2.project1.Cloud.Models.CreateResult;
import edu.msu.nagyjos2.project1.Cloud.Models.DeleteResult;
import edu.msu.nagyjos2.project1.Cloud.Models.LobbyItem;
import edu.msu.nagyjos2.project1.Cloud.Models.LoginResult;
import edu.msu.nagyjos2.project1.Cloud.Models.SignupResult;
import edu.msu.nagyjos2.project1.Cloud.Models.Tile;
import edu.msu.nagyjos2.project1.Cloud.Models.TurnResult;
import edu.msu.nagyjos2.project1.Cloud.Models.UpdateBoardResult;
import edu.msu.nagyjos2.project1.R;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;


public class Cloud {

    public static final String BASE_URL = "https://webdev.cse.msu.edu/~nagyjos2/cse476/battleship/";
    public static final String LOGIN_PATH = "battleship-login.php";
    public static final String SIGNUP_PATH = "battleship-signup.php";
    public static final String CREATE_PATH = "lobby-create.php";
    public static final String CREATE_WAIT_PATH = "lobby-create-waiting.php";
    public static final String DELETE_PATH = "lobby-delete.php";
    public static final String JOIN_PATH = "lobby-join.php";
    public static final String LOBBY_LOAD_PATH = "lobby-load.php";
    public static final String WAIT_FOR_TURN = "game-waiting.php";
    public static final String TURN_DONE_PATH = "game-update.php";


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

    public CreateResult LobbyWaitForGuest(final String hostID) {
        BattleshipNetwork service = retrofit.create(BattleshipNetwork.class);
        try {
            Response<CreateResult> response = service.waitForGuest(hostID).execute();
            // check if signup query failed
            if (!response.isSuccessful()) {
                Log.e("signup", "Failed to login the user, response code is = " + response.code());
                return new CreateResult("no", "", "", "fail");
            }

            CreateResult result = response.body();
            // check if guest has joined
            if (result.getStatus().equals("no")) {
                if (result.getMessage().equals("unjoined")) {
                    Log.e("waiting", "No one joined yet");
                    return new CreateResult("no", "-1", "", "");
                }
                else { // something went wrong
                    Log.e("waiting", "error - could not retrieve guest");
                    return new CreateResult("no", "", "", "fail");
                }
            }

            return result;

        } catch (IOException | RuntimeException e) {
            Log.e("signup", "Exception occurred while signing up the user", e);
            return new CreateResult("no", "", "", "fail");
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
            Log.e("OpenFromCloud", "Exception occurred while deleting lobby!", e);
            return false;
        }
    }

    public JoinResult lobbyJoin(final String hostid, final String guestid){
        BattleshipNetwork service = retrofit.create(BattleshipNetwork.class);
        try{
            Response<JoinResult> response = service.joinLobby(hostid, guestid).execute();
            // check if request failed
            if (!response.isSuccessful()) {
                Log.e("JoinLobby", "Failed to join lobby, response code is = " + response.code());
                return new JoinResult("no", "", "fail");
            }

            JoinResult result = response.body();
            if (!result.getStatus().equals("yes")) {
                Log.e("JoinLobby", "Failed to join lobby, message is = '" + result.getMessage() + "'");
                return new JoinResult("no", "", "fail");
            }

            return result;

            } catch (IOException | RuntimeException e) {
                Log.e("JoinLobby", "Exception occurred while joining lobby!", e);
                return new JoinResult("no", "", "fail");
            }
    }

    public TurnResult waitForTurn(final String hostid, final String curr_player) {
        BattleshipNetwork service = retrofit.create(BattleshipNetwork.class);
        try {
            Response<TurnResult> response = service.waitForTurn(hostid, curr_player).execute();
            // check if request failed
            if (!response.isSuccessful()) {
                Log.e("waitForTurn", "Failed get response, response code is = " + response.code());
                return new TurnResult("fail", "", new ArrayList<Tile>());
            }

            TurnResult result = response.body();
            if (!result.getStatus().equals("yes")) {
                Log.e("waitForTurn", "Still Waiting");
                return result;
            }

            return result;

        } catch (IOException | RuntimeException e) {
            Log.e("waitForTur", "Exception occurred while waiting!", e);
            return new TurnResult("fail", "", new ArrayList<Tile>());
        }
    }

    public boolean updateBoard(final String hostid, BattleshipBoard board) {
        XmlSerializer xml = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        try {
            xml.setOutput(writer);
            xml.startDocument("UTF-8", true);

            xml.startTag(null, "board");
            xml.attribute(null, "status", "yes");
            for (int i = 0; i<board.tiles.size(); i++) {
                String position = Integer.toString(i);
                String hasBoat = Boolean.toString(board.tiles.get(i).hasBoat());
                String isHit = Boolean.toString(board.tiles.get(i).isTileHit());

                xml.startTag(null, "tile");
                xml.attribute(null, "pos", position);
                xml.attribute(null, "boat", hasBoat);
                xml.attribute(null, "hit", isHit);
                xml.endTag(null, "tile");
            }
            xml.endTag(null, "board");
            xml.endDocument();

        } catch (IOException e) {
            e.printStackTrace();
        }

        BattleshipNetwork service = retrofit.create(BattleshipNetwork.class);
        final String xmlStr = writer.toString();
        try {
            Response<UpdateBoardResult> result = service.updateBoard(hostid, xmlStr).execute();

            if (result.isSuccessful()) {
                UpdateBoardResult result2 = result.body();
                if (result2.getStatus().equals("yes")) {
                    return true;
                }
                Log.e("updateBoard", "Failed to save, message = '" + result2.getMsg() + "'");
                return false;
            }

            Log.e("updateBoard", "Failed to save, message = '" + result.code() + "'");
            return false;

        } catch (IOException e) {
            Log.e("updateBoard", "Exception occurred while trying to save the board", e);
            return false;
        } catch (RuntimeException e) {	// to catch xml errors to help debug step 6
            Log.e("updateBoard", "Runtime Exception: " + e.getMessage());
            return false;
        }
    }
}
