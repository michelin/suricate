package io.suricate.monitoring.controllers;

import io.suricate.monitoring.model.dto.api.error.ApiErrorDto;
import io.suricate.monitoring.model.dto.api.export.*;
import io.suricate.monitoring.model.entities.*;
import io.suricate.monitoring.services.api.*;
import io.suricate.monitoring.services.mapper.CategoryMapper;
import io.suricate.monitoring.services.mapper.LibraryMapper;
import io.suricate.monitoring.services.mapper.ProjectMapper;
import io.suricate.monitoring.services.mapper.RepositoryMapper;
import io.suricate.monitoring.utils.exceptions.ObjectNotFoundException;
import io.suricate.monitoring.utils.exceptions.OperationNotPermittedException;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Export controller
 */
@RestController
@RequestMapping("/api")
@Api(value = "Export Controller", tags = {"Exports"})
public class ImportExportController {
    /**
     * The import/export service
     */
    private final ImportExportService importExportService;

    /**
     * The repository service
     */
    private final RepositoryService repositoryService;

    /**
     * Constructor
     * @param importExportService The import/export service
     * @param repositoryService The repository service
     */
    public ImportExportController(ImportExportService importExportService,
                                  RepositoryService repositoryService) {
        this.importExportService = importExportService;
        this.repositoryService = repositoryService;
    }

    /**
     * Export the application data
     * @return The application data to export
     */
    @ApiOperation(value = "Export the application data", response = ImportExportDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = String.class),
            @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
            @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class)
    })
    @GetMapping(value = "/v1/export")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ImportExportDto> exports() {
        ImportExportDto exportDto = importExportService.getDataToExport();

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(exportDto);
    }

    /**
     * Import the application data
     * @param importDto The application data to import
     */
    @ApiOperation(value = "Import the application data", response = ImportExportDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = String.class),
            @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
            @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class)
    })
    @PostMapping(value = "/v1/import")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> imports(@ApiParam(name = "importDto", value = "The data to import", required = true)
                                             @RequestBody ImportExportDto importDto) {
        if (repositoryService.count() > 0) {
            throw new OperationNotPermittedException("This Suricate instance already has data");
        }

        importExportService.importData(importDto);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .build();
    }
}
