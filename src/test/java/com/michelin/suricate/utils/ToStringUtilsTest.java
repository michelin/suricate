package com.michelin.suricate.utils;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ToStringUtilsTest {
    public static class SimpleEntity {
        Long test = 0L;
        String test2 = "ok";
        List<DataEntity> list = new ArrayList<>();
        DataEntity data = new DataEntity();
        DataEntity[] tabData = new DataEntity[10];

        @Entity
        public static class DataEntity {
            @Id
            Long id = 52L;
        }
    }

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

    @Test
    void shouldToStringEntityNull() {
        assertThat(ToStringUtils.toStringEntity(null)).isNull();
    }

    @Test
    void shouldToStringEntity() {
        String actual = ToStringUtils.toStringEntity(new SimpleEntity());
        assertThat(actual).contains("[test=0,test2=ok]");
    }
}
