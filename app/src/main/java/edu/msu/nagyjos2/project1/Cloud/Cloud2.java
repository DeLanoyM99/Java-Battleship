package edu.msu.nagyjos2.project1.Cloud;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import edu.msu.nagyjos2.project1.Cloud.Models.JoinResult;
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


public class Cloud2 {

    public static final String BASE_URL = "https://webdev.cse.msu.edu/~nagyjos2/cse476/battleship/";
    public static final String LOGIN_PATH = "battleship-login.php";
    public static final String SIGNUP_PATH = "battleship-signup.php";
    public static final String CREATE_PATH = "lobby-create.php";
    public static final String DELETE_PATH = "lobby-delete.php";
    public static final String JOIN_PATH = "lobby-join.php";
    public static final String LOBBY_LOAD_PATH = "lobby-load.php";



    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .build();


}

