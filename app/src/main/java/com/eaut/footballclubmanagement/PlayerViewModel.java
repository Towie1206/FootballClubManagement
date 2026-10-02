package com.eaut.footballclubmanagement;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.eaut.footballclubmanagement.models.Player;

import java.util.Collections;

public class PlayerViewModel extends AndroidViewModel {
    private final PlayerRepository repository;
    private final MutableLiveData<PlayerUiState> playerState =
            new MutableLiveData<>(PlayerUiState.loading(Collections.emptyList()));

    public PlayerViewModel(@NonNull Application application) {
        super(application);
        repository = new PlayerRepository(application);
    }

    public void refreshPlayers() {
        repository.refreshPlayers(playerState);
    }

    public LiveData<PlayerUiState> getPlayerState() {
        return playerState;
    }

    public LiveData<OperationResult<Void>> deletePlayer(int id) {
        return repository.deletePlayer(id);
    }

    public LiveData<OperationResult<Player>> addPlayer(Player player) {
        return repository.addPlayer(player);
    }

    public LiveData<OperationResult<Player>> updatePlayer(int id, Player player) {
        return repository.updatePlayer(id, player);
    }

    @Override
    protected void onCleared() {
        repository.close();
        super.onCleared();
    }
}
