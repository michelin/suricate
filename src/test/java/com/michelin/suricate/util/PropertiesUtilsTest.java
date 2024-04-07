package com.michelin.suricate.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

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
    void shouldConvertStringWidgetPropertiesToMapWithoutEscaping() {
        Map<String, String> actual =
            PropertiesUtils.convertStringWidgetPropertiesToMap("key=test\nkey2=test2\\ntest\nkey3=test3");
        assertThat(actual)
            .containsEntry("key", "test")
            .containsEntry("key2", "test2\ntest")
            .containsEntry("key3", "test3");
    }

    @Test
    void shouldConvertAndEscapeStringWidgetPropertiesToMapNull() {
        Map<String, String> actual = PropertiesUtils.convertAndEscapeStringWidgetPropertiesToMap(null);
        assertThat(actual).isEmpty();
    }

    @Test
    void shouldConvertAndEscapeStringWidgetPropertiesToMapEmpty() {
        Map<String, String> actual = PropertiesUtils.convertAndEscapeStringWidgetPropertiesToMap(StringUtils.EMPTY);
        assertThat(actual).isEmpty();
    }

    @Test
    void shouldConvertAndEscapeStringWidgetPropertiesToMap() {
        Map<String, String> actual =
            PropertiesUtils.convertAndEscapeStringWidgetPropertiesToMap("key=test\nkey2=test2");
        assertThat(actual)
            .containsEntry("key", "test")
            .containsEntry("key2", "test2");
    }

    @Test
    void shouldConvertAndPreserveEscapeStringWidgetPropertiesToMap() {
        Map<String, String> actual =
            PropertiesUtils.convertAndEscapeStringWidgetPropertiesToMap("key=test\nkey2=test2\\ntest\nkey3=test3");
        assertThat(actual)
            .containsEntry("key", "test")
            .containsEntry("key2", "test2\\ntest")
            .containsEntry("key3", "test3");
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
