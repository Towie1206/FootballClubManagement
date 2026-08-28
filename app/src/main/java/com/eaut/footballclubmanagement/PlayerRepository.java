package com.eaut.footballclubmanagement;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.eaut.footballclubmanagement.db.AppDatabase;
import com.eaut.footballclubmanagement.db.PlayerDao;
import com.eaut.footballclubmanagement.models.Player;
import com.eaut.footballclubmanagement.network.RetrofitClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayerRepository {

    private PlayerDao playerDao;

    public PlayerRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        playerDao = db.playerDao();
    }

    public LiveData<List<Player>> getPlayers() {
        MutableLiveData<List<Player>> data = new MutableLiveData<>();
        
        // 1. Trả về dữ liệu từ CSDL Cục bộ (Room) trước (Offline)
        new Thread(() -> {
            List<Player> localPlayers = playerDao.getAllPlayers();
            if (localPlayers != null && !localPlayers.isEmpty()) {
                data.postValue(localPlayers);
            }
        }).start();
        
        // 2. Fetch dữ liệu từ Server (Online) và ghi đè
        RetrofitClient.getApiService().getPlayers().enqueue(new Callback<List<Player>>() {
            @Override
            public void onResponse(Call<List<Player>> call, Response<List<Player>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Player> remotePlayers = response.body();
                    data.setValue(remotePlayers);
                    
                    // Ghi đè vào Room DB (Làm nền)
                    new Thread(() -> {
                        playerDao.deleteAll();
                        playerDao.insertAll(remotePlayers);
                    }).start();
                }
            }

            @Override
            public void onFailure(Call<List<Player>> call, Throwable t) {
                // Nếu rớt mạng, không làm gì cả, UI vẫn hiện dữ liệu từ Room ở bước 1
            }
        });
        
        return data;
    }

    public LiveData<Boolean> deletePlayer(int id) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        RetrofitClient.getApiService().deletePlayer(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                result.setValue(response.isSuccessful());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                result.setValue(false);
            }
        });
        return result;
    }

    public LiveData<Player> addPlayer(Player player) {
        MutableLiveData<Player> result = new MutableLiveData<>();
        RetrofitClient.getApiService().addPlayer(player).enqueue(new Callback<Player>() {
            @Override
            public void onResponse(Call<Player> call, Response<Player> response) {
                if (response.isSuccessful()) result.setValue(response.body());
                else result.setValue(null);
            }

            @Override
            public void onFailure(Call<Player> call, Throwable t) {
                result.setValue(null);
            }
        });
        return result;
    }
}
