package com.michelin.suricate.service.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.michelin.suricate.model.dto.api.setting.AllowedSettingValueResponseDto;
import com.michelin.suricate.model.entity.AllowedSettingValue;
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

        assertEquals(1L, actual.getId());
        assertTrue(actual.isDefault());
        assertEquals("title", actual.getTitle());
        assertEquals("value", actual.getValue());
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

        assertEquals(1L, actual.getFirst().getId());
        assertTrue(actual.getFirst().isDefault());
        assertEquals("title", actual.getFirst().getTitle());
        assertEquals("value", actual.getFirst().getValue());
    }
}
