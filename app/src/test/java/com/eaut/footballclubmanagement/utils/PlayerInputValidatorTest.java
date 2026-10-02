package com.eaut.footballclubmanagement.utils;

import com.eaut.footballclubmanagement.models.Player;
import com.eaut.footballclubmanagement.utils.PlayerInputValidator.Field;
import com.eaut.footballclubmanagement.utils.PlayerInputValidator.ValidationResult;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PlayerInputValidatorTest {

    @Test
    public void validate_acceptsInclusiveBoundariesAndNormalizesInputs() {
        String oneHundredCharacterName = repeat('N', 100);
        String oneHundredCharacterClub = repeat('C', 100);

        ValidationResult minimum = validate(
                0, "", " An ", "0", " fw (Tiền đạo) ", " ",
                "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"
        );
        ValidationResult maximum = validate(
                99, " Healthy ", oneHundredCharacterName, "999", "GK", oneHundredCharacterClub,
                String.valueOf(Integer.MAX_VALUE), "10", "11", "12",
                "100", "100", "100", "100", "100", "100"
        );

        assertTrue(minimum.getErrors().toString(), minimum.isValid());
        assertEquals("An", minimum.getPlayer().getFullName());
        assertEquals(0, minimum.getPlayer().getJerseyNumber());
        assertEquals("FW", minimum.getPlayer().getPosition());
        assertEquals("Tự do", minimum.getPlayer().getClub());
        assertEquals("Fit", minimum.getPlayer().getHealthStatus());
        assertEquals(0, minimum.getPlayer().getOvr());

        assertTrue(maximum.getErrors().toString(), maximum.isValid());
        assertEquals(oneHundredCharacterName, maximum.getPlayer().getFullName());
        assertEquals(999, maximum.getPlayer().getJerseyNumber());
        assertEquals("GK", maximum.getPlayer().getPosition());
        assertEquals(oneHundredCharacterClub, maximum.getPlayer().getClub());
        assertEquals(Integer.MAX_VALUE, maximum.getPlayer().getMatches());
        assertEquals("Healthy", maximum.getPlayer().getHealthStatus());
        assertEquals(100, maximum.getPlayer().getOvr());
    }

    @Test
    public void validate_rejectsNameOutsideTwoToOneHundredCharacters() {
        assertOnlyError(validWithName("A"), Field.NAME);
        assertOnlyError(validWithName(repeat('A', 101)), Field.NAME);
        assertOnlyError(validWithName(null), Field.NAME);
    }

    @Test
    public void validate_rejectsInvalidJerseyAndIntegerParsingFailures() {
        String[] invalidValues = {"", "-1", "1000", "1.5", "seven", "2147483648", "--1"};
        for (String value : invalidValues) {
            assertOnlyError(validWithJersey(value), Field.JERSEY);
        }
    }

    @Test
    public void validate_acceptsOnlySupportedPositionCodes() {
        String[] validPositions = {"GK", "df", "MF (Tiền vệ)", " fw   (Tiền đạo) "};
        String[] expectedCodes = {"GK", "DF", "MF", "FW"};
        for (int i = 0; i < validPositions.length; i++) {
            ValidationResult result = validWithPosition(validPositions[i]);
            assertTrue(result.getErrors().toString(), result.isValid());
            assertEquals(expectedCodes[i], result.getPlayer().getPosition());
        }

        for (String invalid : new String[]{null, "", "ST", "FWX", "Tiền đạo"}) {
            assertOnlyError(validWithPosition(invalid), Field.POSITION);
        }
    }

    @Test
    public void validate_rejectsEachSkillOutsideZeroToOneHundred() {
        Field[] fields = {Field.PAC, Field.SHO, Field.PAS, Field.DRI, Field.DEF, Field.PHY};
        for (int index = 0; index < fields.length; index++) {
            for (String invalid : new String[]{"-1", "101", "abc", "2147483648", ""}) {
                String[] skills = {"70", "70", "70", "70", "70", "70"};
                skills[index] = invalid;
                ValidationResult result = validate(
                        1, "Fit", "Nguyễn An", "10", "MF", "EAUT FC",
                        "1", "1", "1", "1",
                        skills[0], skills[1], skills[2], skills[3], skills[4], skills[5]
                );
                assertOnlyError(result, fields[index]);
            }
        }
    }

    @Test
    public void validate_rejectsNegativeGarbageAndOverflowCareerStatistics() {
        Field[] fields = {Field.MATCHES, Field.GOALS, Field.ASSISTS, Field.MVP};
        for (int index = 0; index < fields.length; index++) {
            for (String invalid : new String[]{"-1", "abc", "2147483648", ""}) {
                String[] career = {"12", "5", "7", "2"};
                career[index] = invalid;
                ValidationResult result = validate(
                        1, "Fit", "Nguyễn An", "10", "MF", "EAUT FC",
                        career[0], career[1], career[2], career[3],
                        "70", "70", "70", "70", "70", "70"
                );
                assertOnlyError(result, fields[index]);
            }
        }
    }

    @Test
    public void validate_calculatesOverallUsingIntegerAverage() {
        ValidationResult result = validate(
                1, "Fit", "Nguyễn An", "10", "MF", "EAUT FC",
                "12", "5", "7", "2", "99", "88", "77", "66", "55", "44"
        );

        assertTrue(result.getErrors().toString(), result.isValid());
        assertEquals(71, result.getPlayer().getOvr());
    }

    @Test
    public void validate_editFlowPreservesEverySuppliedField() {
        ValidationResult result = validate(
                42, " Recovering ", " Trần Minh Đức ", "4", "DF (Hậu vệ)", " EAUT FC ",
                "28", "3", "6", "2", "72", "55", "75", "71", "88", "84"
        );

        assertTrue(result.getErrors().toString(), result.isValid());
        Player player = result.getPlayer();
        assertEquals(42, player.getId());
        assertEquals("Trần Minh Đức", player.getFullName());
        assertEquals("DF", player.getPosition());
        assertEquals(4, player.getJerseyNumber());
        assertEquals("Recovering", player.getHealthStatus());
        assertEquals(74, player.getOvr());
        assertEquals(3, player.getGoals());
        assertEquals(6, player.getAssists());
        assertEquals(28, player.getMatches());
        assertEquals(2, player.getMvp());
        assertEquals("EAUT FC", player.getClub());
        assertEquals(72, player.getPac());
        assertEquals(55, player.getSho());
        assertEquals(75, player.getPas());
        assertEquals(71, player.getDri());
        assertEquals(88, player.getDef());
        assertEquals(84, player.getPhy());
    }

    @Test
    public void invalidResultHasNoPlayerAndExposesImmutableErrors() {
        ValidationResult result = validate(
                1, "Fit", "", "bad", "ST", repeat('C', 101),
                "-1", "-1", "-1", "-1", "-1", "101", "bad", "", "-4", "1000"
        );

        assertFalse(result.isValid());
        assertNull(result.getPlayer());
        assertEquals(14, result.getErrors().size());
        try {
            result.getErrors().put(Field.NAME, "changed");
            fail("Validation errors must be immutable");
        } catch (UnsupportedOperationException expected) {
            // Expected: callers must not be able to corrupt a completed validation result.
        }
    }

    private static ValidationResult validWithName(String name) {
        return validate(1, "Fit", name, "10", "MF", "EAUT FC",
                "1", "1", "1", "1", "70", "70", "70", "70", "70", "70");
    }

    private static ValidationResult validWithJersey(String jersey) {
        return validate(1, "Fit", "Nguyễn An", jersey, "MF", "EAUT FC",
                "1", "1", "1", "1", "70", "70", "70", "70", "70", "70");
    }

    private static ValidationResult validWithPosition(String position) {
        return validate(1, "Fit", "Nguyễn An", "10", position, "EAUT FC",
                "1", "1", "1", "1", "70", "70", "70", "70", "70", "70");
    }

    private static ValidationResult validate(
            int id,
            String health,
            String name,
            String jersey,
            String position,
            String club,
            String matches,
            String goals,
            String assists,
            String mvp,
            String pac,
            String sho,
            String pas,
            String dri,
            String def,
            String phy
    ) {
        return PlayerInputValidator.validate(
                id, health, name, jersey, position, club,
                matches, goals, assists, mvp, pac, sho, pas, dri, def, phy
        );
    }

    private static void assertOnlyError(ValidationResult result, Field expectedField) {
        assertFalse("Expected validation to fail", result.isValid());
        assertNull(result.getPlayer());
        Map<Field, String> errors = result.getErrors();
        assertEquals(errors.toString(), 1, errors.size());
        assertTrue(errors.toString(), errors.containsKey(expectedField));
        assertNotNull(errors.get(expectedField));
        assertFalse(errors.get(expectedField).trim().isEmpty());
    }

    private static String repeat(char value, int count) {
        StringBuilder result = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            result.append(value);
        }
        return result.toString();
    }
}
