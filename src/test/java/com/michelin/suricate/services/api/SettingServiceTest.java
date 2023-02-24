package com.michelin.suricate.services.api;

import com.michelin.suricate.model.entities.Setting;
import com.michelin.suricate.repositories.SettingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SettingServiceTest {
    @Mock
    private SettingRepository settingRepository;

    @InjectMocks
    private SettingService settingService;

    @Test
    void shouldGetOneById() {
        Setting setting = new Setting();
        setting.setId(1L);

        when(settingRepository.findById(any()))
                .thenReturn(Optional.of(setting));

        Optional<Setting> actual = settingService.getOneById(1L);

        assertThat(actual)
                .isPresent()
                .contains(setting);

        verify(settingRepository)
                .findById(1L);
    }

    @Test
    void shouldGetAll() {
        Setting setting = new Setting();
        setting.setId(1L);
        List<Setting> settings = Collections.singletonList(setting);

        when(settingRepository.findAllByOrderByDescription())
                .thenReturn(Optional.of(settings));

        Optional<List<Setting>> actual = settingService.getAll();

        assertThat(actual).isPresent();
        assertThat(actual.get())
                .isNotEmpty()
                .contains(setting);

        verify(settingRepository)
                .findAllByOrderByDescription();
    }
}
