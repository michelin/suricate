package com.michelin.suricate.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

class ToStringUtilsTest {
    @Test
    void shouldHideWidgetConfigurationInLogsNull() {
        String actual = ToStringUtils.hideWidgetConfigurationInLogs(null, null);
        assertThat(actual).isNull();
    }

    @Test
    void shouldHideWidgetConfigurationInLogsEmptyLogs() {
        String actual = ToStringUtils.hideWidgetConfigurationInLogs(StringUtils.EMPTY, null);
        assertThat(actual).isNull();
    }

    @Test
    void shouldHideWidgetConfigurationInLogsNoConfig() {
        String actual = ToStringUtils.hideWidgetConfigurationInLogs("test", null);
        assertThat(actual).isEqualTo("test");
    }

    @Test
    void shouldHideWidgetConfigurationInLogsEmptyConfig() {
        String actual = ToStringUtils.hideWidgetConfigurationInLogs(null, Collections.emptyList());
        assertThat(actual).isNull();
    }

    @Test
    void shouldHideWidgetConfigurationInLogs() {
        String actual = ToStringUtils.hideWidgetConfigurationInLogs("Should hide my password",
            Collections.singletonList("password"));
        assertThat(actual).isEqualTo("Should hide my ********");
    }

    @Test
    void shouldAvoidNullValue() {
        String actual =
            ToStringUtils.hideWidgetConfigurationInLogs("Should hide my password", Arrays.asList("password", null));
        assertThat(actual).isEqualTo("Should hide my ********");
    }
}
