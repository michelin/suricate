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

package io.suricate.monitoring.services.mapper;


import io.suricate.monitoring.model.dto.api.widgetconfiguration.WidgetConfigurationResponseDto;
import io.suricate.monitoring.model.entities.WidgetConfiguration;
import org.jasypt.encryption.StringEncryptor;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
public abstract class WidgetConfigurationMapper {

    @Autowired
    @Qualifier("jasyptStringEncryptor")
    StringEncryptor stringEncryptor;

    /* ************************* TO DTO ********************************************** */

    /* ******************************************************* */
    /*                  Simple Mapping                         */
    /* ******************************************************* */

    /**
     * Tranform a configuration into a configurationDto
     *
     * @param widgetConfiguration The configuration to transform
     * @return The related configuration DTO
     */
    @Named("toConfigurationDtoDefault")
    @Mapping(target = "category", qualifiedByName = "toCategoryDtoDefault")
    @Mapping(target = "value", expression = "java(" +
        "widgetConfiguration.getDataType() == io.suricate.monitoring.model.enums.DataType.PASSWORD ? stringEncryptor.decrypt(widgetConfiguration.getValue()) : widgetConfiguration.getValue())" +
        "")
    public abstract WidgetConfigurationResponseDto toConfigurationDtoDefault(WidgetConfiguration widgetConfiguration);

    /* ******************************************************* */
    /*                    List Mapping                         */
    /* ******************************************************* */

    /**
     * Tranform a list of configurations into a list of configurationDto
     *
     * @param widgetConfigurations The configurations to transform
     * @return The related list of configurations DTO
     */
    @Named("toConfigurationDtosDefault")
    @IterableMapping(qualifiedByName = "toConfigurationDtoDefault")
    public abstract List<WidgetConfigurationResponseDto> toConfigurationDtosDefault(List<WidgetConfiguration> widgetConfigurations);
}
