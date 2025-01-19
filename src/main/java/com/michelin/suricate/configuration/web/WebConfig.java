/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.michelin.suricate.configuration.web;

import com.michelin.suricate.property.ApplicationProperties;
import com.michelin.suricate.security.AuthenticationFailureEntryPoint;
import com.michelin.suricate.security.filter.JwtTokenFilter;
import com.michelin.suricate.security.filter.PersonalAccessTokenFilter;
import com.michelin.suricate.security.oauth2.HttpCookieOauth2AuthorizationRequestRepository;
import com.michelin.suricate.security.oauth2.Oauth2AuthenticationFailureHandler;
import com.michelin.suricate.security.oauth2.Oauth2AuthenticationSuccessHandler;
import com.michelin.suricate.security.oauth2.Oauth2UserService;
import com.michelin.suricate.security.oauth2.OpenIdcUserService;
import java.io.IOException;
import java.util.Locale;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.resource.PathResourceResolver;

/**
 * Web configuration.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private ApplicationProperties applicationProperties;

    @Value("${spring.h2.console.enabled:false}")
    private boolean h2Enabled;

    /**
     * The view resolver.
     * Serve the Angular static resources and redirect them all to the index.html.
     *
     * @param registry Store the configurations
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
            .addResourceLocations("classpath:/public/")
            .resourceChain(true)
            .addResolver(new PathResourceResolver() {
                @Override
                protected Resource getResource(@NotNull String resourcePath, @NotNull Resource location)
                    throws IOException {
                    Resource requestedResource = location.createRelative(resourcePath);
                    return requestedResource.exists() && requestedResource.isReadable()
                        ? requestedResource : new ClassPathResource("/public/index.html");
                }
            });
    }

    /**
     * Define the security filter chain.
     *
     * @param http                    The http security
     * @param authFailureEntryPoint   The authentication failure entry point
     * @param oauth2RequestRepository The auth request repository
     * @param userService             The user service
     * @param openIdcUserService      The oidc user service
     * @param oauth2SuccessHandler    The oauth2 authentication success handler
     * @param oauth2FailureHandler    The oauth2 authentication failure handler
     * @return The security filter chain
     * @throws Exception When an error occurred
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           AuthenticationFailureEntryPoint authFailureEntryPoint,
                                           HttpCookieOauth2AuthorizationRequestRepository oauth2RequestRepository,
                                           Oauth2UserService userService, OpenIdcUserService openIdcUserService,
                                           @Autowired(required = false)
                                           Oauth2AuthenticationSuccessHandler oauth2SuccessHandler,
                                           Oauth2AuthenticationFailureHandler oauth2FailureHandler,
                                           HandlerMappingIntrospector introspector) throws Exception {
        MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);

        http
            .cors(corsConfigurer -> corsConfigurer
                .configurationSource(corsConfiguration()))
            .sessionManagement(sessionManagementConfigurer -> sessionManagementConfigurer
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(AbstractHttpConfigurer::disable)
            .exceptionHandling(exceptionHandlingConfigurer -> exceptionHandlingConfigurer
                .authenticationEntryPoint(authFailureEntryPoint)
                .accessDeniedHandler(authFailureEntryPoint))
            .headers(headersConfigurer -> headersConfigurer
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
            .authorizeHttpRequests(authorizeRequestsConfigurer -> {
                authorizeRequestsConfigurer
                    .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                    // Make other MVC requests served by the DispatcherServlet
                    .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.OPTIONS, "/**")).permitAll()
                    // Actuator
                    .requestMatchers(mvcMatcherBuilder.pattern("/actuator/**")).permitAll()
                    // Swagger
                    .requestMatchers(mvcMatcherBuilder.pattern("/swagger-ui/**")).permitAll()
                    .requestMatchers(mvcMatcherBuilder.pattern("/swagger-ui.html")).permitAll()
                    .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/v3/api-docs/**")).permitAll()
                    // Suricate API
                    .requestMatchers(mvcMatcherBuilder.pattern("/api/*/auth/signin")).permitAll()
                    .requestMatchers(mvcMatcherBuilder.pattern("/api/*/users/signup")).permitAll()
                    .requestMatchers(mvcMatcherBuilder.pattern("/api/*/configurations/authentication-providers"))
                    .permitAll()
                    .requestMatchers(mvcMatcherBuilder.pattern("/api/*/projects/{projectToken}")).permitAll()
                    .requestMatchers(mvcMatcherBuilder
                        .pattern("/api/*/projectWidgets/{projectToken}/projectWidgets"))
                    .permitAll()
                    .requestMatchers(mvcMatcherBuilder
                        .pattern("/api/*/projectWidgets/{projectWidgetId}")).permitAll()
                    .requestMatchers(mvcMatcherBuilder.pattern("/api/*/widgets/{widgetId}")).permitAll()
                    .requestMatchers(mvcMatcherBuilder.pattern("/api/*/settings")).permitAll()
                    .requestMatchers(mvcMatcherBuilder.pattern("/api/*/assets/**")).permitAll()
                    .requestMatchers(mvcMatcherBuilder.pattern("/ws/**")).permitAll()
                    .requestMatchers(mvcMatcherBuilder.pattern("/api/oauth2/authorization/**")).permitAll()
                    .requestMatchers(mvcMatcherBuilder.pattern("/api/**")).authenticated()
                    // Front-End
                    .requestMatchers(mvcMatcherBuilder.pattern(HttpMethod.GET, "/**")).permitAll();

                if (h2Enabled) {
                    // Make H2 console served by the Jakarta servlet if enabled
                    authorizeRequestsConfigurer.requestMatchers(PathRequest.toH2Console()).permitAll();
                }
            })
            .addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(personalAccessTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        if (oauth2SuccessHandler.getAuthorizedClientRepository() != null) {
            http
                .oauth2Login(oauth2LoginConfigurer -> oauth2LoginConfigurer
                    .authorizationEndpoint(authorizationEndpointConfigurer -> authorizationEndpointConfigurer
                        // Store auth request in a http cookie on the IDP response
                        .authorizationRequestRepository(oauth2RequestRepository)
                        // Override default "oauth2/authorization/" endpoint by adding "/api"
                        // Endpoint that triggers the OAuth2 auth to given IDP
                        .baseUri("/api/oauth2/authorization"))
                    .userInfoEndpoint(userInfoEndpointConfigurer -> userInfoEndpointConfigurer
                        .userService(userService)
                        .oidcUserService(openIdcUserService))
                    .successHandler(oauth2SuccessHandler)
                    .failureHandler(oauth2FailureHandler));
        }

        return http.build();
    }

    /**
     * Define the CORS configuration.
     *
     * @return The CORS configuration
     */
    public UrlBasedCorsConfigurationSource corsConfiguration() {
        UrlBasedCorsConfigurationSource corsConfiguration = new UrlBasedCorsConfigurationSource();
        corsConfiguration.registerCorsConfiguration("/api/**", applicationProperties.getCors());
        return corsConfiguration;
    }

    /**
     * Define the authentication manager.
     * Used to inject it in the authentication controller.
     *
     * @param authConfiguration The auth configuration
     * @return The authentication manager
     * @throws Exception When an error occurred during the authentication manager creation
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception {
        return authConfiguration.getAuthenticationManager();
    }

    /**
     * HTTP filter processing the given jwt token in header.
     *
     * @return The token authentication filter bean
     */
    @Bean
    public JwtTokenFilter jwtTokenFilter() {
        return new JwtTokenFilter();
    }

    /**
     * HTTP filter processing the given personal access token in header.
     *
     * @return The token authentication filter bean
     */
    @Bean
    public PersonalAccessTokenFilter personalAccessTokenFilter() {
        return new PersonalAccessTokenFilter();
    }

    /**
     * Define a role hierarchy.
     *
     * @return The role hierarchy bean
     */
    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl
            .withDefaultRolePrefix()
            .role("ADMIN").implies("USER")
            .build();
    }

    /**
     * Define a method security expression handler.
     * Used to define the role hierarchy on the method security.
     *
     * @return The method security expression handler bean
     */
    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy());
        return expressionHandler;
    }

    /**
     * Define local language.
     * Used in "jakarta.validation" messages
     *
     * @return The local language
     */
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.ENGLISH);
        return slr;
    }
}
