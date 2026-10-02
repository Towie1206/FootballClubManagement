package com.eaut.footballclubmanagement;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.eaut.footballclubmanagement.network.ApiService;
import com.eaut.footballclubmanagement.network.NetworkErrorParser;
import com.eaut.footballclubmanagement.network.RetrofitClient;
import com.eaut.footballclubmanagement.network.SessionManager;
import com.eaut.footballclubmanagement.utils.CredentialValidator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private Call<ApiService.LoginResponse> registerCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText etRegUsername = findViewById(R.id.etRegUsername);
        EditText etRegPassword = findViewById(R.id.etRegPassword);
        AppCompatButton btnRegisterSubmit = findViewById(R.id.btnRegisterSubmit);
        TextView tvBackToLogin = findViewById(R.id.tvBackToLogin);

        tvBackToLogin.setOnClickListener(v -> finish()); // Quay lại màn login

        btnRegisterSubmit.setOnClickListener(v -> {
            String username = etRegUsername.getText().toString().trim();
            String password = etRegPassword.getText().toString().trim();

            String usernameError = CredentialValidator.usernameError(username);
            String passwordError = CredentialValidator.passwordError(password);
            etRegUsername.setError(usernameError);
            etRegPassword.setError(passwordError);
            if (usernameError != null || passwordError != null) {
                Toast.makeText(this, R.string.invalid_registration, Toast.LENGTH_SHORT).show();
                return;
            }

            btnRegisterSubmit.setEnabled(false);
            btnRegisterSubmit.setText("ĐANG XỬ LÝ...");

            ApiService.LoginRequest req = new ApiService.LoginRequest(username, password);

            registerCall = RetrofitClient.getApiService().register(req);
            registerCall.enqueue(new Callback<ApiService.LoginResponse>() {
                @Override
                public void onResponse(Call<ApiService.LoginResponse> call, Response<ApiService.LoginResponse> response) {
                    btnRegisterSubmit.setEnabled(true);
                    btnRegisterSubmit.setText(R.string.register_action);
                    
                    ApiService.LoginResponse body = response.body();
                    if (response.isSuccessful() && body != null && body.token != null && !body.token.trim().isEmpty()) {
                        String returnedUsername = body.user != null && body.user.username != null
                                ? body.user.username : username;
                        new SessionManager(RegisterActivity.this)
                                .saveSession(body.token, returnedUsername, body.expiresIn);
                        Toast.makeText(RegisterActivity.this, R.string.register_success, Toast.LENGTH_SHORT).show();
                        android.content.Intent intent = new android.content.Intent(RegisterActivity.this, DashboardActivity.class);
                        intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this,
                                NetworkErrorParser.message(response,
                                        response.code() == 409 ? "Tên đăng nhập đã tồn tại" : "Không thể đăng ký"),
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiService.LoginResponse> call, Throwable t) {
                    if (call.isCanceled()) return;
                    btnRegisterSubmit.setEnabled(true);
                    btnRegisterSubmit.setText(R.string.register_action);
                    Toast.makeText(RegisterActivity.this, R.string.server_unreachable, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        if (registerCall != null) registerCall.cancel();
        super.onDestroy();
    }
}
