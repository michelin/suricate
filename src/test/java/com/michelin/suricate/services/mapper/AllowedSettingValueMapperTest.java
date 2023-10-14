package com.michelin.suricate.services.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.michelin.suricate.model.dto.api.setting.AllowedSettingValueResponseDto;
import com.michelin.suricate.model.entities.AllowedSettingValue;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AllowedSettingValueMapperTest {
    @InjectMocks
    private AllowedSettingValueMapperImpl allowedSettingValueMapper;

    @Test
    void shouldToAllowedSettingValueDto() {
        AllowedSettingValue allowedSettingValue = new AllowedSettingValue();
        allowedSettingValue.setId(1L);
        allowedSettingValue.setDefault(true);
        allowedSettingValue.setTitle("title");
        allowedSettingValue.setValue("value");

        AllowedSettingValueResponseDto actual = allowedSettingValueMapper.toAllowedSettingValueDto(allowedSettingValue);

        assertThat(actual.getId()).isEqualTo(1L);
        assertThat(actual.isDefault()).isTrue();
        assertThat(actual.getTitle()).isEqualTo("title");
        assertThat(actual.getValue()).isEqualTo("value");
    }

    @Test
    void shouldToAllowedSettingValuesDtos() {
        AllowedSettingValue allowedSettingValue = new AllowedSettingValue();
        allowedSettingValue.setId(1L);
        allowedSettingValue.setDefault(true);
        allowedSettingValue.setTitle("title");
        allowedSettingValue.setValue("value");

        List<AllowedSettingValueResponseDto> actual =
            allowedSettingValueMapper.toAllowedSettingValuesDtos(Collections.singletonList(allowedSettingValue));

        assertThat(actual.get(0).getId()).isEqualTo(1L);
        assertThat(actual.get(0).isDefault()).isTrue();
        assertThat(actual.get(0).getTitle()).isEqualTo("title");
        assertThat(actual.get(0).getValue()).isEqualTo("value");
    }
}
