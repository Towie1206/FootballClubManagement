package com.eaut.footballclubmanagement;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.ExistingPeriodicWorkPolicy;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AlertDialog;
import android.widget.TextView;
import androidx.appcompat.widget.SwitchCompat;
import android.content.SharedPreferences;

public class SettingsActivity extends AppCompatActivity {

    private SwitchCompat switchNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Nạp thông tin tài khoản
        TextView tvAccountName = findViewById(R.id.tvAccountName);
        TextView tvAccountEmail = findViewById(R.id.tvAccountEmail);
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        
        String savedToken = prefs.getString("token", "");
        if (!savedToken.isEmpty()) {
            tvAccountEmail.setText("admin@phui.id.vn"); // Hoặc lấy từ Decode JWT nếu có
        }

        // Setup Nút Đăng xuất với Hộp thoại xác nhận (AlertDialog)
        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(SettingsActivity.this)
                    .setTitle("Đăng xuất")
                    .setMessage("Bạn có chắc chắn muốn thoát khỏi ứng dụng?")
                    .setPositiveButton("ĐĂNG XUẤT", (dialog, which) -> {
                        prefs.edit().clear().apply();
                        Toast.makeText(this, "Đã đăng xuất!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("HỦY", null)
                    .show();
        });

        // Toggle Nhắc việc (Sử dụng WorkManager)
        switchNotification = findViewById(R.id.switchNotification);
        
        // Kiểm tra trạng thái hiện tại (Lưu vào SharedPreferences)
        boolean isNotifEnabled = prefs.getBoolean("notif_enabled", false);
        switchNotification.setChecked(isNotifEnabled);

        switchNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("notif_enabled", isChecked).apply();
            if (isChecked) {
                setupBackgroundNotifications();
            } else {
                WorkManager.getInstance(this).cancelUniqueWork("MatchReminderWork");
                Toast.makeText(this, "Đã tắt Trợ lý nhắc việc", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupBackgroundNotifications() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                switchNotification.setChecked(false); // Hoàn tác UI
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
                return;
            }
        }

        PeriodicWorkRequest reminderRequest = new PeriodicWorkRequest.Builder(MatchReminderWorker.class, 15, TimeUnit.MINUTES).build();
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("MatchReminderWork", ExistingPeriodicWorkPolicy.KEEP, reminderRequest);
        Toast.makeText(this, "Đã bật Trợ lý nhắc việc chạy ngầm!", Toast.LENGTH_SHORT).show();
        testInstantNotification(); // Demo cho Giảng viên
    }

    private void testInstantNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "match_reminders";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Nhắc lịch thi đấu", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        android.app.PendingIntent pendingIntent = android.app.PendingIntent.getActivity(
                this, 0, intent, android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("⚽ ĐẾN GIỜ RA SÂN!")
                .setStyle(new NotificationCompat.BigTextStyle().bigText("Hôm nay đội có lịch tập. Bầu sô vui lòng vào kiểm tra phong độ cầu thủ và chốt kèo!"))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @androidx.annotation.NonNull String[] permissions, @androidx.annotation.NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switchNotification.setChecked(true);
            setupBackgroundNotifications();
        } else {
            Toast.makeText(this, "Cần cấp quyền để thông báo hoạt động!", Toast.LENGTH_LONG).show();
        }
    }
}
