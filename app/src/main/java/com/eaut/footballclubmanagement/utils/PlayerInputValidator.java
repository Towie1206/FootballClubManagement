package com.eaut.footballclubmanagement.utils;

import com.eaut.footballclubmanagement.models.Player;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

/** Pure validation and mapping logic shared by the player form and JVM tests. */
public final class PlayerInputValidator {
    private PlayerInputValidator() {
    }

    public enum Field {
        NAME,
        JERSEY,
        POSITION,
        CLUB,
        MATCHES,
        GOALS,
        ASSISTS,
        MVP,
        PAC,
        SHO,
        PAS,
        DRI,
        DEF,
        PHY
    }

    public static final class ValidationResult {
        private final Player player;
        private final Map<Field, String> errors;

        private ValidationResult(Player player, Map<Field, String> errors) {
            this.player = player;
            this.errors = Collections.unmodifiableMap(new EnumMap<>(errors));
        }

        public boolean isValid() {
            return errors.isEmpty();
        }

        public Player getPlayer() {
            return player;
        }

        public Map<Field, String> getErrors() {
            return errors;
        }
    }

    public static ValidationResult validate(
            int id,
            String healthStatus,
            String nameInput,
            String jerseyInput,
            String positionInput,
            String clubInput,
            String matchesInput,
            String goalsInput,
            String assistsInput,
            String mvpInput,
            String pacInput,
            String shoInput,
            String pasInput,
            String driInput,
            String defInput,
            String phyInput
    ) {
        EnumMap<Field, String> errors = new EnumMap<>(Field.class);

        String name = trim(nameInput);
        if (name.length() < 2 || name.length() > 100) {
            errors.put(Field.NAME, "Player name must be between 2 and 100 characters");
        }

        Integer jersey = parseInRange(jerseyInput, 0, 999, Field.JERSEY, errors,
                "Jersey number must be between 0 and 999");

        String position = normalizePosition(positionInput);
        if (position == null) {
            errors.put(Field.POSITION, "Please select GK, DF, MF or FW");
        }

        String club = trim(clubInput);
        if (club.isEmpty()) {
            club = "Tự do";
        } else if (club.length() > 100) {
            errors.put(Field.CLUB, "Club name must not exceed 100 characters");
        }

        Integer matches = parseNonNegative(matchesInput, Field.MATCHES, errors);
        Integer goals = parseNonNegative(goalsInput, Field.GOALS, errors);
        Integer assists = parseNonNegative(assistsInput, Field.ASSISTS, errors);
        Integer mvp = parseNonNegative(mvpInput, Field.MVP, errors);
        Integer pac = parseStat(pacInput, Field.PAC, errors);
        Integer sho = parseStat(shoInput, Field.SHO, errors);
        Integer pas = parseStat(pasInput, Field.PAS, errors);
        Integer dri = parseStat(driInput, Field.DRI, errors);
        Integer def = parseStat(defInput, Field.DEF, errors);
        Integer phy = parseStat(phyInput, Field.PHY, errors);

        if (!errors.isEmpty()) {
            return new ValidationResult(null, errors);
        }

        int ovr = (pac + sho + pas + dri + def + phy) / 6;
        String normalizedHealth = trim(healthStatus);
        if (normalizedHealth.isEmpty()) {
            normalizedHealth = "Fit";
        }
        Player player = new Player(id, name, position, jersey, normalizedHealth, ovr,
                goals, assists, matches, mvp, club, pac, sho, pas, dri, def, phy);
        return new ValidationResult(player, errors);
    }

    private static Integer parseStat(String value, Field field, Map<Field, String> errors) {
        return parseInRange(value, 0, 100, field, errors,
                field.name() + " must be between 0 and 100");
    }

    private static Integer parseNonNegative(String value, Field field, Map<Field, String> errors) {
        return parseInRange(value, 0, Integer.MAX_VALUE, field, errors,
                "Value must be a non-negative integer");
    }

    private static Integer parseInRange(
            String value,
            int minimum,
            int maximum,
            Field field,
            Map<Field, String> errors,
            String message
    ) {
        String normalized = trim(value);
        if (normalized.isEmpty()) {
            errors.put(field, message);
            return null;
        }
        try {
            int parsed = Integer.parseInt(normalized);
            if (parsed < minimum || parsed > maximum) {
                errors.put(field, message);
                return null;
            }
            return parsed;
        } catch (NumberFormatException ignored) {
            errors.put(field, message);
            return null;
        }
    }

    private static String normalizePosition(String value) {
        String normalized = trim(value).toUpperCase(Locale.ROOT);
        if (normalized.isEmpty()) {
            return null;
        }
        String code = normalized.split("\\s+", 2)[0];
        switch (code) {
            case "GK":
            case "DF":
            case "MF":
            case "FW":
                return code;
            default:
                return null;
        }
    }

    private static String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
