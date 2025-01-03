package com.michelin.suricate.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

class JsonUtilsTest {
    @Test
    void shouldBeInvalidNull() {
        boolean actual = JsonUtils.isValid(null);
        assertFalse(actual);
    }

    @Test
    void shouldBeInvalidEmpty() {
        boolean actual = JsonUtils.isValid(StringUtils.EMPTY);
        assertFalse(actual);
    }

    @Test
    void shouldBeInvalidFormat() {
        boolean actual = JsonUtils.isValid("{\"test\":0");
        assertFalse(actual);
    }

    @Test
    void shouldBeValid() {
        boolean actual = JsonUtils.isValid("{\"test\":0}");
        assertTrue(actual);
    }
}
