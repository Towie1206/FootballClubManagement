package com.eaut.footballclubmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private TextView tvError, tvGoToRegister;
    private Button btnLogin, btnGoogleSignIn;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

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

        // Chuyển sang màn hình Đăng ký
        tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        btnLogin.setOnClickListener(v -> handleLogin());

        // Cấu hình Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btnGoogleSignIn.setOnClickListener(v -> signInWithGoogle());
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                // Đăng nhập thành công, lấy thông tin
                String email = account.getEmail();
                
                android.content.SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                prefs.edit().putBoolean("isLoggedIn", true).putString("token", "GOOGLE_TOKEN_" + email).apply();
                
                startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                finish();
            } catch (ApiException e) {
                // Sẽ nhảy vào đây nếu chưa config SHA-1 trên Google Cloud
                tvError.setText("Lỗi Google Sign-In: Chưa cấu hình SHA-1 trên Firebase/Google Cloud!");
                tvError.setVisibility(View.VISIBLE);
            }
        }
    }

    private void handleLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            tvError.setText("Vui lòng nhập đầy đủ Tài khoản và Mật khẩu!");
            tvError.setVisibility(android.view.View.VISIBLE);
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("ĐANG XÁC THỰC...");

        com.eaut.footballclubmanagement.network.ApiService.LoginRequest req = 
            new com.eaut.footballclubmanagement.network.ApiService.LoginRequest(username, password);

        com.eaut.footballclubmanagement.network.RetrofitClient.getApiService().login(req).enqueue(new retrofit2.Callback<com.eaut.footballclubmanagement.network.ApiService.LoginResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.eaut.footballclubmanagement.network.ApiService.LoginResponse> call, retrofit2.Response<com.eaut.footballclubmanagement.network.ApiService.LoginResponse> response) {
                btnLogin.setEnabled(true);
                btnLogin.setText("Vào việc");

                if (response.isSuccessful() && response.body() != null) {
                    tvError.setVisibility(android.view.View.GONE);
                    
                    android.content.SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                    prefs.edit()
                         .putBoolean("isLoggedIn", true)
                         .putString("token", response.body().token)
                         .apply();

                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    tvError.setText("Tên đăng nhập hoặc mật khẩu không đúng!");
                    tvError.setVisibility(android.view.View.VISIBLE);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.eaut.footballclubmanagement.network.ApiService.LoginResponse> call, Throwable t) {
                btnLogin.setEnabled(true);
                btnLogin.setText("Vào việc");
                tvError.setText("Lỗi kết nối máy chủ!");
                tvError.setVisibility(android.view.View.VISIBLE);
            }
        });
    }
}
