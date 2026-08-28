package com.eaut.footballclubmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.eaut.footballclubmanagement.db.AppDatabase;
import com.eaut.footballclubmanagement.db.FundDao;
import com.eaut.footballclubmanagement.db.PlayerDao;
import com.eaut.footballclubmanagement.models.Player;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvDashFund, tvDashPlayers, tvDashTopScorer;
    private ExecutorService executorService;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        tvDashFund = findViewById(R.id.tvDashFund);
        tvDashPlayers = findViewById(R.id.tvDashPlayers);
        tvDashTopScorer = findViewById(R.id.tvDashTopScorer);

        db = AppDatabase.getDatabase(this);
        executorService = Executors.newSingleThreadExecutor();

        setupBottomNavigation();

        findViewById(R.id.btnQuickAttendance).setOnClickListener(v -> {
            Toast.makeText(this, "Đã sao chép link điểm danh! Hãy gửi vào Zalo cho anh em.", Toast.LENGTH_LONG).show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardData();
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.nav_dashboard);
    }

    private void loadDashboardData() {
        executorService.execute(() -> {
            FundDao fundDao = db.fundDao();
            PlayerDao playerDao = db.playerDao();

            Integer balanceObj = fundDao.getTotalBalance();
            int balance = (balanceObj != null) ? balanceObj : 0;

            List<Player> players = playerDao.getAllPlayers();
            int playerCount = players.size();
            
            String topScorer = "--";
            int maxGoals = -1;
            for (Player p : players) {
                if (p.goals > maxGoals) {
                    maxGoals = p.goals;
                    topScorer = p.fullName + " (" + p.goals + " bàn)";
                }
            }

            String finalTopScorer = topScorer;
            runOnUiThread(() -> {
                DecimalFormat formatter = new DecimalFormat("#,###");
                tvDashFund.setText(formatter.format(balance) + " ₫");
                tvDashPlayers.setText(String.valueOf(playerCount));
                tvDashTopScorer.setText(finalTopScorer);
                
                if (balance < 0) {
                    tvDashFund.setTextColor(getResources().getColor(R.color.phui_error));
                } else {
                    tvDashFund.setTextColor(getResources().getColor(R.color.phui_accent));
                }
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
                overridePendingTransition(0,0);
                return false;
            } else if (id == R.id.nav_fund) {
                Intent intent = new Intent(this, FundActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0,0);
                return false;
            } else if (id == R.id.nav_ai) {
                Intent intent = new Intent(this, AiCoachActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0,0);
                return false;
            } else if (id == R.id.nav_settings) {
                Intent intent = new Intent(this, SettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0,0);
                return false;
            }
            return true;
        });
    }
}
