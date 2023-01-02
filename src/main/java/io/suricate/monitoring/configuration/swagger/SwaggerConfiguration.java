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

package io.suricate.monitoring.configuration.swagger;

import io.suricate.monitoring.properties.ApplicationProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {
    @Bean
    public OpenAPI openAPI(ApplicationProperties applicationProperties) {
        return new OpenAPI()
                .info(new Info()
                        .title(applicationProperties.swagger.getTitle())
                        .description(applicationProperties.swagger.getDescription())
                        .version(applicationProperties.swagger.getVersion())
                        .contact(new Contact()
                                .name(applicationProperties.swagger.getContactName())
                                .email(applicationProperties.swagger.getContactEmail())
                                .url(applicationProperties.swagger.getContactUrl()))
                        .license(new License()
                                .name(applicationProperties.swagger.getLicense())
                                .url(applicationProperties.swagger.getLicenseUrl())))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .bearerFormat("JWT")
                                .scheme("bearer")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
