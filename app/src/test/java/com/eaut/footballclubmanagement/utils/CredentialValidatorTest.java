package com.eaut.footballclubmanagement.utils;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class CredentialValidatorTest {

    @Test
    public void username_acceptsBoundaryLengthsAndAllowedPunctuation() {
        assertNull(CredentialValidator.usernameError("abc"));
        assertNull(CredentialValidator.usernameError(repeat('a', 32)));
        assertNull(CredentialValidator.usernameError(" captain.name_7-test "));
    }

    @Test
    public void username_rejectsMissingBadLengthWhitespaceAndUnsupportedCharacters() {
        assertNotNull(CredentialValidator.usernameError(null));
        assertNotNull(CredentialValidator.usernameError("ab"));
        assertNotNull(CredentialValidator.usernameError(repeat('a', 33)));
        assertNotNull(CredentialValidator.usernameError("captain name"));
        assertNotNull(CredentialValidator.usernameError("đội_trưởng"));
        assertNotNull(CredentialValidator.usernameError("captain@club"));
    }

    @Test
    public void password_acceptsBoundaryLengthsWhenLetterAndDigitArePresent() {
        assertNull(CredentialValidator.passwordError("abc12345"));
        assertNull(CredentialValidator.passwordError("a1" + repeat('x', 126)));
    }

    @Test
    public void password_rejectsMissingBadLengthOrMissingCharacterClass() {
        assertNotNull(CredentialValidator.passwordError(null));
        assertNotNull(CredentialValidator.passwordError("abc1234"));
        assertNotNull(CredentialValidator.passwordError("a1" + repeat('x', 127)));
        assertNotNull(CredentialValidator.passwordError("abcdefgh"));
        assertNotNull(CredentialValidator.passwordError("12345678"));
    }

    private static String repeat(char value, int count) {
        StringBuilder result = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            result.append(value);
        }
        return result.toString();
    }
}
