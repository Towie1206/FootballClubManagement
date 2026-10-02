package com.eaut.footballclubmanagement;

import com.eaut.footballclubmanagement.models.Player;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ApplicationStateTest {

    @Test
    public void operationResult_factoriesKeepStatusDataAndMessageConsistent() {
        Player player = player(1);
        OperationResult<Player> success = OperationResult.success(player);
        OperationResult<Player> error = OperationResult.error("Không lưu được");
        OperationResult<Player> authRequired = OperationResult.authRequired("Hết phiên");

        assertEquals(OperationResult.Status.SUCCESS, success.getStatus());
        assertSame(player, success.getData());
        assertNull(success.getMessage());
        assertTrue(success.isSuccess());

        assertEquals(OperationResult.Status.ERROR, error.getStatus());
        assertNull(error.getData());
        assertEquals("Không lưu được", error.getMessage());
        assertFalse(error.isSuccess());

        assertEquals(OperationResult.Status.AUTH_REQUIRED, authRequired.getStatus());
        assertNull(authRequired.getData());
        assertEquals("Hết phiên", authRequired.getMessage());
        assertFalse(authRequired.isSuccess());
    }

    @Test
    public void playerUiState_exposesCorrectStatusForEachFactory() {
        List<Player> players = Collections.singletonList(player(1));

        assertState(PlayerUiState.loading(players), PlayerUiState.Status.LOADING, 1, null);
        assertState(PlayerUiState.content(players), PlayerUiState.Status.CONTENT, 1, null);
        assertState(PlayerUiState.empty(), PlayerUiState.Status.EMPTY, 0, null);
        assertState(PlayerUiState.offline(players, "Đang dùng dữ liệu lưu"),
                PlayerUiState.Status.OFFLINE, 1, "Đang dùng dữ liệu lưu");
        assertState(PlayerUiState.error("Lỗi mạng"), PlayerUiState.Status.ERROR, 0, "Lỗi mạng");
        assertState(PlayerUiState.authRequired("Hết phiên"),
                PlayerUiState.Status.AUTH_REQUIRED, 0, "Hết phiên");
    }

    @Test
    public void playerUiState_takesDefensiveSnapshotAndReturnsImmutablePlayers() {
        List<Player> mutableSource = new ArrayList<>();
        mutableSource.add(player(1));
        PlayerUiState state = PlayerUiState.content(mutableSource);

        mutableSource.add(player(2));
        assertEquals(1, state.getPlayers().size());

        try {
            state.getPlayers().add(player(3));
            fail("UI state players must be immutable");
        } catch (UnsupportedOperationException expected) {
            // Expected: observers receive a stable snapshot.
        }
    }

    @Test
    public void playerUiState_treatsNullPlayerListsAsEmpty() {
        assertTrue(PlayerUiState.loading(null).getPlayers().isEmpty());
        assertTrue(PlayerUiState.content(null).getPlayers().isEmpty());
        assertTrue(PlayerUiState.offline(null, "Không có cache").getPlayers().isEmpty());
    }

    private static void assertState(
            PlayerUiState state,
            PlayerUiState.Status status,
            int playerCount,
            String message
    ) {
        assertEquals(status, state.getStatus());
        assertEquals(playerCount, state.getPlayers().size());
        assertEquals(message, state.getMessage());
    }

    private static Player player(int id) {
        return new Player(
                id, "Player " + id, "MF", id, "Fit", 75,
                1, 2, 3, 1, "EAUT FC", 70, 70, 70, 70, 70, 70
        );
    }
}
