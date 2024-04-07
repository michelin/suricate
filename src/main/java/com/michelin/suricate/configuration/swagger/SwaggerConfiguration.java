/*
 * Copyright 2012-2021 the original author or authors.
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

package com.michelin.suricate.configuration.swagger;

import com.michelin.suricate.property.ApplicationProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger configuration.
 */
@Configuration
public class SwaggerConfiguration {
    private static final String BEARER_AUTH = "bearerAuth";

    /**
     * Create the OpenAPI object.
     *
     * @param applicationProperties The application properties
     * @return The OpenAPI object
     */
    @Bean
    public OpenAPI openApi(ApplicationProperties applicationProperties) {
        return new OpenAPI()
            .info(new Info()
                .title(applicationProperties.getSwagger().getTitle())
                .description(applicationProperties.getSwagger().getDescription())
                .version(applicationProperties.getSwagger().getVersion())
                .contact(new Contact()
                    .name(applicationProperties.getSwagger().getContactName())
                    .email(applicationProperties.getSwagger().getContactEmail())
                    .url(applicationProperties.getSwagger().getContactUrl()))
                .license(new License()
                    .name(applicationProperties.getSwagger().getLicense())
                    .url(applicationProperties.getSwagger().getLicenseUrl())))
            .components(new Components()
                .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                    .name(BEARER_AUTH)
                    .type(SecurityScheme.Type.HTTP)
                    .bearerFormat("JWT")
                    .scheme("bearer")))
            .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH));
    }
}
