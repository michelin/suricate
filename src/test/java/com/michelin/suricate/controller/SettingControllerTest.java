/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.michelin.suricate.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        when(settingService.getAll()).thenReturn(Optional.empty());

        NoContentException exception = assertThrows(NoContentException.class, () -> settingController.getAll());

        assertEquals("No resource for the class 'Setting'", exception.getMessage());
    }

    @Test
    void shouldGetAll() {
        SettingResponseDto settingResponseDto = new SettingResponseDto();
        settingResponseDto.setId(1L);

        Setting setting = new Setting();
        setting.setId(1L);

        when(settingService.getAll()).thenReturn(Optional.of(Collections.singletonList(setting)));
        when(settingMapper.toSettingsDtos(any())).thenReturn(Collections.singletonList(settingResponseDto));

        ResponseEntity<List<SettingResponseDto>> actual = settingController.getAll();

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertNotNull(actual.getBody());
        assertTrue(actual.getBody().contains(settingResponseDto));
    }
}
