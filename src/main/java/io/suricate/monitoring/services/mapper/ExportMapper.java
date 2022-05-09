package io.suricate.monitoring.services.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

/**
 * Manage the generation DTO/Model objects for export class
 */
@Component
@Mapper(componentModel = "spring",
        uses = {
                ProjectMapper.class
        })
public abstract class ExportMapper {
}
