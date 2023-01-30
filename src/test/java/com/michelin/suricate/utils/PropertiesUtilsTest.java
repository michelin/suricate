package com.michelin.suricate.utils;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class PropertiesUtilsTest {
    @Test
    void shouldConvertStringWidgetPropertiesToMapNull() {
        Map<String, String> actual = PropertiesUtils.convertStringWidgetPropertiesToMap(null);
        assertThat(actual).isEmpty();
    }

    @Test
    void shouldConvertStringWidgetPropertiesToMapEmpty() {
        Map<String, String> actual = PropertiesUtils.convertStringWidgetPropertiesToMap(StringUtils.EMPTY);
        assertThat(actual).isEmpty();
    }

    @Test
    void shouldConvertStringWidgetPropertiesToMap() {
        Map<String, String> actual = PropertiesUtils.convertStringWidgetPropertiesToMap("key=test\nkey2=test2");
        assertThat(actual)
                .containsEntry("key", "test")
                .containsEntry("key2", "test2");
    }

    @Test
    void shouldConvertStringWidgetPropertiesToPropertiesNull() {
        Properties actual = PropertiesUtils.convertStringWidgetPropertiesToProperties(null);
        assertThat(actual).isNull();
    }

    @Test
    void shouldConvertStringWidgetPropertiesToPropertiesEmpty() {
        Properties actual = PropertiesUtils.convertStringWidgetPropertiesToProperties(StringUtils.EMPTY);
        assertThat(actual).isNull();
    }

    @Test
    void shouldConvertStringWidgetPropertiesToProperties() {
        Properties actual = PropertiesUtils.convertStringWidgetPropertiesToProperties("key=test\nkey2=test2");
        assertThat(actual)
                .containsEntry("key", "test")
                .containsEntry("key2", "test2");
    }
}
