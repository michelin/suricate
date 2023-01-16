package io.suricate.monitoring.services.mapper;

import io.suricate.monitoring.model.dto.api.setting.AllowedSettingValueResponseDto;
import io.suricate.monitoring.model.entities.AllowedSettingValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AllowedSettingValueMapperTest {
    @InjectMocks
    private AllowedSettingValueMapperImpl allowedSettingValueMapper;

    @Test
    void shouldToAllowedSettingValueDTO() {
        AllowedSettingValue allowedSettingValue = new AllowedSettingValue();
        allowedSettingValue.setId(1L);
        allowedSettingValue.setDefault(true);
        allowedSettingValue.setTitle("title");
        allowedSettingValue.setValue("value");

        AllowedSettingValueResponseDto actual = allowedSettingValueMapper.toAllowedSettingValueDTO(allowedSettingValue);

        assertThat(actual.getId()).isEqualTo(1L);
        assertThat(actual.isDefault()).isTrue();
        assertThat(actual.getTitle()).isEqualTo("title");
        assertThat(actual.getValue()).isEqualTo("value");
    }

    @Test
    void shouldToAllowedSettingValuesDTOs() {
        AllowedSettingValue allowedSettingValue = new AllowedSettingValue();
        allowedSettingValue.setId(1L);
        allowedSettingValue.setDefault(true);
        allowedSettingValue.setTitle("title");
        allowedSettingValue.setValue("value");

        List<AllowedSettingValueResponseDto> actual = allowedSettingValueMapper.toAllowedSettingValuesDTOs(Collections.singletonList(allowedSettingValue));

        assertThat(actual.get(0).getId()).isEqualTo(1L);
        assertThat(actual.get(0).isDefault()).isTrue();
        assertThat(actual.get(0).getTitle()).isEqualTo("title");
        assertThat(actual.get(0).getValue()).isEqualTo("value");
    }
}
