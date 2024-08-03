package com.michelin.suricate.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;
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

        assertThat(actual.getId()).isEqualTo(1L);
        assertThat(actual.getUserId()).isEqualTo(1L);
        assertThat(actual.getUnconstrainedValue()).isEqualTo("value");
        assertThat(actual.getSetting()).isEqualTo(settingResponseDto);
        assertThat(actual.getSettingValue()).isEqualTo(allowedSettingValueResponseDto);
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

        assertThat(actual.get(0).getId()).isEqualTo(1L);
        assertThat(actual.get(0).getUserId()).isEqualTo(1L);
        assertThat(actual.get(0).getUnconstrainedValue()).isEqualTo("value");
        assertThat(actual.get(0).getSetting()).isEqualTo(settingResponseDto);
        assertThat(actual.get(0).getSettingValue()).isEqualTo(allowedSettingValueResponseDto);
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
