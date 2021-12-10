package edu.msu.nagyjos2.project1.Cloud;

import static edu.msu.nagyjos2.project1.Cloud.Cloud.CREATE_PATH;
import static edu.msu.nagyjos2.project1.Cloud.Cloud.CREATE_WAIT_PATH;
import static edu.msu.nagyjos2.project1.Cloud.Cloud.DELETE_GAME;
import static edu.msu.nagyjos2.project1.Cloud.Cloud.DELETE_PATH;
import static edu.msu.nagyjos2.project1.Cloud.Cloud.LOGIN_PATH;
import static edu.msu.nagyjos2.project1.Cloud.Cloud.SIGNUP_PATH;
import static edu.msu.nagyjos2.project1.Cloud.Cloud.LOBBY_LOAD_PATH;
import static edu.msu.nagyjos2.project1.Cloud.Cloud.JOIN_PATH;
import static edu.msu.nagyjos2.project1.Cloud.Cloud.TURN_DONE_PATH;
import static edu.msu.nagyjos2.project1.Cloud.Cloud.WAIT_FOR_TURN;

import edu.msu.nagyjos2.project1.Cloud.Models.DeleteGameResult;
import edu.msu.nagyjos2.project1.Cloud.Models.JoinResult;
import edu.msu.nagyjos2.project1.Cloud.Models.Lobbies;
import edu.msu.nagyjos2.project1.Cloud.Models.CreateResult;
import edu.msu.nagyjos2.project1.Cloud.Models.DeleteResult;
import edu.msu.nagyjos2.project1.Cloud.Models.LoginResult;
import edu.msu.nagyjos2.project1.Cloud.Models.SignupResult;
import edu.msu.nagyjos2.project1.Cloud.Models.TurnResult;
import edu.msu.nagyjos2.project1.Cloud.Models.UpdateBoardResult;
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
            @Query("hostid") String hostid,
            @Query("name") String name
            );

    @GET(DELETE_PATH)
    Call<DeleteResult> deleteLobby(
            @Query("hostid") String userId
    );

    @GET(DELETE_GAME)
    Call<DeleteGameResult> deleteGame(
            @Query("hostid") String userId
    );

    @GET(LOBBY_LOAD_PATH)
    Call<Lobbies> getLobbies ();

    @GET(JOIN_PATH)
    Call<JoinResult> joinLobby(
            @Query("hostid") String hostid,
            @Query("guestid") String guestid
    );

    @GET(CREATE_WAIT_PATH)
    Call<CreateResult> waitForGuest(
            @Query("hostid") String hostid
    );

    @GET(WAIT_FOR_TURN)
    Call<TurnResult> waitForTurn(
            @Query("hostid") String hostid,
            @Query("current_player") String current_player
    );

    @FormUrlEncoded
    @POST(TURN_DONE_PATH)
    Call<UpdateBoardResult> updateBoard(
            @Field("xml") String xml,
            @Field("hostid") String hostid
    );


}
