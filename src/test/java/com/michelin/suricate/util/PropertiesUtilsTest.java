package com.michelin.suricate.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

class PropertiesUtilsTest {
    @Test
    void shouldConvertStringWidgetPropertiesToMapNull() {
        Map<String, String> actual = PropertiesUtils.convertStringWidgetPropertiesToMap(null);
        assertTrue(actual.isEmpty());
    }

    @Test
    void shouldConvertStringWidgetPropertiesToMapEmpty() {
        Map<String, String> actual = PropertiesUtils.convertStringWidgetPropertiesToMap(StringUtils.EMPTY);
        assertTrue(actual.isEmpty());
    }

    @Test
    void shouldConvertStringWidgetPropertiesToMap() {
        Map<String, String> actual = PropertiesUtils.convertStringWidgetPropertiesToMap("key=test\nkey2=test2");
        assertEquals("test", actual.get("key"));
        assertEquals("test2", actual.get("key2"));
    }

    @Test
    void shouldConvertStringWidgetPropertiesToMapWithoutEscaping() {
        Map<String, String> actual = PropertiesUtils
            .convertStringWidgetPropertiesToMap("key=test\nkey2=test2\\ntest\nkey3=test3");

        assertEquals("test", actual.get("key"));
        assertEquals("test2\ntest", actual.get("key2"));
        assertEquals("test3", actual.get("key3"));
    }

    @Test
    void shouldConvertAndEscapeStringWidgetPropertiesToMapNull() {
        Map<String, String> actual = PropertiesUtils.convertAndEscapeStringWidgetPropertiesToMap(null);
        assertTrue(actual.isEmpty());
    }

    @Test
    void shouldConvertAndEscapeStringWidgetPropertiesToMapEmpty() {
        Map<String, String> actual = PropertiesUtils.convertAndEscapeStringWidgetPropertiesToMap(StringUtils.EMPTY);
        assertTrue(actual.isEmpty());
    }

    @Test
    void shouldConvertAndEscapeStringWidgetPropertiesToMap() {
        Map<String, String> actual =
            PropertiesUtils.convertAndEscapeStringWidgetPropertiesToMap("key=test\nkey2=test2");

        assertEquals("test", actual.get("key"));
        assertEquals("test2", actual.get("key2"));
    }

    @Test
    void shouldConvertAndPreserveEscapeStringWidgetPropertiesToMap() {
        Map<String, String> actual =
            PropertiesUtils.convertAndEscapeStringWidgetPropertiesToMap("key=test\nkey2=test2\\ntest\nkey3=test3");

        assertEquals("test", actual.get("key"));
        assertEquals("test2\\ntest", actual.get("key2"));
        assertEquals("test3", actual.get("key3"));
    }

    @Test
    void shouldConvertStringWidgetPropertiesToPropertiesNull() {
        Properties actual = PropertiesUtils.convertStringWidgetPropertiesToProperties(null);
        assertNull(actual);
    }

    @Test
    void shouldConvertStringWidgetPropertiesToPropertiesEmpty() {
        Properties actual = PropertiesUtils.convertStringWidgetPropertiesToProperties(StringUtils.EMPTY);
        assertNull(actual);
    }

    @Test
    void shouldConvertStringWidgetPropertiesToProperties() {
        Properties actual = PropertiesUtils.convertStringWidgetPropertiesToProperties("key=test\nkey2=test2");

        assertEquals("test", actual.get("key"));
        assertEquals("test2", actual.get("key2"));
    }
}
