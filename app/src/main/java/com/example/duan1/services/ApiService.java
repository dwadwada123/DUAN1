package com.example.duan1.services;

import com.example.duan1.models.Player;
import com.example.duan1.models.Squad;
import com.example.duan1.models.UploadResponse;
import com.example.duan1.models.User;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    // AUTHENTICATION / PROFILE
    @POST("/api/auth/sync")
    Call<User> syncUser(
            @Header("Authorization") String idToken,
            @Body SyncUserRequest request
    );

    @GET("/api/auth/profile")
    Call<User> getProfile(@Header("Authorization") String idToken);

    @PUT("/api/auth/profile")
    Call<User> updateProfile(
            @Header("Authorization") String idToken,
            @Body UpdateProfileRequest request
    );

    // PLAYER (PUBLIC)
    @GET("/api/players")
    Call<List<Player>> getPlayers(
            @Header("Authorization") String idToken,
            @Query("search") String search
    );

    @GET("/api/players/{id}")
    Call<Player> getPlayerDetail(
            @Header("Authorization") String idToken,
            @Path("id") String playerId
    );

    // INVENTORY (KHO CẦU THỦ)
    @GET("/api/players/inventory")
    Call<List<Player>> getMyInventory(
            @Header("Authorization") String idToken
    );

    @POST("/api/players/recruit")
    Call<Void> recruitPlayer(
            @Header("Authorization") String idToken,
            @Body RecruitRequest request
    );

    @DELETE("/api/players/inventory/{id}")
    Call<Void> dismissPlayer(
            @Header("Authorization") String idToken,
            @Path("id") String playerId
    );

    // PLAYER (ADMIN)
    @POST("/api/players")
    Call<Player> createPlayer(
            @Header("Authorization") String idToken,
            @Body PlayerRequest request
    );

    @PUT("/api/players/{id}")
    Call<Player> updatePlayer(
            @Header("Authorization") String idToken,
            @Path("id") String playerId,
            @Body PlayerRequest request
    );

    @DELETE("/api/players/{id}")
    Call<Void> deletePlayerSoft(
            @Header("Authorization") String idToken,
            @Path("id") String playerId
    );

    // SQUAD
    @GET("/api/squads")
    Call<List<Squad>> getUserSquads(@Header("Authorization") String idToken);

    @GET("/api/squads/{id}")
    Call<Squad> getSquadDetail(
            @Header("Authorization") String idToken,
            @Path("id") String squadId
    );

    @POST("/api/squads")
    Call<Squad> createSquad(
            @Header("Authorization") String idToken,
            @Body SquadRequest request
    );

    @PUT("/api/squads/{id}")
    Call<Squad> updateSquad(
            @Header("Authorization") String idToken,
            @Path("id") String squadId,
            @Body SquadUpdateRequest request
    );

    @DELETE("/api/squads/{id}")
    Call<Void> deleteSquad(
            @Header("Authorization") String idToken,
            @Path("id") String squadId
    );

    // ADMIN
    @GET("/api/admin/users")
    Call<List<User>> getAllUsers(@Header("Authorization") String idToken);

    @POST("/api/admin/users/{id}/ban")
    Call<Void> banUser(
            @Header("Authorization") String idToken,
            @Path("id") String userId,
            @Body BanUserRequest request
    );

    @retrofit2.http.HTTP(method = "DELETE", path = "/api/admin/users/{id}", hasBody = true)
    Call<Void> deleteUser(
            @Header("Authorization") String idToken,
            @Path("id") String userId,
            @Body DeleteUserRequest request
    );

    @GET("/api/admin/users/stats")
    Call<StatsResponse> getStats(@Header("Authorization") String idToken);

    // METADATA
    @GET("/api/meta/teams")
    Call<List<String>> getTeams();

    @GET("/api/meta/nations")
    Call<List<String>> getNations();

    @GET("/api/meta/positions")
    Call<List<String>> getPositions();

    @Multipart
    @POST("/api/upload")
    Call<UploadResponse> uploadImage(@Part MultipartBody.Part image);
}