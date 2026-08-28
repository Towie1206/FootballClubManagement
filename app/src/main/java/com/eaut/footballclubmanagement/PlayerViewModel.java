package com.eaut.footballclubmanagement;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.eaut.footballclubmanagement.models.Player;
import java.util.List;

public class PlayerViewModel extends AndroidViewModel {

    private PlayerRepository repository;
    private LiveData<List<Player>> playersLiveData;

    public PlayerViewModel(@NonNull Application application) {
        super(application);
        repository = new PlayerRepository(application);
    }

    // Trigger load danh sách
    public void loadPlayers() {
        playersLiveData = repository.getPlayers();
    }

    public LiveData<List<Player>> getPlayersLiveData() {
        return playersLiveData;
    }

    public LiveData<Boolean> deletePlayer(int id) {
        return repository.deletePlayer(id);
    }

    public LiveData<Player> addPlayer(Player player) {
        return repository.addPlayer(player);
    }
}
