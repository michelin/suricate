package com.michelin.suricate.utils;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

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
    void shouldConvertStringWidgetPropertiesToMapWithoutDecoding() {
        Map<String, String> actual = PropertiesUtils.convertStringWidgetPropertiesToMap("key=test\nkey2=word%0Aword2%0Aword3\nkey3=test");
        assertThat(actual)
                .containsEntry("key", "test")
                .containsEntry("key2", "word%0Aword2%0Aword3")
                .containsEntry("key3", "test");
    }

    @Test
    void shouldConvertAndDecodeStringWidgetPropertiesToMap() {
        Map<String, String> actual = PropertiesUtils.convertAndDecodeStringWidgetPropertiesToMap("key=test\nkey2=word%0Aword2%0Aword3\nkey3=test");
        assertThat(actual)
                .containsEntry("key", "test")
                .containsEntry("key2", "word\nword2\nword3")
                .containsEntry("key3", "test");
    }

    @Test
    void shouldHandleExceptionWhenConvertAndDecodeStringWidgetPropertiesToMap() {
        try (MockedStatic<URLDecoder> mocked = mockStatic(URLDecoder.class)) {
            mocked.when(() -> URLDecoder.decode(any(), any()))
                    .thenThrow(new UnsupportedEncodingException("error"));

            Map<String, String> actual = PropertiesUtils.convertAndDecodeStringWidgetPropertiesToMap("key=test\nkey2=word%0Aword2%0Aword3\nkey3=test");
            assertThat(actual)
                    .containsEntry("key", "test")
                    .containsEntry("key2", "word%0Aword2%0Aword3")
                    .containsEntry("key3", "test");
        }
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

    @Test
    void shouldConvertStringWidgetPropertiesToPropertiesWithoutDecoding() {
        Properties actual = PropertiesUtils.convertStringWidgetPropertiesToProperties("key=test\nkey2=word%0Aword2%0Aword3\nkey3=test");
        assertThat(actual)
                .containsEntry("key", "test")
                .containsEntry("key2", "word%0Aword2%0Aword3")
                .containsEntry("key3", "test");
    }
}
