/*
 *
 *  * Copyright 2012-2021 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.michelin.suricate.controllers;

import com.michelin.suricate.model.dto.api.setting.SettingResponseDto;
import com.michelin.suricate.model.entities.Setting;
import com.michelin.suricate.services.api.SettingService;
import com.michelin.suricate.services.mapper.SettingMapper;
import com.michelin.suricate.utils.exceptions.NoContentException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@Tag(name = "Setting", description = "Setting Controller")
public class SettingController {
    @Autowired
    private SettingService settingService;

    @Autowired
    private SettingMapper settingMapper;

    /**
     * Get the full list of settings
     * @return The full list of settings
     */
    @Operation(summary = "Get the full list of settings")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "204", description = "No Content")
    })
    @GetMapping(value = "/v1/settings")
    public ResponseEntity<List<SettingResponseDto>> getAll() {
        Optional<List<Setting>> settingsOptional = settingService.getAll();

        if (!settingsOptional.isPresent()) {
            throw new NoContentException(Setting.class);
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(settingMapper.toSettingsDTOs(settingsOptional.get()));
    }
}
