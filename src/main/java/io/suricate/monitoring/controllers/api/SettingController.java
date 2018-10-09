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

import io.suricate.monitoring.model.dto.error.ApiErrorDto;
import io.suricate.monitoring.model.dto.setting.SettingDto;
import io.suricate.monitoring.model.entity.setting.Setting;
import io.suricate.monitoring.model.mapper.setting.SettingMapper;
import io.suricate.monitoring.service.api.SettingService;
import io.suricate.monitoring.utils.exception.NoContentException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * Settings controller
 */
@RestController
@RequestMapping("/api/settings")
@Api(value = "Setting Controller", tags = {"Setting"})
public class SettingController {

    /**
     * Class logger
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(SettingController.class);

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
    @ApiOperation(value = "Get the full list of settings", response = SettingDto.class, nickname = "getAllSettings")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = SettingDto.class, responseContainer = "List"),
        @ApiResponse(code = 204, message = "No Content"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class)
    })
    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<SettingDto>> getAll() {
        Optional<List<Setting>> settingsOptional = settingService.getAll();

        if (!settingsOptional.isPresent()) {
            throw new NoContentException(Setting.class);
        }

        return ResponseEntity
            .ok()
            .cacheControl(CacheControl.noCache())
            .contentType(MediaType.APPLICATION_JSON)
            .body(settingMapper.toSettingDtosDefault(settingsOptional.get()));
    }
}
