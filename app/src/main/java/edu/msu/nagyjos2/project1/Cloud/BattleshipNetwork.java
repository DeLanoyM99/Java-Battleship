package edu.msu.nagyjos2.project1.Cloud;

import static edu.msu.nagyjos2.project1.Cloud.Cloud.LOGIN_PATH;
import static edu.msu.nagyjos2.project1.Cloud.Cloud.SIGNUP_PATH;

import edu.msu.nagyjos2.project1.Cloud.Models.LoginResult;
import edu.msu.nagyjos2.project1.Cloud.Models.SignupResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BattleshipNetwork {

    @GET(SIGNUP_PATH)
    Call<SignupResult> signup(
            @Query("user") String username,
            @Query("password") String password
    );

    @GET(LOGIN_PATH)
    Call<LoginResult> login(
            @Query("user") String username,
            @Query("password") String password
    );
}
