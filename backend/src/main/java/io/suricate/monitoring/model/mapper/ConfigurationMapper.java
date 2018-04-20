package io.suricate.monitoring.model.mapper;


import io.suricate.monitoring.model.dto.ConfigurationDto;
import io.suricate.monitoring.model.entity.Configuration;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Interface that manage the generation DTO/Model objects for configuration class
 */
@Component
@Mapper(
    componentModel = "spring"
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
    public abstract ConfigurationDto toConfigurationDtoDefault(Configuration configuration);

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
}
