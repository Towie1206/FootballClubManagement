package com.eaut.footballclubmanagement.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SecurityUtilsTest {

    @Test
    public void hashSHA256_returnsKnownDigestForAsciiInput() {
        assertEquals(
                "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",
                SecurityUtils.hashSHA256("abc")
        );
    }

    @Test
    public void hashSHA256_returnsKnownDigestForEmptyInput() {
        assertEquals(
                "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
                SecurityUtils.hashSHA256("")
        );
    }

    @Test
    public void hashSHA256_isDeterministicAndKeepsLeadingZeroes() {
        String first = SecurityUtils.hashSHA256("a");
        String second = SecurityUtils.hashSHA256("a");

        assertEquals("ca978112ca1bbdcafac231b39a23dc4da786eff8147c4e72b9807785afee48bb", first);
        assertEquals(first, second);
        assertEquals(64, first.length());
    }

    @Test
    public void hashSHA256_returnsNullForNullInput() {
        assertNull(SecurityUtils.hashSHA256(null));
    }
}
