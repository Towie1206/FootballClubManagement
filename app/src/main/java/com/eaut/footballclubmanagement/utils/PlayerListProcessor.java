package com.eaut.footballclubmanagement.utils;

import com.eaut.footballclubmanagement.models.Player;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/** Pure filtering/sorting logic so list behavior is deterministic and unit-testable. */
public final class PlayerListProcessor {
    private PlayerListProcessor() {
    }

    public enum SortMode {
        POSITION,
        OVR,
        GOALS
    }

    public static List<Player> filterAndSort(
            List<Player> source,
            String searchQuery,
            String positionFilter,
            SortMode sortMode
    ) {
        List<Player> result = new ArrayList<>();
        if (source == null) {
            return result;
        }

        String query = searchable(searchQuery);
        String position = positionFilter == null ? "ALL" : positionFilter.trim().toUpperCase(Locale.ROOT);
        for (Player player : source) {
            if (player == null) {
                continue;
            }
            boolean matchesName = searchable(player.getFullName()).contains(query);
            boolean matchesPosition = "ALL".equals(position)
                    || position.equals(safe(player.getPosition()).toUpperCase(Locale.ROOT));
            if (matchesName && matchesPosition) {
                result.add(player);
            }
        }

        SortMode effectiveMode = sortMode == null ? SortMode.POSITION : sortMode;
        Comparator<Player> comparator;
        if (effectiveMode == SortMode.OVR) {
            comparator = Comparator.comparingInt(Player::getOvr).reversed()
                    .thenComparing(PlayerListProcessor::safeName);
        } else if (effectiveMode == SortMode.GOALS) {
            comparator = Comparator.comparingInt(Player::getGoals).reversed()
                    .thenComparing(Comparator.comparingInt(Player::getAssists).reversed())
                    .thenComparing(PlayerListProcessor::safeName);
        } else {
            comparator = Comparator.comparingInt((Player player) -> positionRank(player.getPosition()))
                    .thenComparing(Comparator.comparingInt(Player::getOvr).reversed())
                    .thenComparing(PlayerListProcessor::safeName);
        }
        result.sort(comparator);
        return result;
    }

    private static int positionRank(String position) {
        switch (safe(position).toUpperCase(Locale.ROOT)) {
            case "FW": return 0;
            case "MF": return 1;
            case "DF": return 2;
            case "GK": return 3;
            default: return 4;
        }
    }

    private static String safeName(Player player) {
        return searchable(player == null ? null : player.getFullName());
    }

    private static String searchable(String value) {
        String lower = safe(value).toLowerCase(Locale.ROOT);
        return Normalizer.normalize(lower, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
    }

    private static String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
