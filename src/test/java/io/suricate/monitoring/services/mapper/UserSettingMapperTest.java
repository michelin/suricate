package io.suricate.monitoring.services.mapper;

import io.suricate.monitoring.model.dto.api.setting.AllowedSettingValueResponseDto;
import io.suricate.monitoring.model.dto.api.setting.SettingResponseDto;
import io.suricate.monitoring.model.dto.api.user.UserSettingResponseDto;
import io.suricate.monitoring.model.entities.AllowedSettingValue;
import io.suricate.monitoring.model.entities.Setting;
import io.suricate.monitoring.model.entities.User;
import io.suricate.monitoring.model.entities.UserSetting;
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
class UserSettingMapperTest {
    @Mock
    protected AllowedSettingValueMapper allowedSettingValueMapper;

    @Mock
    protected SettingMapper settingMapper;

    @InjectMocks
    private UserSettingMapperImpl userSettingMapper;

    @Test
    void shouldToUserSettingDTO() {
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

        SettingResponseDto settingResponseDto = new SettingResponseDto();
        settingResponseDto.setId(1L);

        AllowedSettingValueResponseDto allowedSettingValueResponseDto = new AllowedSettingValueResponseDto();
        allowedSettingValueResponseDto.setId(1L);

        when(settingMapper.toSettingDTO(any()))
                .thenReturn(settingResponseDto);
        when(allowedSettingValueMapper.toAllowedSettingValueDTO(any()))
                .thenReturn(allowedSettingValueResponseDto);

        UserSettingResponseDto actual = userSettingMapper.toUserSettingDTO(userSetting);

        assertThat(actual.getId()).isEqualTo(1L);
        assertThat(actual.getUserId()).isEqualTo(1L);
        assertThat(actual.getUnconstrainedValue()).isEqualTo("value");
        assertThat(actual.getSetting()).isEqualTo(settingResponseDto);
        assertThat(actual.getSettingValue()).isEqualTo(allowedSettingValueResponseDto);
    }

    @Test
    void shouldToUserSettingsDTOs() {
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

        SettingResponseDto settingResponseDto = new SettingResponseDto();
        settingResponseDto.setId(1L);

        AllowedSettingValueResponseDto allowedSettingValueResponseDto = new AllowedSettingValueResponseDto();
        allowedSettingValueResponseDto.setId(1L);

        when(settingMapper.toSettingDTO(any()))
                .thenReturn(settingResponseDto);
        when(allowedSettingValueMapper.toAllowedSettingValueDTO(any()))
                .thenReturn(allowedSettingValueResponseDto);

        List<UserSettingResponseDto> actual = userSettingMapper.toUserSettingsDTOs(Collections.singletonList(userSetting));

        assertThat(actual.get(0).getId()).isEqualTo(1L);
        assertThat(actual.get(0).getUserId()).isEqualTo(1L);
        assertThat(actual.get(0).getUnconstrainedValue()).isEqualTo("value");
        assertThat(actual.get(0).getSetting()).isEqualTo(settingResponseDto);
        assertThat(actual.get(0).getSettingValue()).isEqualTo(allowedSettingValueResponseDto);
    }
}
