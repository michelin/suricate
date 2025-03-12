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
package com.michelin.suricate.service.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.entity.Setting;
import com.michelin.suricate.repository.SettingRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

        when(settingRepository.findById(any())).thenReturn(Optional.of(setting));

        Optional<Setting> actual = settingService.getOneById(1L);

        assertTrue(actual.isPresent());
        assertEquals(setting, actual.get());

        verify(settingRepository).findById(1L);
    }

    @Test
    void shouldGetAll() {
        Setting setting = new Setting();
        setting.setId(1L);
        List<Setting> settings = Collections.singletonList(setting);

        when(settingRepository.findAllByOrderByDescription()).thenReturn(Optional.of(settings));

        Optional<List<Setting>> actual = settingService.getAll();

        assertTrue(actual.isPresent());
        assertEquals(setting, actual.get().getFirst());

        verify(settingRepository).findAllByOrderByDescription();
    }
}
