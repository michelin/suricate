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

package io.suricate.monitoring.controllers;

import io.suricate.monitoring.configuration.swagger.ApiPageable;
import io.suricate.monitoring.model.dto.api.category.CategoryParameterResponseDto;
import io.suricate.monitoring.model.dto.api.error.ApiErrorDto;
import io.suricate.monitoring.model.dto.api.widgetconfiguration.WidgetConfigurationRequestDto;
import io.suricate.monitoring.model.entities.CategoryParameter;
import io.suricate.monitoring.services.api.CategoryParametersService;
import io.suricate.monitoring.services.cache.CacheService;
import io.suricate.monitoring.services.mapper.CategoryMapper;
import io.suricate.monitoring.utils.exceptions.ObjectNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
@Tag(name = "Category Parameters", description = "Category Parameters Controller")
public class CategoryParametersController {
    @Autowired
    private CategoryParametersService categoryParametersService;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CacheService cacheService;

    /**
     * Get all parameters of all categories
     * @return The list of parameters of all categories
     */
    @Operation(summary = "Get all parameters of all categories")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "204", description = "No Content"),
        @ApiResponse(responseCode = "401", description = "Authentication error, token expired or invalid", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "403", description = "You don't have permission to access to this resource", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })
    @ApiPageable
    @GetMapping(value = "/v1/category-parameters")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Page<CategoryParameterResponseDto> getAll(@Parameter(name = "search", description = "Search keyword")
                                                     @RequestParam(value = "search", required = false) String search,
                                                     @ParameterObject Pageable pageable) {
        return categoryParametersService.getAll(search, pageable).map(categoryMapper::toCategoryParameterDTO);
    }

    /**
     * Get a configuration by the key
     * @param key The key to find
     * @return The related configuration
     */
    @Operation(summary = "Get a configuration by the key")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "401", description = "Authentication error, token expired or invalid", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "403", description = "You don't have permission to access to this resource", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "404", description = "Configuration not found", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })
    @GetMapping(value = "/v1/category-parameters/{key}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CategoryParameterResponseDto> getOneByKey(@Parameter(name = "key", description = "The configuration key", required = true)
                                                                    @PathVariable("key") final String key) {
        Optional<CategoryParameter> configurationOptional = categoryParametersService.getOneByKey(key);

        if (!configurationOptional.isPresent()) {
            throw new ObjectNotFoundException(CategoryParameter.class, key);
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(categoryMapper.toCategoryParameterDTO(configurationOptional.get()));
    }

    /**
     * Update the configuration by the key
     * @param key                           The key of the config
     * @param widgetConfigurationRequestDto The new configuration values
     * @return The config updated
     */
    @Operation(summary = "Update a configuration by the key")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Configuration updated"),
        @ApiResponse(responseCode = "401", description = "Authentication error, token expired or invalid", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "403", description = "You don't have permission to access to this resource", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "404", description = "Configuration not found", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })
    @PutMapping(value = "/v1/category-parameters/{key}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> updateOneByKey(@Parameter(name = "key", description = "The configuration key", required = true)
                                               @PathVariable("key") final String key,
                                               @Parameter(name = "configurationResponseDto", description = "The configuration updated", required = true)
                                               @RequestBody final WidgetConfigurationRequestDto widgetConfigurationRequestDto) {
        Optional<CategoryParameter> configurationOptional = categoryParametersService.getOneByKey(key);

        if (!configurationOptional.isPresent()) {
            throw new ObjectNotFoundException(CategoryParameter.class, key);
        }

        categoryParametersService.updateConfiguration(configurationOptional.get(), widgetConfigurationRequestDto.getValue());
        cacheService.clearCache("configuration");

        return ResponseEntity.noContent().build();
    }

    /**
     * Delete a configuration by key
     * @param key The configuration key
     * @return The config deleted
     */
    @Operation(summary = "Delete a configuration by the key")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Configuration deleted"),
        @ApiResponse(responseCode = "401", description = "Authentication error, token expired or invalid", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "403", description = "You don't have permission to access to this resource", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "404", description = "Configuration not found", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })
    @DeleteMapping(value = "/v1/category-parameters/{key}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteOneByKey(@Parameter(name = "key", description = "The configuration key", required = true)
                                               @PathVariable("key") final String key) {
        Optional<CategoryParameter> configurationOptional = categoryParametersService.getOneByKey(key);

        if (!configurationOptional.isPresent()) {
            throw new ObjectNotFoundException(CategoryParameter.class, key);
        }

        categoryParametersService.deleteOneByKey(key);

        return ResponseEntity.noContent().build();
    }
}
