package com.michelin.suricate.configuration.swagger;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Api pageable annotation.
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Parameter(name = "page", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "0"),
    description = "Zero-based page index (0..N)")
@Parameter(name = "size", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "20"),
    description = "The size of the page to be returned")
@Parameter(name = "sort", in = ParameterIn.QUERY,
    array = @ArraySchema(schema = @Schema(type = "string")),
    description = "Sorting criteria in the format: property(,asc|desc)")
public @interface ApiPageable {
}
