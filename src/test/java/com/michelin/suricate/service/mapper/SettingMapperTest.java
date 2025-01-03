package com.michelin.suricate.service.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

        assertEquals(1L, actual.getId());
        assertEquals("description", actual.getDescription());
        assertTrue(actual.isConstrained());
        assertEquals(DataTypeEnum.TEXT, actual.getDataType());
        assertEquals(SettingType.LANGUAGE, actual.getType());
        assertEquals(allowedSettingValueResponseDto, actual.getAllowedSettingValues().getFirst());
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

        assertEquals(1L, actual.getFirst().getId());
        assertEquals("description", actual.getFirst().getDescription());
        assertTrue(actual.getFirst().isConstrained());
        assertEquals(DataTypeEnum.TEXT, actual.getFirst().getDataType());
        assertEquals(SettingType.LANGUAGE, actual.getFirst().getType());
        assertEquals(allowedSettingValueResponseDto, actual.getFirst().getAllowedSettingValues().getFirst());
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
