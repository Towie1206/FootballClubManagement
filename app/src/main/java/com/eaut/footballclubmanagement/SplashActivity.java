package com.eaut.footballclubmanagement;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.eaut.footballclubmanagement.network.SessionManager;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Animation: Fade in và trượt lên (Slide up)
        android.view.View logoLayout = findViewById(R.id.logoContainer); // Phải bao các TextView vào 1 thẻ này
        logoLayout.setAlpha(0f);
        logoLayout.setTranslationY(100f);
        logoLayout.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(1200)
                .withEndAction(() -> {
                    // Chờ animation chạy xong mới kiểm tra đăng nhập
                    if (isFinishing() || isDestroyed()) return;
                    boolean isLoggedIn = new SessionManager(this).hasValidSession();
                    
                    Intent intent;
                    if (isLoggedIn) {
                        intent = new Intent(SplashActivity.this, DashboardActivity.class);
                    } else {
                        intent = new Intent(SplashActivity.this, LoginActivity.class);
                    }
                    startActivity(intent);
                    finish();
                })
                .start();
    }

    @Override
    protected void onDestroy() {
        android.view.View logoLayout = findViewById(R.id.logoContainer);
        if (logoLayout != null) logoLayout.animate().cancel();
        super.onDestroy();
    }
}
