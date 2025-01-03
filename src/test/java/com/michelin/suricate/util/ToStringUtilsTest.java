package com.michelin.suricate.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;
import java.util.Collections;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

class ToStringUtilsTest {
    @Test
    void shouldHideWidgetConfigurationInLogsNull() {
        String actual = ToStringUtils.hideWidgetConfigurationInLogs(null, null);
        assertNull(actual);
    }

    @Test
    void shouldHideWidgetConfigurationInLogsEmptyLogs() {
        String actual = ToStringUtils.hideWidgetConfigurationInLogs(StringUtils.EMPTY, null);
        assertNull(actual);
    }

    @Test
    void shouldHideWidgetConfigurationInLogsNoConfig() {
        String actual = ToStringUtils.hideWidgetConfigurationInLogs("test", null);
        assertEquals("test", actual);
    }

    @Test
    void shouldHideWidgetConfigurationInLogsEmptyConfig() {
        String actual = ToStringUtils.hideWidgetConfigurationInLogs(null, Collections.emptyList());
        assertNull(actual);
    }

    @Test
    void shouldHideWidgetConfigurationInLogs() {
        String actual = ToStringUtils
            .hideWidgetConfigurationInLogs("Should hide my password", Collections.singletonList("password"));
        assertEquals("Should hide my ********", actual);
    }

    @Test
    void shouldAvoidNullValue() {
        String actual = ToStringUtils
            .hideWidgetConfigurationInLogs("Should hide my password", Arrays.asList("password", null));
        assertEquals("Should hide my ********", actual);
    }
}
