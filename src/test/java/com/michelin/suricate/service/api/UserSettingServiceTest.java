package com.michelin.suricate.service.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.dto.api.user.UserSettingRequestDto;
import com.michelin.suricate.model.entity.AllowedSettingValue;
import com.michelin.suricate.model.entity.Setting;
import com.michelin.suricate.model.entity.User;
import com.michelin.suricate.model.entity.UserSetting;
import com.michelin.suricate.repository.UserSettingRepository;
import com.michelin.suricate.util.exception.ObjectNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserSettingServiceTest {
    @Mock
    private UserSettingRepository userSettingRepository;

    @Mock
    private SettingService settingService;

    @Mock
    private AllowedSettingValueService allowedSettingValueService;

    @InjectMocks
    private UserSettingService userSettingService;

    @Test
    void shouldCreateDefaultSettingsForUser() {
        UserSetting userSetting = new UserSetting();
        userSetting.setId(1L);

        AllowedSettingValue allowedSettingValue = new AllowedSettingValue();
        allowedSettingValue.setId(1L);
        allowedSettingValue.setDefault(true);

        Setting setting = new Setting();
        setting.setId(1L);
        setting.setAllowedSettingValues(Collections.singleton(allowedSettingValue));
        List<Setting> settings = Collections.singletonList(setting);
        allowedSettingValue.setSetting(setting);

        User user = new User();
        user.setId(1L);

        when(settingService.getAll())
            .thenReturn(Optional.of(settings));
        when(userSettingRepository.saveAll(any()))
            .thenReturn(Collections.singletonList(userSetting));

        List<UserSetting> actual = userSettingService.createDefaultSettingsForUser(user);

        assertThat(actual)
            .isNotEmpty();
        assertThat(actual.get(0).getUser())
            .isEqualTo(user);
        assertThat(actual.get(0).getSetting())
            .isEqualTo(setting);
        assertThat(actual.get(0).getSettingValue())
            .isEqualTo(allowedSettingValue);

        verify(settingService)
            .getAll();
        verify(userSettingRepository)
            .saveAll(argThat(userSettings -> {
                UserSetting createdUserSetting = userSettings.iterator().next();
                return createdUserSetting.getUser().equals(user)
                    && createdUserSetting.getSetting().equals(setting)
                    && createdUserSetting.getSettingValue().equals(allowedSettingValue);
            }));
        verify(userSettingRepository)
            .flush();
    }

    @Test
    void shouldCreateUserSettingFromAllowedSettingValue() {
        Setting setting = new Setting();
        setting.setId(1L);

        AllowedSettingValue allowedSettingValue = new AllowedSettingValue();
        allowedSettingValue.setId(1L);
        allowedSettingValue.setSetting(setting);

        User user = new User();
        user.setId(1L);

        UserSetting actual = userSettingService.createUserSettingFromAllowedSettingValue(allowedSettingValue, user);

        assertThat(actual.getUser())
            .isEqualTo(user);
        assertThat(actual.getSetting())
            .isEqualTo(setting);
        assertThat(actual.getSettingValue())
            .isEqualTo(allowedSettingValue);
    }

    @Test
    void shouldGetUserSetting() {
        UserSetting userSetting = new UserSetting();
        userSetting.setId(1L);

        when(userSettingRepository.findByUserUsernameAndSettingId(any(), any()))
            .thenReturn(Optional.of(userSetting));

        Optional<UserSetting> actual = userSettingService.getUserSetting("username", 1L);

        assertThat(actual)
            .isPresent()
            .contains(userSetting);

        verify(userSettingRepository)
            .findByUserUsernameAndSettingId("username", 1L);
    }

    @Test
    void shouldGetUserSettingsByUsername() {
        UserSetting userSetting = new UserSetting();
        userSetting.setId(1L);
        List<UserSetting> userSettings = Collections.singletonList(userSetting);

        when(userSettingRepository.findAllByUserUsernameIgnoreCase(any()))
            .thenReturn(Optional.of(userSettings));

        Optional<List<UserSetting>> actual = userSettingService.getUserSettingsByUsername("username");

        assertThat(actual).isPresent();
        assertThat(actual.get())
            .isNotEmpty()
            .contains(userSetting);

        verify(userSettingRepository)
            .findAllByUserUsernameIgnoreCase("username");
    }

    @Test
    void shouldUpdateUserConstrainedSetting() {
        AllowedSettingValue allowedSettingValue = new AllowedSettingValue();
        allowedSettingValue.setId(1L);

        Setting setting = new Setting();
        setting.setId(1L);
        setting.setConstrained(true);

        UserSetting userSetting = new UserSetting();
        userSetting.setId(1L);
        userSetting.setSetting(setting);

        UserSettingRequestDto userSettingRequestDto = new UserSettingRequestDto();
        userSettingRequestDto.setAllowedSettingValueId(1L);
        userSettingRequestDto.setUnconstrainedValue("value");

        when(userSettingRepository.findByUserUsernameAndSettingId(any(), any()))
            .thenReturn(Optional.of(userSetting));
        when(allowedSettingValueService.findById(any()))
            .thenReturn(Optional.of(allowedSettingValue));
        when(userSettingRepository.save(any()))
            .thenAnswer(answer -> answer.getArgument(0));

        userSettingService.updateUserSetting("username", 1L, userSettingRequestDto);

        assertThat(userSetting.getSettingValue())
            .isEqualTo(allowedSettingValue);

        verify(userSettingRepository)
            .findByUserUsernameAndSettingId("username", 1L);
        verify(allowedSettingValueService)
            .findById(1L);
        verify(userSettingRepository)
            .save(userSetting);
    }

    @Test
    void shouldUpdateUserUnconstrainedSetting() {
        AllowedSettingValue allowedSettingValue = new AllowedSettingValue();
        allowedSettingValue.setId(1L);

        Setting setting = new Setting();
        setting.setId(1L);
        setting.setConstrained(false);

        UserSetting userSetting = new UserSetting();
        userSetting.setId(1L);
        userSetting.setSetting(setting);

        UserSettingRequestDto userSettingRequestDto = new UserSettingRequestDto();
        userSettingRequestDto.setAllowedSettingValueId(1L);
        userSettingRequestDto.setUnconstrainedValue("value");

        when(userSettingRepository.findByUserUsernameAndSettingId(any(), any()))
            .thenReturn(Optional.of(userSetting));
        when(userSettingRepository.save(any()))
            .thenAnswer(answer -> answer.getArgument(0));

        userSettingService.updateUserSetting("username", 1L, userSettingRequestDto);

        assertThat(userSetting.getUnconstrainedValue())
            .isEqualTo("value");

        verify(userSettingRepository)
            .findByUserUsernameAndSettingId("username", 1L);
        verify(userSettingRepository)
            .save(userSetting);
    }

    @Test
    void shouldThrowExceptionWhenUserSettingNotFound() {
        UserSettingRequestDto userSettingRequestDto = new UserSettingRequestDto();
        userSettingRequestDto.setAllowedSettingValueId(1L);
        userSettingRequestDto.setUnconstrainedValue("value");

        when(userSettingRepository.findByUserUsernameAndSettingId(any(), any()))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userSettingService.updateUserSetting("username", 1L, userSettingRequestDto))
            .isInstanceOf(ObjectNotFoundException.class)
            .hasMessage("UserSetting 'user: username, settingId: 1' not found");

        verify(userSettingRepository)
            .findByUserUsernameAndSettingId("username", 1L);
    }
}
