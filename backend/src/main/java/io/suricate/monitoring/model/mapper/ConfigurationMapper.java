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

package io.suricate.monitoring.model.mapper;


import io.suricate.monitoring.model.dto.ConfigurationDto;
import io.suricate.monitoring.model.entity.Configuration;
import io.suricate.monitoring.model.mapper.widget.CategoryMapper;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Interface that manage the generation DTO/Model objects for configuration class
 */
@Component
@Mapper(
    componentModel = "spring",
    uses = {
        CategoryMapper.class
    }
)
public abstract class ConfigurationMapper {

    /* ******************************************************* */
    /*                  Simple Mapping                         */
    /* ******************************************************* */

    /**
     * Tranform a configuration into a configurationDto
     *
     * @param configuration The configuration to transform
     * @return The related configuration DTO
     */
    @Named("toConfigurationDtoDefault")
    @Mappings({
        @Mapping(target = "category", qualifiedByName = "toCategoryDtoWithoutConfigurationsAndWithoutWidgets")
    })
    public abstract ConfigurationDto toConfigurationDtoDefault(Configuration configuration);

    /**
     * Get the configuration without category
     *
     * @param configuration The configuration
     * @return The related DTO
     */
    @Named("toConfigurationDtoWithoutCategory")
    @Mappings({
        @Mapping(target = "category", ignore = true)
    })
    public abstract ConfigurationDto toConfigurationDtoWithoutCategory(Configuration configuration);

    /* ******************************************************* */
    /*                    List Mapping                         */
    /* ******************************************************* */

    /**
     * Tranform a list of configurations into a list of configurationDto
     *
     * @param configurations The configurations to transform
     * @return The related list of configurations DTO
     */
    @Named("toConfigurationDtosDefault")
    @IterableMapping(qualifiedByName = "toConfigurationDtoDefault")
    public abstract List<ConfigurationDto> toConfigurationDtosDefault(List<Configuration> configurations);

    /**
     * Tranform a list of configurations into a list of configurationDto without category
     *
     * @param configurations The configurations to transform
     * @return The related list of configurations DTO
     */
    @Named("toConfigurationDtosWithoutCategory")
    @IterableMapping(qualifiedByName = "toConfigurationDtoWithoutCategory")
    public abstract List<ConfigurationDto> toConfigurationDtosWithoutCategory(List<Configuration> configurations);
}
