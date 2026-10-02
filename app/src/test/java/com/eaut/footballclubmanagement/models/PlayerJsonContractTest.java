package com.eaut.footballclubmanagement.models;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PlayerJsonContractTest {

    private final Gson gson = new Gson();

    @Test
    public void deserializeBackendPlayer_preservesEveryProfileAndStatisticField() {
        String payload = "{"
                + "\"id\":9,"
                + "\"fullName\":\"Nguyễn Quang Hải\","
                + "\"position\":\"MF\","
                + "\"jerseyNumber\":19,"
                + "\"healthStatus\":\"Fit\","
                + "\"ovr\":86,"
                + "\"goals\":12,"
                + "\"assists\":18,"
                + "\"matches\":30,"
                + "\"mvp\":7,"
                + "\"club\":\"FC Hà Nội\","
                + "\"pac\":84,"
                + "\"sho\":82,"
                + "\"pas\":91,"
                + "\"dri\":90,"
                + "\"def\":74,"
                + "\"phy\":76"
                + "}";

        Player player = gson.fromJson(payload, Player.class);

        assertEquals(9, player.getId());
        assertEquals("Nguyễn Quang Hải", player.getFullName());
        assertEquals("MF", player.getPosition());
        assertEquals(19, player.getJerseyNumber());
        assertEquals("Fit", player.getHealthStatus());
        assertEquals(86, player.getOvr());
        assertEquals(12, player.getGoals());
        assertEquals(18, player.getAssists());
        assertEquals(30, player.getMatches());
        assertEquals(7, player.getMvp());
        assertEquals("FC Hà Nội", player.getClub());
        assertEquals(84, player.getPac());
        assertEquals(82, player.getSho());
        assertEquals(91, player.getPas());
        assertEquals(90, player.getDri());
        assertEquals(74, player.getDef());
        assertEquals(76, player.getPhy());
    }

    @Test
    public void serializePlayer_usesTheCamelCaseContractExpectedByBackend() {
        Player player = new Player(
                9, "Nguyễn Quang Hải", "MF", 19, "Fit", 86,
                12, 18, 30, 7, "FC Hà Nội", 84, 82, 91, 90, 74, 76
        );

        JsonObject json = new JsonParser().parse(gson.toJson(player)).getAsJsonObject();
        Set<String> expectedFields = new HashSet<>(Arrays.asList(
                "id", "fullName", "position", "jerseyNumber", "healthStatus", "ovr",
                "goals", "assists", "matches", "mvp", "club", "pac", "sho", "pas",
                "dri", "def", "phy"
        ));

        assertEquals(expectedFields, json.keySet());
        assertEquals("Nguyễn Quang Hải", json.get("fullName").getAsString());
        assertEquals(19, json.get("jerseyNumber").getAsInt());
        assertEquals(18, json.get("assists").getAsInt());
        assertEquals(91, json.get("pas").getAsInt());
    }

    @Test
    public void gsonRoundTrip_doesNotDropDataRequiredByEditFlow() {
        Player original = new Player(
                21, "Trần Minh Đức", "DF", 4, "Recovering", 79,
                3, 6, 28, 2, "EAUT FC", 72, 55, 75, 71, 88, 84
        );

        Player restored = gson.fromJson(gson.toJson(original), Player.class);

        assertEquals(original.getId(), restored.getId());
        assertEquals(original.getFullName(), restored.getFullName());
        assertEquals(original.getPosition(), restored.getPosition());
        assertEquals(original.getJerseyNumber(), restored.getJerseyNumber());
        assertEquals(original.getHealthStatus(), restored.getHealthStatus());
        assertEquals(original.getOvr(), restored.getOvr());
        assertEquals(original.getGoals(), restored.getGoals());
        assertEquals(original.getAssists(), restored.getAssists());
        assertEquals(original.getMatches(), restored.getMatches());
        assertEquals(original.getMvp(), restored.getMvp());
        assertEquals(original.getClub(), restored.getClub());
        assertEquals(original.getPac(), restored.getPac());
        assertEquals(original.getSho(), restored.getSho());
        assertEquals(original.getPas(), restored.getPas());
        assertEquals(original.getDri(), restored.getDri());
        assertEquals(original.getDef(), restored.getDef());
        assertEquals(original.getPhy(), restored.getPhy());
        assertTrue(restored.getOvr() > 0);
    }
}
