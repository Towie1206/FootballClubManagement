package com.eaut.footballclubmanagement.utils;

import com.eaut.footballclubmanagement.models.Player;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.eaut.footballclubmanagement.utils.PlayerListProcessor.SortMode.GOALS;
import static com.eaut.footballclubmanagement.utils.PlayerListProcessor.SortMode.OVR;
import static com.eaut.footballclubmanagement.utils.PlayerListProcessor.SortMode.POSITION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

public class PlayerListProcessorTest {

    @Test
    public void filterAndSort_searchesVietnameseNamesWithoutRequiringAccentsOrCase() {
        List<Player> players = Arrays.asList(
                player(1, "Nguyễn Quang Hải", "MF", 86, 12, 18),
                player(2, "Đỗ Hùng Dũng", "MF", 84, 5, 11),
                player(3, "Nguyễn Tiến Linh", "FW", 82, 20, 4)
        );

        List<Player> result = PlayerListProcessor.filterAndSort(players, "  NGUYEN  ", "all", OVR);

        assertIds(result, 1, 3);
    }

    @Test
    public void filterAndSort_combinesPositionFilterWithSearch() {
        List<Player> players = Arrays.asList(
                player(1, "Nguyễn Quang Hải", "MF", 86, 12, 18),
                player(2, "Nguyễn Tiến Linh", "FW", 82, 20, 4),
                player(3, "Nguyễn Văn Toàn", "FW", 80, 15, 7)
        );

        List<Player> result = PlayerListProcessor.filterAndSort(players, "nguyen", " fw ", GOALS);

        assertIds(result, 2, 3);
    }

    @Test
    public void positionSort_usesFootballOrderThenOverallThenName() {
        List<Player> players = Arrays.asList(
                player(1, "GK One", "GK", 90, 0, 0),
                player(2, "DF One", "DF", 80, 0, 0),
                player(3, "MF One", "MF", 70, 0, 0),
                player(4, "FW Low", "FW", 75, 0, 0),
                player(5, "FW Ánh", "FW", 88, 0, 0),
                player(6, "FW Bảo", "FW", 88, 0, 0),
                player(7, "Unknown", "ST", 99, 0, 0)
        );

        List<Player> result = PlayerListProcessor.filterAndSort(players, null, null, POSITION);

        assertIds(result, 5, 6, 4, 3, 2, 1, 7);
    }

    @Test
    public void overallSort_isDescendingAndUsesNameAsStableTieBreaker() {
        List<Player> players = Arrays.asList(
                player(1, "Bình", "DF", 80, 1, 1),
                player(2, "Ánh", "GK", 80, 1, 1),
                player(3, "Cường", "FW", 90, 1, 1)
        );

        List<Player> result = PlayerListProcessor.filterAndSort(players, "", "ALL", OVR);

        assertIds(result, 3, 2, 1);
    }

    @Test
    public void goalsSort_breaksTiesByAssistsThenName() {
        List<Player> players = Arrays.asList(
                player(1, "Cường", "FW", 80, 10, 4),
                player(2, "Bình", "MF", 80, 10, 7),
                player(3, "Ánh", "DF", 80, 10, 7),
                player(4, "Dũng", "GK", 80, 12, 0)
        );

        List<Player> result = PlayerListProcessor.filterAndSort(players, "", "ALL", GOALS);

        assertIds(result, 4, 3, 2, 1);
    }

    @Test
    public void filterAndSort_doesNotMutateSourceAndSkipsNullPlayers() {
        Player first = player(1, "First", "MF", 50, 1, 0);
        Player second = player(2, "Second", "FW", 99, 2, 0);
        List<Player> source = new ArrayList<>(Arrays.asList(first, null, second));

        List<Player> result = PlayerListProcessor.filterAndSort(source, null, null, OVR);

        assertNotSame(source, result);
        assertEquals(Arrays.asList(first, null, second), source);
        assertIds(result, 2, 1);
    }

    @Test
    public void filterAndSort_handlesNullAndEmptySources() {
        assertTrue(PlayerListProcessor.filterAndSort(null, null, null, null).isEmpty());
        assertTrue(PlayerListProcessor.filterAndSort(Collections.emptyList(), "x", "FW", GOALS).isEmpty());
    }

    private static Player player(int id, String name, String position, int ovr, int goals, int assists) {
        return new Player(
                id, name, position, id, "Fit", ovr, goals, assists, 20, 1,
                "EAUT FC", 70, 70, 70, 70, 70, 70
        );
    }

    private static void assertIds(List<Player> actual, int... expectedIds) {
        List<Integer> actualIds = new ArrayList<>();
        for (Player player : actual) {
            actualIds.add(player.getId());
        }
        List<Integer> expected = new ArrayList<>();
        for (int id : expectedIds) {
            expected.add(id);
        }
        assertEquals(expected, actualIds);
    }
}
