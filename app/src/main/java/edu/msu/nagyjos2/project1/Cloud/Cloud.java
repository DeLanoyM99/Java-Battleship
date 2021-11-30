package edu.msu.nagyjos2.project1.Cloud;

import android.util.Log;

import java.io.IOException;

import edu.msu.nagyjos2.project1.Cloud.Models.LoginResult;
import edu.msu.nagyjos2.project1.Cloud.Models.SignupResult;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class Cloud {

    public static final String BASE_URL = "https://webdev.cse.msu.edu/~nagyjos2/cse476/battleship/";
    public static final String LOGIN_PATH = "battleship-login.php";
    public static final String SIGNUP_PATH = "battleship-signup.php";

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

    public boolean login(final String username, final String password) {
        BattleshipNetwork service = retrofit.create(BattleshipNetwork.class);
        try {
            Response<LoginResult> response = service.login(username, password).execute();

            // check if signup query failed
            if (!response.isSuccessful()) {
                Log.e("signup", "Failed to login the user, response code is = " + response.code());
                return false;
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
                return false;
            }

            return true;

        } catch (IOException | RuntimeException e) {
            Log.e("signup", "Exception occurred while signing up the user", e);
            return false;
        }
    }

}
