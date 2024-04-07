package com.michelin.suricate.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.dto.api.setting.SettingResponseDto;
import com.michelin.suricate.model.entity.Setting;
import com.michelin.suricate.service.api.SettingService;
import com.michelin.suricate.service.mapper.SettingMapper;
import com.michelin.suricate.util.exception.NoContentException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class SettingControllerTest {
    @Mock
    private SettingService settingService;

    @Mock
    private SettingMapper settingMapper;

    @InjectMocks
    private SettingController settingController;

    @Test
    void shouldGetAllNotFound() {
        when(settingService.getAll())
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> settingController.getAll())
            .isInstanceOf(NoContentException.class)
            .hasMessage("No resource for the class 'Setting'");
    }

    @Test
    void shouldGetAll() {
        SettingResponseDto settingResponseDto = new SettingResponseDto();
        settingResponseDto.setId(1L);

        Setting setting = new Setting();
        setting.setId(1L);

        when(settingService.getAll())
            .thenReturn(Optional.of(Collections.singletonList(setting)));
        when(settingMapper.toSettingsDtos(any()))
            .thenReturn(Collections.singletonList(settingResponseDto));

        ResponseEntity<List<SettingResponseDto>> actual = settingController.getAll();

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).contains(settingResponseDto);
    }
}
