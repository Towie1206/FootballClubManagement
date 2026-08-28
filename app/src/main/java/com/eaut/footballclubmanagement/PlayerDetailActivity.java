package com.eaut.footballclubmanagement;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.eaut.footballclubmanagement.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayerDetailActivity extends AppCompatActivity {

    private int playerId;
    private String name, position;
    private int jersey, goals, mvp, ovr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_detail);

        playerId = getIntent().getIntExtra("PLAYER_ID", -1);
        name = getIntent().getStringExtra("PLAYER_NAME");
        position = getIntent().getStringExtra("PLAYER_POSITION");
        jersey = getIntent().getIntExtra("PLAYER_JERSEY", 0);
        goals = getIntent().getIntExtra("PLAYER_GOALS", 0);
        mvp = getIntent().getIntExtra("PLAYER_MVP", 0);
        ovr = getIntent().getIntExtra("PLAYER_OVR", 50);

        TextView tvName = findViewById(R.id.tvDetailName);
        TextView tvPosition = findViewById(R.id.tvDetailPosition);
        TextView tvOvr = findViewById(R.id.tvDetailOvr);
        TextView tvJersey = findViewById(R.id.tvDetailJersey);

        android.widget.ProgressBar progressPac = findViewById(R.id.progressPac);
        android.widget.ProgressBar progressSho = findViewById(R.id.progressSho);
        android.widget.ProgressBar progressMvp = findViewById(R.id.progressMvp);

        tvName.setText(name);
        tvPosition.setText(position);
        tvOvr.setText(String.valueOf(ovr));
        tvJersey.setText("Số áo: " + jersey);

        // Hiệu ứng chạy thanh tiến trình (Animation Progress Bars)
        android.animation.ObjectAnimator.ofInt(progressPac, "progress", 0, Math.min(ovr + (position.equals("FW") ? 5 : 0), 99))
                .setDuration(1000).start();
        
        android.animation.ObjectAnimator.ofInt(progressSho, "progress", 0, Math.min((goals * 8) + 40, 99))
                .setDuration(1000).start();
                
        android.animation.ObjectAnimator.ofInt(progressMvp, "progress", 0, Math.min(mvp * 20, 100))
                .setDuration(1000).start();

        Button btnEdit = findViewById(R.id.btnEditFromDetail);
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(PlayerDetailActivity.this, PlayerFormActivity.class);
            intent.putExtra("PLAYER_ID", playerId);
            intent.putExtra("PLAYER_NAME", name);
            intent.putExtra("PLAYER_JERSEY", jersey);
            intent.putExtra("PLAYER_POSITION", position);
            startActivity(intent);
            finish(); // Đóng trang detail, sau khi sửa xong quay về Main
        });

        Button btnDelete = findViewById(R.id.btnDeleteFromDetail);
        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Xóa cầu thủ " + name + "?")
                    .setPositiveButton("Xóa", (dialog, which) -> deletePlayer(playerId))
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }

    private void deletePlayer(int id) {
        RetrofitClient.getApiService().deletePlayer(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast.makeText(PlayerDetailActivity.this, "Đã xóa thành công", Toast.LENGTH_SHORT).show();
                finish(); // Đóng trang detail, quay về màn Main
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(PlayerDetailActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
