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

import com.google.common.base.Predicates;
import io.suricate.monitoring.configuration.ApplicationProperties;
import io.suricate.monitoring.configuration.security.ConnectedUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.*;

import static springfox.documentation.builders.PathSelectors.regex;

/**
 * The swagger configuration, to see the swagger page go to : "/swagger-ui.html"
 */
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    /**
     * The class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SwaggerConfiguration.class);

    /**
     * The application properties
     */
    private final ApplicationProperties applicationProperties;

    /**
     * The spring boot environment
     */
    private final Environment environment;

    /**
     * Constructor
     *
     * @param applicationProperties The application properties to inject
     */
    public SwaggerConfiguration(final ApplicationProperties applicationProperties,
                                final Environment environment) {
        this.applicationProperties = applicationProperties;
        this.environment = environment;
    }

    /**
     * The Springfow swagger configuration for the API Swagger docs
     *
     * @return The swagger springfox configuration
     */
    @Bean
    public Docket swaggerSpringfoxApiDocket() {
        LOGGER.debug("Starting Swagger");

        Contact contact = new Contact(
            applicationProperties.swagger.contactName,
            applicationProperties.swagger.contactUrl,
            applicationProperties.swagger.contactEmail
        );

        ApiInfo apiInfo = new ApiInfo(
            applicationProperties.swagger.title,
            applicationProperties.swagger.description,
            applicationProperties.swagger.version,
            applicationProperties.swagger.termsOfServiceUrl,
            contact,
            applicationProperties.swagger.license,
            applicationProperties.swagger.licenseUrl,
            new ArrayList<>()
        );

        return new Docket(DocumentationType.SWAGGER_2)
            .host(
                String.format(
                    "%s:%s",
                    InetAddress.getLoopbackAddress().getHostName(),
                    environment.getProperty("server.port")
                )
            )
            .protocols(
                new HashSet<>(
                    Arrays.asList(
                        applicationProperties.swagger.protocols.trim().split("\\s*,\\s*")
                    )
                )
            )
            .apiInfo(apiInfo)
            .groupName(applicationProperties.swagger.groupName)
            .securitySchemes(Collections.singletonList(apiKey()))
            .securityContexts(Collections.singletonList(securityContext()))
            .ignoredParameterTypes(ConnectedUser.class, ResponseEntity.class, Pageable.class)
            .forCodeGeneration(true)
            .useDefaultResponseMessages(false)
            .directModelSubstitute(ByteBuffer.class, String.class)
            .genericModelSubstitutes(ResponseEntity.class)
            .select()
            .paths(regex(applicationProperties.swagger.defaultIncludePattern))
            .build();
    }

    /**
     * Method used to define the token type
     *
     * @return the Token type
     */
    private static ApiKey apiKey() {
        return new ApiKey("Bearer", "Authorization", "header");
    }

    /**
     * Swagger context used to defined witch endpoint is secured
     *
     * @return the security context
     */
    private static SecurityContext securityContext() {
        return SecurityContext
            .builder()
            .securityReferences(defaultAuth())
            .forPaths(
                Predicates.and(
                    Predicates.not(regex("/api/oauth/token")),
                    Predicates.not(regex("/api/users/register")),
                    Predicates.not(regex("/api/projects/project/.*")),
                    Predicates.not(regex("/api/asset/.*")),
                    Predicates.not(regex("/api/configurations/application")),
                    regex("/api/.*")
                )
            )
            .build();
    }

    /**
     * Define the default authorization scope for the API
     *
     * @return the current authorization scope
     */
    private static List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Collections.singletonList(new SecurityReference("Bearer", authorizationScopes));
    }


}
