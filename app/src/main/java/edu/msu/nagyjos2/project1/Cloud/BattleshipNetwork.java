package edu.msu.nagyjos2.project1.Cloud;

import static edu.msu.nagyjos2.project1.Cloud.Cloud.CREATE_PATH;
import static edu.msu.nagyjos2.project1.Cloud.Cloud.DELETE_PATH;
import static edu.msu.nagyjos2.project1.Cloud.Cloud.LOGIN_PATH;
import static edu.msu.nagyjos2.project1.Cloud.Cloud.SIGNUP_PATH;

import edu.msu.nagyjos2.project1.Cloud.Models.CreateResult;
import edu.msu.nagyjos2.project1.Cloud.Models.DeleteResult;
import edu.msu.nagyjos2.project1.Cloud.Models.LoginResult;
import edu.msu.nagyjos2.project1.Cloud.Models.SignupResult;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
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

    @GET(CREATE_PATH)
    Call<CreateResult> createLobby(
            @Query("userid") String userId,
            @Query("name") String name
            );

    @GET(DELETE_PATH)
    Call<DeleteResult> deleteLobby(
            @Query("userid") String userId
    );


}
