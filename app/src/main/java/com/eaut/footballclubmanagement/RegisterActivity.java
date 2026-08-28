package com.eaut.footballclubmanagement;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class RegisterActivity extends AppCompatActivity {

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

            if (username.isEmpty() || password.length() < 6) {
                Toast.makeText(this, "Vui lòng nhập tên và mật khẩu >= 6 ký tự!", Toast.LENGTH_SHORT).show();
                return;
            }

            btnRegisterSubmit.setEnabled(false);
            btnRegisterSubmit.setText("ĐANG XỬ LÝ...");

            com.eaut.footballclubmanagement.network.ApiService.LoginRequest req = 
                new com.eaut.footballclubmanagement.network.ApiService.LoginRequest(username, password);

            com.eaut.footballclubmanagement.network.RetrofitClient.getApiService().register(req).enqueue(new retrofit2.Callback<com.eaut.footballclubmanagement.network.ApiService.LoginResponse>() {
                @Override
                public void onResponse(retrofit2.Call<com.eaut.footballclubmanagement.network.ApiService.LoginResponse> call, retrofit2.Response<com.eaut.footballclubmanagement.network.ApiService.LoginResponse> response) {
                    btnRegisterSubmit.setEnabled(true);
                    btnRegisterSubmit.setText("ĐĂNG KÝ NGAY");
                    
                    if (response.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Đăng ký thành công! Hãy đăng nhập.", Toast.LENGTH_LONG).show();
                        finish(); // Trở lại màn hình đăng nhập
                    } else {
                        Toast.makeText(RegisterActivity.this, "Tên đăng nhập đã tồn tại!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<com.eaut.footballclubmanagement.network.ApiService.LoginResponse> call, Throwable t) {
                    btnRegisterSubmit.setEnabled(true);
                    btnRegisterSubmit.setText("ĐĂNG KÝ NGAY");
                    Toast.makeText(RegisterActivity.this, "Lỗi mạng!", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
