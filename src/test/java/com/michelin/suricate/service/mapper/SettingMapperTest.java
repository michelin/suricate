package com.michelin.suricate.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.dto.api.setting.AllowedSettingValueResponseDto;
import com.michelin.suricate.model.dto.api.setting.SettingResponseDto;
import com.michelin.suricate.model.entity.AllowedSettingValue;
import com.michelin.suricate.model.entity.Setting;
import com.michelin.suricate.model.enumeration.DataTypeEnum;
import com.michelin.suricate.model.enumeration.SettingType;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SettingMapperTest {
    @Mock
    private AllowedSettingValueMapperImpl allowedSettingValueMapper;

    @InjectMocks
    private SettingMapperImpl settingMapper;

    @Test
    void shouldToSettingDto() {
        AllowedSettingValueResponseDto allowedSettingValueResponseDto = new AllowedSettingValueResponseDto();
        allowedSettingValueResponseDto.setId(1L);
        allowedSettingValueResponseDto.setTitle("title");
        allowedSettingValueResponseDto.setValue("value");
        allowedSettingValueResponseDto.setDefault(true);

        when(allowedSettingValueMapper.toAllowedSettingValuesDtos(any()))
            .thenReturn(Collections.singletonList(allowedSettingValueResponseDto));

        Setting setting = getSetting();
        SettingResponseDto actual = settingMapper.toSettingDto(setting);

        assertThat(actual.getId()).isEqualTo(1L);
        assertThat(actual.getDescription()).isEqualTo("description");
        assertThat(actual.isConstrained()).isTrue();
        assertThat(actual.getDataType()).isEqualTo(DataTypeEnum.TEXT);
        assertThat(actual.getType()).isEqualTo(SettingType.LANGUAGE);
        assertThat(actual.getAllowedSettingValues().get(0)).isEqualTo(allowedSettingValueResponseDto);
    }

    @Test
    void shouldToSettingsDtos() {
        AllowedSettingValueResponseDto allowedSettingValueResponseDto = new AllowedSettingValueResponseDto();
        allowedSettingValueResponseDto.setId(1L);
        allowedSettingValueResponseDto.setTitle("title");
        allowedSettingValueResponseDto.setValue("value");
        allowedSettingValueResponseDto.setDefault(true);

        when(allowedSettingValueMapper.toAllowedSettingValuesDtos(any()))
            .thenReturn(Collections.singletonList(allowedSettingValueResponseDto));

        Setting setting = getSetting();
        List<SettingResponseDto> actual = settingMapper.toSettingsDtos(Collections.singletonList(setting));

        assertThat(actual.get(0).getId()).isEqualTo(1L);
        assertThat(actual.get(0).getDescription()).isEqualTo("description");
        assertThat(actual.get(0).isConstrained()).isTrue();
        assertThat(actual.get(0).getDataType()).isEqualTo(DataTypeEnum.TEXT);
        assertThat(actual.get(0).getType()).isEqualTo(SettingType.LANGUAGE);
        assertThat(actual.get(0).getAllowedSettingValues().get(0)).isEqualTo(allowedSettingValueResponseDto);
    }

    @NotNull
    private static Setting getSetting() {
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
        return setting;
    }
}
