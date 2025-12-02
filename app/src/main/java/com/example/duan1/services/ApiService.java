package com.example.duan1.services; // Đã đổi package thành apis

import com.example.duan1.models.Player;
import com.example.duan1.models.Squad;
import com.example.duan1.models.User;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

// Import các lớp Request/Response Body mà bạn đã tạo
import com.example.duan1.services.SyncUserRequest;
import com.example.duan1.services.PlayerRequest;
import com.example.duan1.services.SquadRequest;
import com.example.duan1.services.SquadUpdateRequest;
import com.example.duan1.services.BanUserRequest;
import com.example.duan1.services.DeleteUserRequest;
import com.example.duan1.services.StatsResponse;

public interface ApiService {

    // --- CÁC ENDPOINT AUTH (Xác thực) ---

    /**
     * Đồng bộ User từ Firebase sang Mongo. Trả về Role.
     * Endpoint: POST /api/auth/sync
     * Yêu cầu: Auth (Firebase ID Token)
     */
    @POST("/api/auth/sync")
    Call<User> syncUser(
            @Header("Authorization") String idToken, // Gửi "Bearer [Token]"
            @Body SyncUserRequest request
    );

    // --- CÁC ENDPOINT PLAYERS (Cầu thủ) ---

    /**
     * Lấy danh sách cầu thủ. Hỗ trợ query: ?search=Messi
     * Endpoint: GET /api/players
     * Yêu cầu: Auth
     */
    @GET("/api/players")
    Call<List<Player>> getPlayers(
            @Header("Authorization") String idToken,
            @Query("search") String search
    );

    /**
     * Lấy chi tiết 1 cầu thủ
     * Endpoint: GET /api/players/:id
     * Yêu cầu: Auth
     */
    @GET("/api/players/{id}")
    Call<Player> getPlayerDetail(
            @Header("Authorization") String idToken,
            @Path("id") String playerId
    );

    /**
     * Thêm cầu thủ mới (kèm upload ảnh)
     * Endpoint: POST /api/players
     * Yêu cầu: Admin
     * Gợi ý: Dùng @Multipart và @Part cho việc upload file và dữ liệu
     */
    @POST("/api/players")
    Call<Player> createPlayer(
            @Header("Authorization") String idToken,
            @Body PlayerRequest request
    );

    /**
     * Sửa cầu thủ
     * Endpoint: PUT /api/players/:id
     * Yêu cầu: Admin
     */
    @PUT("/api/players/{id}")
    Call<Player> updatePlayer(
            @Header("Authorization") String idToken,
            @Path("id") String playerId,
            @Body PlayerRequest request
    );

    /**
     * Xóa mềm cầu thủ (is_active: false)
     * Endpoint: DELETE /api/players/:id
     * Yêu cầu: Admin
     */
    @DELETE("/api/players/{id}")
    Call<Void> deletePlayerSoft(
            @Header("Authorization") String idToken,
            @Path("id") String playerId
    );


    // --- CÁC ENDPOINT SQUADS (Đội hình) ---

    /**
     * Lấy danh sách đội hình của User đang login
     * Endpoint: GET /api/squads
     * Yêu cầu: Auth
     */
    @GET("/api/squads")
    Call<List<Squad>> getUserSquads(@Header("Authorization") String idToken);

    /**
     * Tạo đội hình mới
     * Endpoint: POST /api/squads
     * Yêu cầu: Auth
     */
    @POST("/api/squads")
    Call<Squad> createSquad(
            @Header("Authorization") String idToken,
            @Body SquadRequest request
    );

    /**
     * Cập nhật đội hình (Thêm cầu thủ vào slot)
     * Endpoint: PUT /api/squads/:id
     * Yêu cầu: Auth
     */
    @PUT("/api/squads/{id}")
    Call<Squad> updateSquad(
            @Header("Authorization") String idToken,
            @Path("id") String squadId,
            @Body SquadUpdateRequest request
    );

    /**
     * Xóa đội hình
     * Endpoint: DELETE /api/squads/:id
     * Yêu cầu: Auth
     */
    @DELETE("/api/squads/{id}")
    Call<Void> deleteSquad(
            @Header("Authorization") String idToken,
            @Path("id") String squadId
    );


    // --- CÁC ENDPOINT ADMIN ---

    /**
     * Lấy danh sách tất cả User
     * Endpoint: GET /api/admin/users
     * Yêu cầu: Admin
     */
    @GET("/api/admin/users")
    Call<List<User>> getAllUsers(@Header("Authorization") String idToken);

    /**
     * Ban User (Ghi log lý do)
     * Endpoint: POST /api/admin/users/:id/ban
     * Yêu cầu: Admin
     */
    @POST("/api/admin/users/{id}/ban")
    Call<Void> banUser(
            @Header("Authorization") String idToken,
            @Path("id") String userId,
            @Body BanUserRequest request
    );

    /**
     * Xóa User (Ghi log lý do)
     * Endpoint: DELETE /api/admin/users/:id
     * Yêu cầu: Admin
     */
    @DELETE("/api/admin/users/{id}")
    Call<Void> deleteUser(
            @Header("Authorization") String idToken,
            @Path("id") String userId,
            @Body DeleteUserRequest request
    );

    /**
     * Lấy thông kê (Số lượng User, Player, Squad)
     * Endpoint: GET /api/admin/stats
     * Yêu cầu: Admin
     */
    @GET("/api/admin/stats")
    Call<StatsResponse> getStats(@Header("Authorization") String idToken);
}