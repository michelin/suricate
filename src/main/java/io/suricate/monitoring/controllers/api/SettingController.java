/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.suricate.monitoring.controllers.api;

import io.suricate.monitoring.model.dto.api.error.ApiErrorDto;
import io.suricate.monitoring.model.dto.api.setting.SettingResponseDto;
import io.suricate.monitoring.model.entity.setting.Setting;
import io.suricate.monitoring.model.enums.SettingType;
import io.suricate.monitoring.service.api.SettingService;
import io.suricate.monitoring.service.mapper.SettingMapper;
import io.suricate.monitoring.utils.exception.NoContentException;
import io.suricate.monitoring.utils.exception.ObjectNotFoundException;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Settings controller
 */
@RestController
@RequestMapping("/api")
@Api(value = "Setting Controller", tags = {"Settings"})
public class SettingController {

    /**
     * The setting service
     */
    private final SettingService settingService;

    /**
     * The setting mapper used for the transformation of Model object into a DTO object
     */
    private final SettingMapper settingMapper;

    /**
     * The constructor
     *
     * @param settingService The setting service to inject
     * @param settingMapper  The setting mapper
     */
    @Autowired
    public SettingController(final SettingService settingService,
                             final SettingMapper settingMapper) {
        this.settingService = settingService;
        this.settingMapper = settingMapper;
    }

    /**
     * Get the full list of settings
     *
     * @return The full list of settings
     */
    @ApiOperation(value = "Get the full list of settings", response = SettingResponseDto.class, nickname = "getAllSettings")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = SettingResponseDto.class, responseContainer = "List"),
        @ApiResponse(code = 204, message = "No Content")
    })
    @GetMapping(value = "/v1/settings")
    public ResponseEntity<List<SettingResponseDto>> getAll(@ApiParam(name = "type", value = "The setting type to get", allowableValues = "template, language")
                                                           @RequestParam(value = "type", required = false) String type) {
        Optional<List<Setting>> settingsOptional = Optional.empty();

        if (type != null) {
            Optional<Setting> settingByType = settingService.getOneByType(SettingType.getSettingTypeByString(type));
            if (settingByType.isPresent()) {
                settingsOptional = Optional.of(Collections.singletonList(settingByType.get()));
            }
        } else {
            settingsOptional = settingService.getAll();
        }

        if (!settingsOptional.isPresent()) {
            throw new NoContentException(Setting.class);
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(settingMapper.toSettingDtosDefault(settingsOptional.get()));
    }

    /**
     * Get a setting
     *
     * @param settingId The setting id to get
     * @return The setting
     */
    @ApiOperation(value = "Get a setting by id", response = SettingResponseDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = SettingResponseDto.class),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class)
    })
    @GetMapping(value = "/v1/settings/{settingId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SettingResponseDto> getOne(@ApiParam(name = "settingId", value = "The setting id", required = true)
                                                     @PathVariable("settingId") Long settingId) {
        Optional<Setting> settingOptional = settingService.getOneById(settingId);
        if (!settingOptional.isPresent()) {
            throw new ObjectNotFoundException(Setting.class, settingId);
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(settingMapper.toSettingDtoDefault(settingOptional.get()));
    }
}
