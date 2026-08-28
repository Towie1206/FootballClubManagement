package com.eaut.footballclubmanagement;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class MatchReminderWorker extends Worker {

    public MatchReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        sendRichNotification();
        return Result.success();
    }

    private void sendRichNotification() {
        Context context = getApplicationContext();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "match_reminders";

        // Tương thích Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId, "Nhắc lịch thi đấu", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Kênh thông báo nhắc nhở lịch tập và thi đấu");
            notificationManager.createNotificationChannel(channel);
        }

        // Action khi bấm vào thông báo -> Mở App
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Xây dựng Thông báo xịn (Rich Notification)
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher)) // Dùng logo làm avatar
                .setContentTitle("⚽ ĐẾN GIỜ RA SÂN!")
                .setContentText("Hôm nay đội có lịch tập. Bầu sô vào kiểm tra phong độ cầu thủ nhé!")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Hôm nay đội có lịch tập lúc 19:00. Bầu sô vui lòng vào kiểm tra phong độ cầu thủ, sắp xếp đội hình ra sân và chốt kèo với đối tác nhé! Đừng quên kiểm tra OVR của các tân binh."))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .addAction(android.R.drawable.ic_menu_agenda, "XẾP ĐỘI HÌNH", pendingIntent); // Thêm nút Action

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
