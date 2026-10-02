package com.eaut.footballclubmanagement;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.eaut.footballclubmanagement.db.AppDatabase;
import com.eaut.footballclubmanagement.models.Player;
import com.eaut.footballclubmanagement.network.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvTotalPlayers, tvAvgOvr, tvTotalGoals;
    private TextView tvTopScorer, tvTopPlayer;
    private TextView tvCountFW, tvCountMF, tvCountDF, tvCountGK;
    private TextView tvCountFit, tvCountInjured;
    private ExecutorService executorService;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        bindViews();
        db = AppDatabase.getDatabase(this);
        executorService = Executors.newSingleThreadExecutor();

        setupBottomNavigation();
        findViewById(R.id.btnDashLogout).setOnClickListener(v -> confirmLogout());
    }

    private void bindViews() {
        tvTotalPlayers = findViewById(R.id.tvTotalPlayers);
        tvAvgOvr = findViewById(R.id.tvAvgOvr);
        tvTotalGoals = findViewById(R.id.tvTotalGoals);
        tvTopScorer = findViewById(R.id.tvTopScorer);
        tvTopPlayer = findViewById(R.id.tvTopPlayer);
        tvCountFW = findViewById(R.id.tvCountFW);
        tvCountMF = findViewById(R.id.tvCountMF);
        tvCountDF = findViewById(R.id.tvCountDF);
        tvCountGK = findViewById(R.id.tvCountGK);
        tvCountFit = findViewById(R.id.tvCountFit);
        tvCountInjured = findViewById(R.id.tvCountInjured);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStatisticsData();
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.nav_dashboard);
    }

    private void loadStatisticsData() {
        executorService.execute(() -> {
            List<Player> players = db.playerDao().getAllPlayers();
            int totalPlayers = players.size();
            int totalGoals = 0;
            int sumOvr = 0;
            Player topScorer = null;
            Player topPlayer = null;

            int fw = 0, mf = 0, df = 0, gk = 0;
            int fit = 0, injured = 0;

            for (Player p : players) {
                sumOvr += p.getOvr();
                totalGoals += p.getGoals();

                if (topScorer == null || p.getGoals() > topScorer.getGoals()) {
                    topScorer = p;
                }
                if (topPlayer == null || p.getOvr() > topPlayer.getOvr()) {
                    topPlayer = p;
                }

                String pos = p.getPosition() != null ? p.getPosition().toUpperCase() : "";
                if (pos.startsWith("FW") || pos.contains("TIỀN ĐẠO")) fw++;
                else if (pos.startsWith("MF") || pos.contains("TIỀN VỆ")) mf++;
                else if (pos.startsWith("DF") || pos.contains("HẬU VỆ")) df++;
                else if (pos.startsWith("GK") || pos.contains("THỦ MÔN")) gk++;
                else mf++;

                String health = p.getHealthStatus() != null ? p.getHealthStatus() : "";
                if (health.equalsIgnoreCase("Injured") || health.contains("Chấn thương")) {
                    injured++;
                } else {
                    fit++;
                }
            }

            double avgOvr = totalPlayers > 0 ? (double) sumOvr / totalPlayers : 0.0;
            String topScorerText = topScorer != null && topScorer.getGoals() > 0
                    ? topScorer.getFullName() + " (" + topScorer.getGoals() + " goals)"
                    : "None";
            String topPlayerText = topPlayer != null
                    ? topPlayer.getFullName() + " (" + topPlayer.getOvr() + " OVR - " + topPlayer.getPosition() + ")"
                    : "None";

            final int fFw = fw, fMf = mf, fDf = df, fGk = gk;
            final int fFit = fit, fInjured = injured;
            final int fTotalGoals = totalGoals;

            runOnUiThread(() -> {
                tvTotalPlayers.setText(String.valueOf(totalPlayers));
                tvAvgOvr.setText(String.format(Locale.getDefault(), "%.1f", avgOvr));
                tvTotalGoals.setText(String.valueOf(fTotalGoals));
                tvTopScorer.setText(topScorerText);
                tvTopPlayer.setText(topPlayerText);

                tvCountFW.setText(String.valueOf(fFw));
                tvCountMF.setText(String.valueOf(fMf));
                tvCountDF.setText(String.valueOf(fDf));
                tvCountGK.setText(String.valueOf(fGk));

                tvCountFit.setText("✔ Fit: " + fFit);
                tvCountInjured.setText("⚠ Injured: " + fInjured);
            });
        });
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.nav_dashboard);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return false;
            }
            return id == R.id.nav_dashboard;
        });
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out of the system?")
                .setPositiveButton("Log Out", (dialog, which) -> {
                    new SessionManager(this).clearAuth();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        if (executorService != null) {
            executorService.shutdown();
        }
        super.onDestroy();
    }
}
