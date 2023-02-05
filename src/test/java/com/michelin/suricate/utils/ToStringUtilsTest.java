package com.michelin.suricate.utils;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

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
        String actual = ToStringUtils.hideWidgetConfigurationInLogs("Should hide my password", Collections.singletonList("password"));
        assertThat(actual).isEqualTo("Should hide my ********");
    }
}
