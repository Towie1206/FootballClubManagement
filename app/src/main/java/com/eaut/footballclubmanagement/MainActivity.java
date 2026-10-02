package com.eaut.footballclubmanagement;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eaut.footballclubmanagement.models.Player;
import com.eaut.footballclubmanagement.utils.PlayerIntent;
import com.eaut.footballclubmanagement.utils.PlayerListProcessor;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PlayerAdapter adapter;
    private EditText etSearch;
    private View layoutEmptyState;
    private TextView tvEmptyTitle;
    private TextView tvEmptyMessage;
    private AppCompatButton btnRetry;
    private ShimmerFrameLayout shimmerViewContainer;
    private androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefresh;
    private PlayerViewModel playerViewModel;
    private List<Player> originalPlayerList = new ArrayList<>();
    private String currentSearchQuery = "";
    private String currentFilterPosition = "ALL";
    private PlayerListProcessor.SortMode currentSortMode = PlayerListProcessor.SortMode.POSITION;
    private boolean redirectedForAuth;

    private final ActivityResultLauncher<Intent> playerScreenLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && playerViewModel != null) {
                    playerViewModel.refreshPlayers();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        playerViewModel = new ViewModelProvider(this).get(PlayerViewModel.class);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.swipeRefreshLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        bindViews();
        setupPlayerList();
        setupBottomNavigation();
        setupSearchAndFilters();

        findViewById(R.id.btnLogout).setOnClickListener(v -> confirmLogout());

        swipeRefresh.setOnRefreshListener(playerViewModel::refreshPlayers);
        btnRetry.setOnClickListener(v -> playerViewModel.refreshPlayers());
        playerViewModel.getPlayerState().observe(this, this::renderState);
        playerViewModel.refreshPlayers();
    }

    private void bindViews() {
        recyclerView = findViewById(R.id.recyclerViewPlayers);
        etSearch = findViewById(R.id.etSearch);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        tvEmptyTitle = findViewById(R.id.tvEmptyTitle);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);
        btnRetry = findViewById(R.id.btnRetry);
        shimmerViewContainer = findViewById(R.id.shimmerViewContainer);
        swipeRefresh = findViewById(R.id.swipeRefreshLayout);
    }

    private void setupPlayerList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PlayerAdapter(new ArrayList<>(), new PlayerAdapter.OnPlayerClickListener() {
            @Override
            public void onEdit(Player player, View cardRoot) {
                Intent intent = new Intent(MainActivity.this, PlayerDetailActivity.class);
                PlayerIntent.putPlayer(intent, player);
                androidx.core.app.ActivityOptionsCompat options =
                        androidx.core.app.ActivityOptionsCompat.makeSceneTransitionAnimation(
                                MainActivity.this, cardRoot, "player_card_transition");
                playerScreenLauncher.launch(intent, options);
            }

            @Override
            public void onDelete(Player player) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.delete_player_title)
                        .setMessage(getString(R.string.delete_player_message, safe(player.getFullName())))
                        .setPositiveButton(R.string.delete_action,
                                (dialog, which) -> deletePlayer(player.getId()))
                        .setNegativeButton(R.string.cancel_action, null)
                        .show();
            }
        });
        recyclerView.setAdapter(adapter);

        FloatingActionButton addButton = findViewById(R.id.btnAddPlayer);
        addButton.setOnClickListener(v -> playerScreenLauncher.launch(
                new Intent(this, PlayerFormActivity.class)));
    }

    private void setupSearchAndFilters() {
        AppCompatButton btnSort = findViewById(R.id.btnSort);
        AppCompatButton filterAll = findViewById(R.id.filterAll);
        AppCompatButton filterFW = findViewById(R.id.filterFW);
        AppCompatButton filterMF = findViewById(R.id.filterMF);
        AppCompatButton filterDF = findViewById(R.id.filterDF);
        AppCompatButton filterGK = findViewById(R.id.filterGK);
        AppCompatButton[] filterButtons = {filterAll, filterFW, filterMF, filterDF, filterGK};

        updateSortButton(btnSort);
        btnSort.setOnClickListener(v -> showSortDialog(btnSort));

        View.OnClickListener filterListener = view -> {
            for (AppCompatButton button : filterButtons) {
                button.setBackgroundResource(R.drawable.bg_input);
                button.setTextColor(ContextCompat.getColor(this, R.color.phui_text_primary));
            }
            AppCompatButton selected = (AppCompatButton) view;
            selected.setBackgroundResource(R.drawable.bg_button_primary);
            selected.setTextColor(ContextCompat.getColor(this, R.color.phui_bg));
            if (selected == filterFW) currentFilterPosition = "FW";
            else if (selected == filterMF) currentFilterPosition = "MF";
            else if (selected == filterDF) currentFilterPosition = "DF";
            else if (selected == filterGK) currentFilterPosition = "GK";
            else currentFilterPosition = "ALL";
            applyFiltersAndSort();
        };
        for (AppCompatButton button : filterButtons) button.setOnClickListener(filterListener);

        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override public void afterTextChanged(android.text.Editable s) {
                currentSearchQuery = s == null ? "" : s.toString();
                applyFiltersAndSort();
            }
        });
    }

    private void showSortDialog(AppCompatButton btnSort) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_sort_players, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        RadioButton rbSortPosition = dialogView.findViewById(R.id.rbSortPosition);
        RadioButton rbSortOvr = dialogView.findViewById(R.id.rbSortOvr);
        RadioButton rbSortGoals = dialogView.findViewById(R.id.rbSortGoals);

        View rowSortPosition = dialogView.findViewById(R.id.rowSortPosition);
        View rowSortOvr = dialogView.findViewById(R.id.rowSortOvr);
        View rowSortGoals = dialogView.findViewById(R.id.rowSortGoals);

        // Pre-select current
        if (currentSortMode == PlayerListProcessor.SortMode.OVR) {
            rbSortOvr.setChecked(true);
        } else if (currentSortMode == PlayerListProcessor.SortMode.GOALS) {
            rbSortGoals.setChecked(true);
        } else {
            rbSortPosition.setChecked(true);
        }

        rowSortPosition.setOnClickListener(v -> {
            rbSortPosition.setChecked(true);
            rbSortOvr.setChecked(false);
            rbSortGoals.setChecked(false);
        });

        rowSortOvr.setOnClickListener(v -> {
            rbSortPosition.setChecked(false);
            rbSortOvr.setChecked(true);
            rbSortGoals.setChecked(false);
        });

        rowSortGoals.setOnClickListener(v -> {
            rbSortPosition.setChecked(false);
            rbSortOvr.setChecked(false);
            rbSortGoals.setChecked(true);
        });

        dialogView.findViewById(R.id.btnSortCancel).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.btnSortApply).setOnClickListener(v -> {
            if (rbSortOvr.isChecked()) {
                currentSortMode = PlayerListProcessor.SortMode.OVR;
            } else if (rbSortGoals.isChecked()) {
                currentSortMode = PlayerListProcessor.SortMode.GOALS;
            } else {
                currentSortMode = PlayerListProcessor.SortMode.POSITION;
            }
            updateSortButton(btnSort);
            applyFiltersAndSort();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void updateSortButton(AppCompatButton button) {
        if (currentSortMode == PlayerListProcessor.SortMode.OVR) {
            button.setText("⭐ OVR ▾");
        } else if (currentSortMode == PlayerListProcessor.SortMode.GOALS) {
            button.setText("⚽ Goals ▾");
        } else {
            button.setText("📋 Position ▾");
        }
    }

    private void renderState(PlayerUiState state) {
        if (state == null) return;
        boolean hasPlayers = !state.getPlayers().isEmpty();
        if (state.getStatus() == PlayerUiState.Status.LOADING) {
            if (hasPlayers) {
                originalPlayerList = state.getPlayers();
                stopShimmer();
                swipeRefresh.setRefreshing(true);
                applyFiltersAndSort();
            } else {
                showShimmer();
            }
            return;
        }

        swipeRefresh.setRefreshing(false);
        stopShimmer();
        switch (state.getStatus()) {
            case CONTENT:
                originalPlayerList = state.getPlayers();
                applyFiltersAndSort();
                break;
            case EMPTY:
                originalPlayerList = new ArrayList<>();
                adapter.updateData(new ArrayList<>());
                showEmpty(R.string.empty_players_title, R.string.empty_players_message, false);
                break;
            case OFFLINE:
                originalPlayerList = state.getPlayers();
                applyFiltersAndSort();
                if (originalPlayerList.isEmpty()) {
                    showErrorSnackbar(state.getMessage());
                }
                break;
            case ERROR:
                originalPlayerList = new ArrayList<>();
                adapter.updateData(new ArrayList<>());
                showEmpty(R.string.load_error_title, R.string.load_error_message, true);
                showErrorSnackbar(state.getMessage());
                break;
            case AUTH_REQUIRED:
                redirectToLogin();
                break;
            default:
                break;
        }
    }

    private void applyFiltersAndSort() {
        List<Player> filtered = PlayerListProcessor.filterAndSort(
                originalPlayerList, currentSearchQuery, currentFilterPosition, currentSortMode);
        List<Object> items = new ArrayList<>();
        if (currentSortMode == PlayerListProcessor.SortMode.POSITION) {
            String previousPosition = null;
            for (Player player : filtered) {
                String position = safe(player.getPosition());
                if (!position.equals(previousPosition)) {
                    items.add(positionHeader(position));
                    previousPosition = position;
                }
                items.add(player);
            }
        } else {
            items.addAll(filtered);
        }
        adapter.updateData(items);
        if (items.isEmpty()) {
            showEmpty(R.string.no_filter_results_title, R.string.no_filter_results_message, false);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private String positionHeader(String position) {
        switch (position) {
            case "FW": return getString(R.string.position_header_fw);
            case "MF": return getString(R.string.position_header_mf);
            case "DF": return getString(R.string.position_header_df);
            case "GK": return getString(R.string.position_header_gk);
            default: return getString(R.string.position_header_other);
        }
    }

    private void showShimmer() {
        layoutEmptyState.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        shimmerViewContainer.setVisibility(View.VISIBLE);
        shimmerViewContainer.startShimmer();
    }

    private void stopShimmer() {
        shimmerViewContainer.stopShimmer();
        shimmerViewContainer.setVisibility(View.GONE);
    }

    private void showEmpty(int titleResource, int messageResource, boolean canRetry) {
        tvEmptyTitle.setText(titleResource);
        tvEmptyMessage.setText(messageResource);
        btnRetry.setVisibility(canRetry ? View.VISIBLE : View.GONE);
        layoutEmptyState.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void deletePlayer(int id) {
        playerViewModel.deletePlayer(id).observe(this, result -> {
            if (result == null) return;
            if (result.getStatus() == OperationResult.Status.AUTH_REQUIRED) {
                redirectToLogin();
            } else if (result.isSuccess()) {
                List<Player> updated = new ArrayList<>();
                for (Player p : originalPlayerList) {
                    if (p.getId() != id) {
                        updated.add(p);
                    }
                }
                originalPlayerList = updated;
                applyFiltersAndSort();
                Snackbar.make(recyclerView, R.string.player_deleted, Snackbar.LENGTH_SHORT).show();
            } else {
                showErrorSnackbar(result.getMessage());
            }
        });
    }

    private void showErrorSnackbar(String message) {
        if (message == null || message.trim().isEmpty()) return;
        Snackbar snackbar = Snackbar.make(findViewById(R.id.swipeRefreshLayout), message, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.phui_error));
        snackbar.show();
    }

    private void redirectToLogin() {
        if (redirectedForAuth) return;
        redirectedForAuth = true;
        Toast.makeText(this, R.string.session_expired, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.nav_home);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                if (adapter.getItemCount() > 0) recyclerView.smoothScrollToPosition(0);
                return true;
            }
            if (id == R.id.nav_dashboard) {
                Intent intent = new Intent(this, DashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return false;
            }
            return false;
        });
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out of the system?")
                .setPositiveButton("Log Out", (dialog, which) -> {
                    new com.eaut.footballclubmanagement.network.SessionManager(this).clearAuth();
                    redirectToLogin();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }

    @Override
    protected void onDestroy() {
        shimmerViewContainer.stopShimmer();
        recyclerView.setAdapter(null);
        super.onDestroy();
    }
}
