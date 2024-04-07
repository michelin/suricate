package com.michelin.suricate.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

class JsonUtilsTest {
    @Test
    void shouldBeInvalidNull() {
        boolean actual = JsonUtils.isValid(null);
        assertThat(actual).isFalse();
    }

    @Test
    void shouldBeInvalidEmpty() {
        boolean actual = JsonUtils.isValid(StringUtils.EMPTY);
        assertThat(actual).isFalse();
    }

    @Test
    void shouldBeInvalidFormat() {
        boolean actual = JsonUtils.isValid("{\"test\":0");
        assertThat(actual).isFalse();
    }

    @Test
    void shouldBeValid() {
        boolean actual = JsonUtils.isValid("{\"test\":0}");
        assertThat(actual).isTrue();
    }
}
