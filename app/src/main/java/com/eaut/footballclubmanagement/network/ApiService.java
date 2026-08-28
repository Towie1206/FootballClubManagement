package com.eaut.footballclubmanagement.network;

import com.eaut.footballclubmanagement.models.Player;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.DELETE;

public interface ApiService {

    // Lấy danh sách cầu thủ
    @GET("api/players")
    Call<List<Player>> getPlayers();

    // Thêm cầu thủ mới
    @POST("api/players")
    Call<Player> addPlayer(@Body Player player);

    // Sửa thông tin cầu thủ
    @PUT("api/players/{id}")
    Call<Player> updatePlayer(@retrofit2.http.Path("id") int id, @Body Player player);

    // Xóa cầu thủ
    @DELETE("api/players/{id}")
    Call<Void> deletePlayer(@retrofit2.http.Path("id") int id);

    // API để gửi prompt tới AI Coach
    @POST("api/ai/coach")
    Call<AiResponse> askAiCoach(@Body AiRequest request);

    // API Đăng nhập
    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    // API Đăng ký
    @POST("api/auth/register")
    Call<LoginResponse> register(@Body LoginRequest request);

    class AiRequest {
        public String prompt;
        public AiRequest(String prompt) { this.prompt = prompt; }
    }

    class AiResponse {
        public String reply;
    }

    class LoginRequest {
        public String username;
        public String password;
        public LoginRequest(String u, String p) { this.username = u; this.password = p; }
    }

    class LoginResponse {
        public String token;
        public String message;
        // Bỏ qua map User cho đơn giản
    }
}
