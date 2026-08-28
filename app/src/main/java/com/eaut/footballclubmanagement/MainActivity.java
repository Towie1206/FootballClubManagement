package com.eaut.footballclubmanagement;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.eaut.footballclubmanagement.models.Player;
import com.eaut.footballclubmanagement.PlayerViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PlayerAdapter adapter;
    private FloatingActionButton btnAddPlayer;
    private BottomNavigationView bottomNavigation;
    private android.widget.EditText etSearch;
    private android.view.View layoutEmptyState;
    private com.facebook.shimmer.ShimmerFrameLayout shimmerViewContainer;
    private List<Player> originalPlayerList = new ArrayList<>();
    
    private PlayerViewModel playerViewModel;
    private androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        playerViewModel = new ViewModelProvider(this).get(PlayerViewModel.class);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.swipeRefreshLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0); // Bỏ bottom padding để BottomNav sát đáy
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerViewPlayers);
        etSearch = findViewById(R.id.etSearch);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        shimmerViewContainer = findViewById(R.id.shimmerViewContainer);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new PlayerAdapter(new ArrayList<>(), new PlayerAdapter.OnPlayerClickListener() {
            @Override
            public void onEdit(Player p, android.view.View cardRoot) {
                Intent intent = new Intent(MainActivity.this, PlayerDetailActivity.class);
                intent.putExtra("PLAYER_ID", p.getId());
                intent.putExtra("PLAYER_NAME", p.getFullName());
                intent.putExtra("PLAYER_JERSEY", p.getJerseyNumber());
                intent.putExtra("PLAYER_POSITION", p.getPosition());
                intent.putExtra("PLAYER_GOALS", p.getGoals());
                intent.putExtra("PLAYER_MVP", p.getMvp());
                intent.putExtra("PLAYER_OVR", p.getOvr());

                androidx.core.app.ActivityOptionsCompat options = androidx.core.app.ActivityOptionsCompat.makeSceneTransitionAnimation(
                        MainActivity.this, cardRoot, "player_card_transition"
                );
                startActivity(intent, options.toBundle());
            }

            @Override
            public void onDelete(Player p) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Xác nhận xóa")
                        .setMessage("Xóa cầu thủ " + p.getFullName() + "?")
                        .setPositiveButton("Xóa", (dialog, which) -> deletePlayer(p.getId()))
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        });
        recyclerView.setAdapter(adapter);

        androidx.recyclerview.widget.ItemTouchHelper itemTouchHelper = new androidx.recyclerview.widget.ItemTouchHelper(new androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback(0, androidx.recyclerview.widget.ItemTouchHelper.LEFT | androidx.recyclerview.widget.ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) { return false; }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Player deletedPlayer = adapter.getPlayerAt(position);
                
                playerViewModel.deletePlayer(deletedPlayer.getId()).observe(MainActivity.this, isSuccess -> {
                    if (isSuccess != null && isSuccess) {
                        adapter.removePlayerAt(position);
                        showSuccessSnackbar("Đã xóa " + deletedPlayer.getFullName(), deletedPlayer, position);
                    } else {
                        showErrorSnackbar("Xóa thất bại do lỗi mạng!");
                        adapter.notifyItemChanged(position);
                    }
                });
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);

        btnAddPlayer = findViewById(R.id.btnAddPlayer);
        btnAddPlayer.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, PlayerFormActivity.class)));

        bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_dashboard) {
                Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0,0);
                return false;
            } else if (id == R.id.nav_home) {
                recyclerView.smoothScrollToPosition(0);
                return true;
            } else if (id == R.id.nav_fund) {
                Intent intent = new Intent(MainActivity.this, FundActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return false;
            } else if (id == R.id.nav_ai) {
                Intent intent = new Intent(MainActivity.this, AiCoachActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return false;
            } else if (id == R.id.nav_settings) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return false;
            }
            return false;
        });

        androidx.appcompat.widget.AppCompatButton btnSort = findViewById(R.id.btnSort);
        androidx.appcompat.widget.AppCompatButton filterAll = findViewById(R.id.filterAll);
        androidx.appcompat.widget.AppCompatButton filterFW = findViewById(R.id.filterFW);
        androidx.appcompat.widget.AppCompatButton filterMF = findViewById(R.id.filterMF);
        androidx.appcompat.widget.AppCompatButton filterDF = findViewById(R.id.filterDF);
        androidx.appcompat.widget.AppCompatButton filterGK = findViewById(R.id.filterGK);
        androidx.appcompat.widget.AppCompatButton[] filterButtons = {filterAll, filterFW, filterMF, filterDF, filterGK};
        
        btnSort.setOnClickListener(v -> {
            currentSortMode = (currentSortMode + 1) % 3;
            if (currentSortMode == 0) btnSort.setText("↕ OVR");
            else if (currentSortMode == 1) btnSort.setText("⭐ OVR (Cao)");
            else btnSort.setText("⚽ Bàn thắng");
            applyFiltersAndSort();
        });

        android.view.View.OnClickListener filterListener = v -> {
            for (androidx.appcompat.widget.AppCompatButton btn : filterButtons) {
                btn.setBackgroundResource(R.drawable.bg_input);
                btn.setTextColor(getResources().getColor(R.color.phui_text_primary));
            }
            androidx.appcompat.widget.AppCompatButton clicked = (androidx.appcompat.widget.AppCompatButton) v;
            clicked.setBackgroundResource(R.drawable.bg_button_primary);
            clicked.setTextColor(getResources().getColor(R.color.phui_bg));
            if (clicked == filterAll) currentFilterPosition = "ALL";
            else if (clicked == filterFW) currentFilterPosition = "FW";
            else if (clicked == filterMF) currentFilterPosition = "MF";
            else if (clicked == filterDF) currentFilterPosition = "DF";
            else if (clicked == filterGK) currentFilterPosition = "GK";
            applyFiltersAndSort();
        };

        for (androidx.appcompat.widget.AppCompatButton btn : filterButtons) {
            btn.setOnClickListener(filterListener);
        }

        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(android.text.Editable s) {
                currentSearchQuery = s.toString().toLowerCase();
                applyFiltersAndSort();
            }
        });

        swipeRefresh = findViewById(R.id.swipeRefreshLayout);
        swipeRefresh.setOnRefreshListener(this::fetchDataFromViewModel);

        shimmerViewContainer.startShimmer();
        fetchDataFromViewModel();
    }

    private void showSuccessSnackbar(String message, Player deletedPlayer, int position) {
        com.google.android.material.snackbar.Snackbar snackbar = com.google.android.material.snackbar.Snackbar.make(recyclerView, message, com.google.android.material.snackbar.Snackbar.LENGTH_LONG);
        snackbar.setAction("HOÀN TÁC", v -> {
            playerViewModel.addPlayer(deletedPlayer).observe(MainActivity.this, addedPlayer -> {
                if (addedPlayer != null) adapter.restorePlayer(deletedPlayer, position);
            });
        });
        snackbar.setActionTextColor(getResources().getColor(R.color.phui_accent));
        snackbar.show();
    }

    private void showErrorSnackbar(String message) {
        com.google.android.material.snackbar.Snackbar snackbar = com.google.android.material.snackbar.Snackbar.make(recyclerView, message, com.google.android.material.snackbar.Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.phui_error));
        snackbar.show();
    }

    private String currentSearchQuery = "";
    private String currentFilterPosition = "ALL";
    private int currentSortMode = 0;

    private void applyFiltersAndSort() {
        if (originalPlayerList == null) return;
        List<Player> filteredList = new ArrayList<>();
        for (Player p : originalPlayerList) {
            boolean matchesSearch = p.getFullName().toLowerCase().contains(currentSearchQuery);
            boolean matchesPosition = currentFilterPosition.equals("ALL") || p.getPosition().equals(currentFilterPosition);
            if (matchesSearch && matchesPosition) filteredList.add(p);
        }
        if (currentSortMode == 1) java.util.Collections.sort(filteredList, (p1, p2) -> Integer.compare(p2.getOvr(), p1.getOvr()));
        else if (currentSortMode == 2) java.util.Collections.sort(filteredList, (p1, p2) -> Integer.compare(p2.getGoals(), p1.getGoals()));
        
        adapter.updateData(filteredList);
        
        if (filteredList.isEmpty()) {
            layoutEmptyState.setVisibility(android.view.View.VISIBLE);
            recyclerView.setVisibility(android.view.View.GONE);
        } else {
            layoutEmptyState.setVisibility(android.view.View.GONE);
            recyclerView.setVisibility(android.view.View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        shimmerViewContainer.startShimmer();
        fetchDataFromViewModel();
    }

    private void deletePlayer(int id) {
        playerViewModel.deletePlayer(id).observe(this, isSuccess -> {
            if (isSuccess != null && isSuccess) {
                showSuccessSnackbar("Đã xóa cầu thủ thành công", null, -1);
                fetchDataFromViewModel();
            } else {
                showErrorSnackbar("Lỗi mạng hoặc xóa thất bại!");
            }
        });
    }

    private void fetchDataFromViewModel() {
        playerViewModel.loadPlayers();
        playerViewModel.getPlayersLiveData().observe(this, players -> {
            if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
            
            // Dừng và ẩn Shimmer Loading
            shimmerViewContainer.stopShimmer();
            shimmerViewContainer.setVisibility(android.view.View.GONE);
            
            if (players != null) {
                originalPlayerList = players;
                applyFiltersAndSort();
            } else {
                showErrorSnackbar("Lỗi kết nối Server! Vui lòng thử lại.");
            }
        });
    }
}