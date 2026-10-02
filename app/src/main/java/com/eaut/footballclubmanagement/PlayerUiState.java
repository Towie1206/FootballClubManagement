package com.eaut.footballclubmanagement;

import com.eaut.footballclubmanagement.models.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PlayerUiState {
    public enum Status { LOADING, CONTENT, EMPTY, OFFLINE, ERROR, AUTH_REQUIRED }

    private final Status status;
    private final List<Player> players;
    private final String message;

    private PlayerUiState(Status status, List<Player> players, String message) {
        this.status = status;
        this.players = Collections.unmodifiableList(new ArrayList<>(
                players == null ? Collections.emptyList() : players));
        this.message = message;
    }

    public static PlayerUiState loading(List<Player> currentPlayers) {
        return new PlayerUiState(Status.LOADING, currentPlayers, null);
    }

    public static PlayerUiState content(List<Player> players) {
        return new PlayerUiState(Status.CONTENT, players, null);
    }

    public static PlayerUiState empty() {
        return new PlayerUiState(Status.EMPTY, Collections.emptyList(), null);
    }

    public static PlayerUiState offline(List<Player> cachedPlayers, String message) {
        return new PlayerUiState(Status.OFFLINE, cachedPlayers, message);
    }

    public static PlayerUiState error(String message) {
        return new PlayerUiState(Status.ERROR, Collections.emptyList(), message);
    }

    public static PlayerUiState authRequired(String message) {
        return new PlayerUiState(Status.AUTH_REQUIRED, Collections.emptyList(), message);
    }

    public Status getStatus() { return status; }
    public List<Player> getPlayers() { return players; }
    public String getMessage() { return message; }
}
