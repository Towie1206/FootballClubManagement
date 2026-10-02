package com.eaut.footballclubmanagement;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.eaut.footballclubmanagement.db.AppDatabase;
import com.eaut.footballclubmanagement.db.PlayerDao;
import com.eaut.footballclubmanagement.models.Player;
import com.eaut.footballclubmanagement.network.ApiService;
import com.eaut.footballclubmanagement.network.NetworkErrorParser;
import com.eaut.footballclubmanagement.network.RetrofitClient;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayerRepository {
    private final PlayerDao playerDao;
    private final ApiService apiService;
    private static final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();
    private final AtomicInteger refreshGeneration = new AtomicInteger();
    private volatile Call<List<Player>> activeRefresh;

    public PlayerRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        playerDao = db.playerDao();
        apiService = RetrofitClient.getApiService();
    }

    public void refreshPlayers(MutableLiveData<PlayerUiState> state) {
        int generation = refreshGeneration.incrementAndGet();
        Call<List<Player>> previous = activeRefresh;
        if (previous != null) previous.cancel();

        List<Player> visiblePlayers = state.getValue() == null
                ? Collections.emptyList() : state.getValue().getPlayers();
        state.setValue(PlayerUiState.loading(visiblePlayers));

        databaseExecutor.execute(() -> {
            List<Player> cachedPlayers;
            try {
                cachedPlayers = playerDao.getAllPlayers();
                if (cachedPlayers == null || cachedPlayers.isEmpty()) {
                    cachedPlayers = com.eaut.footballclubmanagement.utils.SeedData.getInitialPlayers();
                    try {
                        playerDao.insertAll(cachedPlayers);
                    } catch (Exception ignored) {}
                }
            } catch (RuntimeException ignored) {
                cachedPlayers = com.eaut.footballclubmanagement.utils.SeedData.getInitialPlayers();
            }
            if (generation != refreshGeneration.get()) return;
            if (!cachedPlayers.isEmpty()) {
                state.postValue(PlayerUiState.loading(cachedPlayers));
            }
            requestRemotePlayers(state, generation, cachedPlayers);
        });
    }

    private void requestRemotePlayers(
            MutableLiveData<PlayerUiState> state,
            int generation,
            List<Player> cachedPlayers
    ) {
        Call<List<Player>> call = apiService.getPlayers();
        activeRefresh = call;
        call.enqueue(new Callback<List<Player>>() {
            @Override
            public void onResponse(Call<List<Player>> ignored, Response<List<Player>> response) {
                if (generation != refreshGeneration.get()) return;
                if (response.code() == 401) {
                    state.postValue(PlayerUiState.authRequired("Phiên đăng nhập đã hết hạn"));
                    return;
                }
                if (!response.isSuccessful() || response.body() == null) {
                    publishRemoteFailure(state, cachedPlayers,
                            NetworkErrorParser.message(response, "Không tải được danh sách cầu thủ"));
                    return;
                }

                List<Player> remotePlayers = response.body();
                databaseExecutor.execute(() -> {
                    try {
                        playerDao.deleteAll();
                        if (!remotePlayers.isEmpty()) playerDao.insertAll(remotePlayers);
                    } catch (RuntimeException ignoredDatabaseFailure) {
                        // Remote data is still authoritative for this session.
                    }
                    if (generation != refreshGeneration.get()) return;
                    state.postValue(remotePlayers.isEmpty()
                            ? PlayerUiState.empty()
                            : PlayerUiState.content(remotePlayers));
                });
            }

            @Override
            public void onFailure(Call<List<Player>> failedCall, Throwable throwable) {
                if (failedCall.isCanceled() || generation != refreshGeneration.get()) return;
                publishRemoteFailure(state, cachedPlayers,
                        "Đang ngoại tuyến. Không thể kết nối máy chủ");
            }
        });
    }

    private void publishRemoteFailure(
            MutableLiveData<PlayerUiState> state,
            List<Player> cachedPlayers,
            String message
    ) {
        if (cachedPlayers == null || cachedPlayers.isEmpty()) {
            cachedPlayers = com.eaut.footballclubmanagement.utils.SeedData.getInitialPlayers();
        }
        state.postValue(PlayerUiState.offline(cachedPlayers, message));
    }

    public LiveData<OperationResult<Player>> addPlayer(Player player) {
        MutableLiveData<OperationResult<Player>> result = new MutableLiveData<>();
        final Player fallbackPlayer;
        if (player.getId() <= 0) {
            fallbackPlayer = new Player(
                    (int) (System.currentTimeMillis() % 1000000),
                    player.getFullName(), player.getPosition(), player.getJerseyNumber(),
                    player.getHealthStatus(), player.getOvr(), player.getGoals(),
                    player.getAssists(), player.getMatches(), player.getMvp(),
                    player.getClub(), player.getPac(), player.getSho(),
                    player.getPas(), player.getDri(), player.getDef(), player.getPhy()
            );
        } else {
            fallbackPlayer = player;
        }

        apiService.addPlayer(fallbackPlayer).enqueue(new Callback<Player>() {
            @Override
            public void onResponse(Call<Player> call, Response<Player> response) {
                Player saved = response.body();
                if (response.isSuccessful() && saved != null) {
                    persistThenPublish(saved, result);
                } else {
                    persistThenPublish(fallbackPlayer, result);
                }
            }

            @Override
            public void onFailure(Call<Player> call, Throwable throwable) {
                persistThenPublish(fallbackPlayer, result);
            }
        });
        return result;
    }

    public LiveData<OperationResult<Player>> updatePlayer(int id, Player player) {
        MutableLiveData<OperationResult<Player>> result = new MutableLiveData<>();
        apiService.updatePlayer(id, player).enqueue(new Callback<Player>() {
            @Override
            public void onResponse(Call<Player> call, Response<Player> response) {
                Player saved = response.body();
                if (response.isSuccessful() && saved != null) {
                    persistThenPublish(saved, result);
                } else {
                    persistThenPublish(player, result);
                }
            }

            @Override
            public void onFailure(Call<Player> call, Throwable throwable) {
                persistThenPublish(player, result);
            }
        });
        return result;
    }

    private void persistThenPublish(
            Player player,
            MutableLiveData<OperationResult<Player>> result
    ) {
        databaseExecutor.execute(() -> {
            try {
                playerDao.insert(player);
                result.postValue(OperationResult.success(player));
            } catch (RuntimeException databaseFailure) {
                result.postValue(OperationResult.error(
                        "Máy chủ đã lưu nhưng bộ nhớ ngoại tuyến chưa cập nhật. Hãy tải lại danh sách"));
            }
        });
    }

    public LiveData<OperationResult<Void>> deletePlayer(int id) {
        MutableLiveData<OperationResult<Void>> result = new MutableLiveData<>();
        apiService.deletePlayer(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                deleteLocalThenPublish(id, result);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                deleteLocalThenPublish(id, result);
            }
        });
        return result;
    }

    private void deleteLocalThenPublish(int id, MutableLiveData<OperationResult<Void>> result) {
        databaseExecutor.execute(() -> {
            try {
                playerDao.deleteById(id);
                result.postValue(OperationResult.success(null));
            } catch (RuntimeException databaseFailure) {
                result.postValue(OperationResult.error("Lỗi xóa dữ liệu nội bộ"));
            }
        });
    }

    public void close() {
        refreshGeneration.incrementAndGet();
        Call<List<Player>> call = activeRefresh;
        if (call != null) call.cancel();
    }
}
