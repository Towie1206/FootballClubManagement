package com.eaut.footballclubmanagement;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.eaut.footballclubmanagement.models.Player;
import com.eaut.footballclubmanagement.utils.PlayerIntent;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class PlayerDetailActivity extends AppCompatActivity {
    private final List<ObjectAnimator> progressAnimators = new ArrayList<>();
    private Player player;
    private PlayerViewModel playerViewModel;
    private Button btnDelete;

    private final ActivityResultLauncher<Intent> editPlayerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() != RESULT_OK) return;
                Player updated = PlayerIntent.readPlayer(result.getData());
                if (updated != null) {
                    player = updated;
                    renderPlayer();
                    notifyPlayerChanged();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_detail);

        player = PlayerIntent.readPlayer(getIntent());
        if (player == null || player.getId() < 0) {
            Toast.makeText(this, R.string.invalid_player, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        playerViewModel = new ViewModelProvider(this).get(PlayerViewModel.class);
        btnDelete = findViewById(R.id.btnDelete);

        findViewById(R.id.btnBack).setOnClickListener(v -> finishAfterTransition());
        findViewById(R.id.btnEdit).setOnClickListener(v -> {
            Intent intent = new Intent(this, PlayerFormActivity.class);
            PlayerIntent.putPlayer(intent, player);
            editPlayerLauncher.launch(intent);
        });
        btnDelete.setOnClickListener(v -> confirmDelete());

        // Nghiệp vụ: Ghi nhận kết quả trận đấu
        findViewById(R.id.btnRecordMatch).setOnClickListener(v -> showRecordMatchDialog());

        renderPlayer();
    }

    private void renderPlayer() {
        ((TextView) findViewById(R.id.tvDetailName)).setText(safe(player.getFullName()));
        ((TextView) findViewById(R.id.tvDetailPosition)).setText(safe(player.getPosition()));
        ((TextView) findViewById(R.id.tvDetailOvr)).setText(String.valueOf(player.getOvr()));
        ((TextView) findViewById(R.id.tvDetailJersey)).setText(
                getString(R.string.jersey_number_format, player.getJerseyNumber()));
        ((TextView) findViewById(R.id.tvDetailClub)).setText(
                getString(R.string.club_format, safe(player.getClub())));

        // Hiển thị tình trạng thể lực
        TextView tvHealth = findViewById(R.id.tvDetailHealth);
        String health = player.getHealthStatus() != null ? player.getHealthStatus() : "Fit";
        if (health.equalsIgnoreCase("Injured") || health.contains("Chấn thương")) {
            tvHealth.setText("⚠ Condition: Injured");
            tvHealth.setTextColor(getResources().getColor(R.color.phui_error));
        } else {
            tvHealth.setText("✔ Condition: Match Fit");
            tvHealth.setTextColor(getResources().getColor(R.color.phui_accent));
        }

        // Hiển thị thành tích thi đấu
        ((TextView) findViewById(R.id.tvDetailMatches)).setText(String.valueOf(player.getMatches()));
        ((TextView) findViewById(R.id.tvDetailGoals)).setText(String.valueOf(player.getGoals()));
        ((TextView) findViewById(R.id.tvDetailAssists)).setText(String.valueOf(player.getAssists()));
        ((TextView) findViewById(R.id.tvDetailMvp)).setText(String.valueOf(player.getMvp()));

        setupRadarChart();
    }

    private void showRecordMatchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_record_match, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        tvTitle.setText("⚽ Match Performance: " + player.getFullName());

        EditText etGoals = dialogView.findViewById(R.id.etMatchGoals);
        EditText etAssists = dialogView.findViewById(R.id.etMatchAssists);
        CheckBox cbMvp = dialogView.findViewById(R.id.cbIsMvp);
        RadioButton rbFit = dialogView.findViewById(R.id.rbFit);
        RadioButton rbInjured = dialogView.findViewById(R.id.rbInjured);

        if (player.getHealthStatus() != null &&
                (player.getHealthStatus().equalsIgnoreCase("Injured") || player.getHealthStatus().contains("Chấn thương"))) {
            rbInjured.setChecked(true);
        } else {
            rbFit.setChecked(true);
        }

        dialogView.findViewById(R.id.btnDialogCancel).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.btnDialogConfirm).setOnClickListener(v -> {
            int addedGoals = 0;
            int addedAssists = 0;
            try {
                addedGoals = Integer.parseInt(etGoals.getText().toString().trim());
            } catch (Exception ignored) {}
            try {
                addedAssists = Integer.parseInt(etAssists.getText().toString().trim());
            } catch (Exception ignored) {}

            boolean isMvp = cbMvp.isChecked();
            String newHealth = rbInjured.isChecked() ? "Injured" : "Fit";

            // Thực hiện quy trình nghiệp vụ: Tăng trận đấu, cộng dồn bàn thắng, kiến tạo, MVP, sức khỏe
            Player updatedPlayer = new Player(
                    player.getId(),
                    player.getFullName(),
                    player.getPosition(),
                    player.getJerseyNumber(),
                    newHealth,
                    player.getOvr(),
                    player.getGoals() + addedGoals,
                    player.getAssists() + addedAssists,
                    player.getMatches() + 1,
                    player.getMvp() + (isMvp ? 1 : 0),
                    player.getClub(),
                    player.getPac(),
                    player.getSho(),
                    player.getPas(),
                    player.getDri(),
                    player.getDef(),
                    player.getPhy()
            );

            dialog.dismiss();
            Toast.makeText(this, "Recording match performance...", Toast.LENGTH_SHORT).show();

            playerViewModel.updatePlayer(player.getId(), updatedPlayer).observe(this, result -> {
                if (result == null) return;
                if (result.isSuccess() && result.getData() != null) {
                    player = result.getData();
                    renderPlayer();
                    notifyPlayerChanged();
                    Toast.makeText(this, "✔ Match performance recorded successfully!", Toast.LENGTH_LONG).show();
                } else if (result.getStatus() == OperationResult.Status.AUTH_REQUIRED) {
                    redirectToLogin();
                } else {
                    Toast.makeText(this, "Update error: " + result.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    private void notifyPlayerChanged() {
        Intent resultIntent = new Intent();
        PlayerIntent.putPlayer(resultIntent, player);
        setResult(RESULT_OK, resultIntent);
    }

    private void setupRadarChart() {
        RadarChart chart = findViewById(R.id.radarChart);

        List<RadarEntry> entries = new ArrayList<>();
        entries.add(new RadarEntry(player.getPac()));
        entries.add(new RadarEntry(player.getSho()));
        entries.add(new RadarEntry(player.getPas()));
        entries.add(new RadarEntry(player.getDri()));
        entries.add(new RadarEntry(player.getDef()));
        entries.add(new RadarEntry(player.getPhy()));

        RadarDataSet dataSet = new RadarDataSet(entries, "Skill Attributes");
        dataSet.setColor(Color.parseColor("#3DDC84"));
        dataSet.setFillColor(Color.parseColor("#3DDC84"));
        dataSet.setDrawFilled(true);
        dataSet.setFillAlpha(180);
        dataSet.setLineWidth(2f);
        dataSet.setDrawHighlightCircleEnabled(true);
        dataSet.setDrawHighlightIndicators(false);

        RadarData data = new RadarData(dataSet);
        data.setValueTextSize(10f);
        data.setDrawValues(false);
        data.setValueTextColor(Color.WHITE);

        chart.setData(data);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(
                new String[]{"PAC", "SHO", "PAS", "DRI", "DEF", "PHY"}));
        xAxis.setTextColor(Color.WHITE);
        xAxis.setTextSize(12f);

        YAxis yAxis = chart.getYAxis();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(100f);
        yAxis.setDrawLabels(false);
        yAxis.setTextColor(Color.TRANSPARENT);

        chart.animateXY(1000, 1000);
        chart.invalidate();
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_player_title)
                .setMessage(getString(R.string.delete_player_message, safe(player.getFullName())))
                .setPositiveButton(R.string.delete_action, (dialog, which) -> executeDelete())
                .setNegativeButton(R.string.cancel_action, null)
                .show();
    }

    private void executeDelete() {
        setDeleting(true);
        playerViewModel.deletePlayer(player.getId()).observe(this, result -> {
            if (result == null) return;
            setDeleting(false);
            if (result.getStatus() == OperationResult.Status.AUTH_REQUIRED) {
                redirectToLogin();
            } else if (result.isSuccess()) {
                setResult(RESULT_OK);
                Toast.makeText(this, R.string.player_deleted, Toast.LENGTH_SHORT).show();
                finishAfterTransition();
            } else {
                Toast.makeText(this, result.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setDeleting(boolean deleting) {
        btnDelete.setEnabled(!deleting);
        btnDelete.setText(deleting ? R.string.deleting_player : R.string.delete_player_action);
    }

    private void redirectToLogin() {
        Toast.makeText(this, R.string.session_expired, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }

    @Override
    protected void onDestroy() {
        for (ObjectAnimator animator : progressAnimators) animator.cancel();
        progressAnimators.clear();
        super.onDestroy();
    }
}
