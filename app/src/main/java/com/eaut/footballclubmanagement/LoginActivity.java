package com.eaut.footballclubmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.eaut.footballclubmanagement.network.ApiService;
import com.eaut.footballclubmanagement.network.NetworkErrorParser;
import com.eaut.footballclubmanagement.network.RetrofitClient;
import com.eaut.footballclubmanagement.network.SessionManager;
import com.eaut.footballclubmanagement.utils.CredentialValidator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private TextView tvError, tvGoToRegister;
    private Button btnLogin, btnGoogleSignIn;
    private Call<ApiService.LoginResponse> loginCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        tvError = findViewById(R.id.tvError);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);

        etUsername.setText("admin");
        etPassword.setText("123456");

        // Chuyển sang màn hình Đăng ký
        tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        btnLogin.setOnClickListener(v -> handleLogin());

        // Nút vào thẳng chế độ Demo Offline để không bao giờ bị nghẽn
        btnGoogleSignIn.setVisibility(View.VISIBLE);
        btnGoogleSignIn.setText("⚡ VÀO THẲNG DEMO (OFFLINE)");
        btnGoogleSignIn.setOnClickListener(v -> enterOfflineDemo());
    }

    private void enterOfflineDemo() {
        new SessionManager(LoginActivity.this)
                .saveSession("demo_offline_token", "admin", 86400);
        java.util.concurrent.Executors.newSingleThreadExecutor().execute(() -> {
            try {
                com.eaut.footballclubmanagement.db.AppDatabase db =
                        com.eaut.footballclubmanagement.db.AppDatabase.getDatabase(LoginActivity.this);
                if (db.playerDao().getAllPlayers().isEmpty()) {
                    db.playerDao().insertAll(com.eaut.footballclubmanagement.utils.SeedData.getInitialPlayers());
                }
            } catch (Exception ignored) {}
        });
        android.widget.Toast.makeText(LoginActivity.this, "Đang vào ứng dụng...", android.widget.Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void handleLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        String usernameError = CredentialValidator.usernameError(username);
        String passwordError = CredentialValidator.passwordError(password);
        etUsername.setError(usernameError);
        etPassword.setError(passwordError);
        if (usernameError != null || passwordError != null) {
            showError("Vui lòng kiểm tra lại thông tin đăng nhập");
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("ĐANG XÁC THỰC...");

        ApiService.LoginRequest req = new ApiService.LoginRequest(username, password);

        loginCall = RetrofitClient.getApiService().login(req);
        loginCall.enqueue(new Callback<ApiService.LoginResponse>() {
            @Override
            public void onResponse(Call<ApiService.LoginResponse> call, Response<ApiService.LoginResponse> response) {
                setLoading(false);

                ApiService.LoginResponse body = response.body();
                if (response.isSuccessful() && body != null && body.token != null && !body.token.trim().isEmpty()) {
                    tvError.setVisibility(View.GONE);
                    String returnedUsername = body.user != null && body.user.username != null
                            ? body.user.username : username;
                    new SessionManager(LoginActivity.this)
                            .saveSession(body.token, returnedUsername, body.expiresIn);

                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    showError(NetworkErrorParser.message(response,
                            response.code() == 401
                                    ? "Tên đăng nhập hoặc mật khẩu không đúng"
                                    : "Không thể đăng nhập. Vui lòng thử lại"));
                }
            }

            @Override
            public void onFailure(Call<ApiService.LoginResponse> call, Throwable t) {
                if (call.isCanceled()) return;
                setLoading(false);
                // Nếu máy chủ chưa bật hoặc lỗi mạng, tự động đưa người dùng vào ứng dụng luôn!
                android.widget.Toast.makeText(LoginActivity.this, "Máy chủ ngoại tuyến - Vào chế độ Demo!", android.widget.Toast.LENGTH_SHORT).show();
                enterOfflineDemo();
            }
        });
    }

    private void setLoading(boolean loading) {
        btnLogin.setEnabled(!loading);
        btnLogin.setText(loading ? R.string.authenticating : R.string.login_action);
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        if (loginCall != null) loginCall.cancel();
        super.onDestroy();
    }
}
