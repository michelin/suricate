package com.michelin.suricate.service.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.dto.api.setting.AllowedSettingValueResponseDto;
import com.michelin.suricate.model.dto.api.setting.SettingResponseDto;
import com.michelin.suricate.model.dto.api.user.UserSettingResponseDto;
import com.michelin.suricate.model.entity.AllowedSettingValue;
import com.michelin.suricate.model.entity.Setting;
import com.michelin.suricate.model.entity.User;
import com.michelin.suricate.model.entity.UserSetting;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserSettingMapperTest {
    @Mock
    protected AllowedSettingValueMapper allowedSettingValueMapper;

    @Mock
    protected SettingMapper settingMapper;

    @InjectMocks
    private UserSettingMapperImpl userSettingMapper;

    @Test
    void shouldToUserSettingDto() {
        SettingResponseDto settingResponseDto = new SettingResponseDto();
        settingResponseDto.setId(1L);

        AllowedSettingValueResponseDto allowedSettingValueResponseDto = new AllowedSettingValueResponseDto();
        allowedSettingValueResponseDto.setId(1L);

        when(settingMapper.toSettingDto(any()))
            .thenReturn(settingResponseDto);
        when(allowedSettingValueMapper.toAllowedSettingValueDto(any()))
            .thenReturn(allowedSettingValueResponseDto);

        UserSettingResponseDto actual = userSettingMapper.toUserSettingDto(getUserSetting());

        assertEquals(1L, actual.getId());
        assertEquals(1L, actual.getUserId());
        assertEquals("value", actual.getUnconstrainedValue());
        assertEquals(settingResponseDto, actual.getSetting());
        assertEquals(allowedSettingValueResponseDto, actual.getSettingValue());
    }

    @Test
    void shouldToUserSettingsDtos() {
        SettingResponseDto settingResponseDto = new SettingResponseDto();
        settingResponseDto.setId(1L);

        AllowedSettingValueResponseDto allowedSettingValueResponseDto = new AllowedSettingValueResponseDto();
        allowedSettingValueResponseDto.setId(1L);

        when(settingMapper.toSettingDto(any()))
            .thenReturn(settingResponseDto);
        when(allowedSettingValueMapper.toAllowedSettingValueDto(any()))
            .thenReturn(allowedSettingValueResponseDto);

        List<UserSettingResponseDto> actual =
            userSettingMapper.toUserSettingsDtos(Collections.singletonList(getUserSetting()));

        assertEquals(1L, actual.getFirst().getId());
        assertEquals(1L, actual.getFirst().getUserId());
        assertEquals("value", actual.getFirst().getUnconstrainedValue());
        assertEquals(settingResponseDto, actual.getFirst().getSetting());
        assertEquals(allowedSettingValueResponseDto, actual.getFirst().getSettingValue());
    }

    @NotNull
    private static UserSetting getUserSetting() {
        AllowedSettingValue allowedSettingValue = new AllowedSettingValue();
        allowedSettingValue.setId(1L);

        Setting setting = new Setting();
        setting.setId(1L);

        User user = new User();
        user.setId(1L);

        UserSetting userSetting = new UserSetting();
        userSetting.setId(1L);
        userSetting.setUnconstrainedValue("value");
        userSetting.setSetting(setting);
        userSetting.setSettingValue(allowedSettingValue);
        userSetting.setUser(user);
        return userSetting;
    }
}
