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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.entity.AllowedSettingValue;
import com.michelin.suricate.repository.AllowedSettingValueRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AllowedSettingValueServiceTest {
    @Mock
    private AllowedSettingValueRepository allowedSettingValueRepository;

    @InjectMocks
    private AllowedSettingValueService allowedSettingValueService;

    @Test
    void shouldFindById() {
        AllowedSettingValue allowedSettingValue = new AllowedSettingValue();
        allowedSettingValue.setId(1L);
        allowedSettingValue.setTitle("title");
        allowedSettingValue.setValue("value");

        when(allowedSettingValueRepository.findById(1L)).thenReturn(Optional.of(allowedSettingValue));

        Optional<AllowedSettingValue> actual = allowedSettingValueService.findById(1L);

        assertTrue(actual.isPresent());
        assertEquals(allowedSettingValue, actual.get());

        verify(allowedSettingValueRepository).findById(1L);
    }
}
