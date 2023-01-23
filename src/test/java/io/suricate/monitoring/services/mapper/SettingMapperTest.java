package io.suricate.monitoring.services.mapper;

import io.suricate.monitoring.model.dto.api.setting.AllowedSettingValueResponseDto;
import io.suricate.monitoring.model.dto.api.setting.SettingResponseDto;
import io.suricate.monitoring.model.entities.AllowedSettingValue;
import io.suricate.monitoring.model.entities.Setting;
import io.suricate.monitoring.model.enums.DataTypeEnum;
import io.suricate.monitoring.model.enums.SettingType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SettingMapperTest {
    @Mock
    private AllowedSettingValueMapperImpl allowedSettingValueMapper;

    @InjectMocks
    private SettingMapperImpl settingMapper;

    @Test
    void shouldToSettingDTO() {
        AllowedSettingValue allowedSettingValue = new AllowedSettingValue();
        allowedSettingValue.setId(1L);
        allowedSettingValue.setTitle("title");
        allowedSettingValue.setValue("value");
        allowedSettingValue.setDefault(true);

        Setting setting = new Setting();
        setting.setId(1L);
        setting.setDescription("description");
        setting.setConstrained(true);
        setting.setDataType(DataTypeEnum.TEXT);
        setting.setType(SettingType.LANGUAGE);
        setting.setAllowedSettingValues(Collections.singleton(allowedSettingValue));

        AllowedSettingValueResponseDto allowedSettingValueResponseDto = new AllowedSettingValueResponseDto();
        allowedSettingValueResponseDto.setId(1L);
        allowedSettingValueResponseDto.setTitle("title");
        allowedSettingValueResponseDto.setValue("value");
        allowedSettingValueResponseDto.setDefault(true);

        when(allowedSettingValueMapper.toAllowedSettingValuesDTOs(any()))
                .thenReturn(Collections.singletonList(allowedSettingValueResponseDto));

        SettingResponseDto actual = settingMapper.toSettingDTO(setting);

        assertThat(actual.getId()).isEqualTo(1L);
        assertThat(actual.getDescription()).isEqualTo("description");
        assertThat(actual.isConstrained()).isTrue();
        assertThat(actual.getDataType()).isEqualTo(DataTypeEnum.TEXT);
        assertThat(actual.getType()).isEqualTo(SettingType.LANGUAGE);
        assertThat(actual.getAllowedSettingValues().get(0)).isEqualTo(allowedSettingValueResponseDto);
    }

    @Test
    void shouldToSettingsDTOs() {
        AllowedSettingValue allowedSettingValue = new AllowedSettingValue();
        allowedSettingValue.setId(1L);
        allowedSettingValue.setTitle("title");
        allowedSettingValue.setValue("value");
        allowedSettingValue.setDefault(true);

        Setting setting = new Setting();
        setting.setId(1L);
        setting.setDescription("description");
        setting.setConstrained(true);
        setting.setDataType(DataTypeEnum.TEXT);
        setting.setType(SettingType.LANGUAGE);
        setting.setAllowedSettingValues(Collections.singleton(allowedSettingValue));

        AllowedSettingValueResponseDto allowedSettingValueResponseDto = new AllowedSettingValueResponseDto();
        allowedSettingValueResponseDto.setId(1L);
        allowedSettingValueResponseDto.setTitle("title");
        allowedSettingValueResponseDto.setValue("value");
        allowedSettingValueResponseDto.setDefault(true);

        when(allowedSettingValueMapper.toAllowedSettingValuesDTOs(any()))
                .thenReturn(Collections.singletonList(allowedSettingValueResponseDto));

        List<SettingResponseDto> actual = settingMapper.toSettingsDTOs(Collections.singletonList(setting));

        assertThat(actual.get(0).getId()).isEqualTo(1L);
        assertThat(actual.get(0).getDescription()).isEqualTo("description");
        assertThat(actual.get(0).isConstrained()).isTrue();
        assertThat(actual.get(0).getDataType()).isEqualTo(DataTypeEnum.TEXT);
        assertThat(actual.get(0).getType()).isEqualTo(SettingType.LANGUAGE);
        assertThat(actual.get(0).getAllowedSettingValues().get(0)).isEqualTo(allowedSettingValueResponseDto);
    }
}
