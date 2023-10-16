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

package com.michelin.suricate.configuration.web;

import com.michelin.suricate.properties.ApplicationProperties;
import com.michelin.suricate.security.AuthenticationFailureEntryPoint;
import com.michelin.suricate.security.filter.JwtTokenFilter;
import com.michelin.suricate.security.filter.PersonalAccessTokenFilter;
import com.michelin.suricate.security.oauth2.HttpCookieOauth2AuthorizationRequestRepository;
import com.michelin.suricate.security.oauth2.Oauth2AuthenticationFailureHandler;
import com.michelin.suricate.security.oauth2.Oauth2AuthenticationSuccessHandler;
import com.michelin.suricate.security.oauth2.Oauth2UserService;
import com.michelin.suricate.security.oauth2.OidcUserService;
import java.io.IOException;
import java.util.Locale;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.resource.PathResourceResolver;

/**
 * Web configuration.
 */
@Configuration
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class WebConfig implements WebMvcConfigurer {
    /**
     * The view resolver.
     *
     * @param registry Store the configurations
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/favicon.ico")
            .addResourceLocations("classpath:/");

        registry.addResourceHandler("swagger-ui.html")
            .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/**")
            .addResourceLocations("classpath:/public/")
            .resourceChain(true)
            .addResolver(new PathResourceResolver() {
                @Override
                protected Resource getResource(@NotNull String resourcePath, @NotNull Resource location)
                    throws IOException {
                    Resource requestedResource = location.createRelative(resourcePath);
                    return requestedResource.exists() && requestedResource.isReadable() ? requestedResource :
                        new ClassPathResource("/public/index.html");
                }
            });
    }

    /**
     * Define the security filter chain.
     *
     * @param http                               The http security
     * @param authenticationFailureEntryPoint    The authentication failure entry point
     * @param authRequestRepository              The auth request repository
     * @param userService                        The user service
     * @param oidcUserService                    The oidc user service
     * @param oauth2AuthenticationSuccessHandler The oauth2 authentication success handler
     * @param oauth2AuthenticationFailureHandler The oauth2 authentication failure handler
     * @return The security filter chain
     * @throws Exception When an error occurred
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           ApplicationProperties applicationProperties,
                                           AuthenticationFailureEntryPoint authenticationFailureEntryPoint,
                                           HttpCookieOauth2AuthorizationRequestRepository authRequestRepository,
                                           Oauth2UserService userService,
                                           OidcUserService oidcUserService, @Autowired(required = false)
                                           Oauth2AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler,
                                           Oauth2AuthenticationFailureHandler oauth2AuthenticationFailureHandler)
        throws Exception {
        http
            .cors(corsConfigurer -> corsConfigurer
                .configurationSource(new UrlBasedCorsConfigurationSource() {
                    {
                        registerCorsConfiguration("/api/**", applicationProperties.getCors());
                    }
                }))
            .sessionManagement(sessionManagementConfigurer -> sessionManagementConfigurer
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(AbstractHttpConfigurer::disable)
            .exceptionHandling(exceptionHandlingConfigurer -> exceptionHandlingConfigurer
                .authenticationEntryPoint(authenticationFailureEntryPoint)
                .accessDeniedHandler(authenticationFailureEntryPoint))
            .headers(headersConfigurer -> headersConfigurer
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
            .authorizeHttpRequests(authorizeRequestsConfigurer -> authorizeRequestsConfigurer
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .requestMatchers(HttpMethod.OPTIONS).permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/api/*/auth/signin").permitAll()
                .requestMatchers("/api/*/users/signup").permitAll()
                .requestMatchers("/api/*/configurations/authentication-providers").permitAll()
                .requestMatchers("/api/*/projects/{projectToken}").permitAll()
                .requestMatchers("/api/*/projectWidgets/{projectToken}/projectWidgets").permitAll()
                .requestMatchers("/api/*/projectWidgets/{projectWidgetId}").permitAll()
                .requestMatchers("/api/*/widgets/{widgetId}").permitAll()
                .requestMatchers("/api/*/settings").permitAll()
                .requestMatchers("/api/*/assets/**").permitAll()
                .requestMatchers("/ws/**").permitAll()
                .requestMatchers("/api/oauth2/authorization/**").permitAll()
                .requestMatchers("/api/**").authenticated())
            .addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(personalAccessTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        if (oauth2AuthenticationSuccessHandler.getAuthorizedClientRepository() != null) {
            http
                .oauth2Login(oauth2LoginConfigurer -> oauth2LoginConfigurer
                    .authorizationEndpoint(authorizationEndpointConfigurer -> authorizationEndpointConfigurer
                        // Store auth request in a http cookie on the IDP response
                        .authorizationRequestRepository(authRequestRepository)
                        // Override default "oauth2/authorization/" endpoint by adding "/api"
                        // Endpoint that triggers the OAuth2 auth to given IDP
                        .baseUri("/api/oauth2/authorization"))
                    .userInfoEndpoint(userInfoEndpointConfigurer -> userInfoEndpointConfigurer
                        .userService(userService)
                        .oidcUserService(oidcUserService))
                    .successHandler(oauth2AuthenticationSuccessHandler)
                    .failureHandler(oauth2AuthenticationFailureHandler));
        }

        return http.build();
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
    protected RoleHierarchyImpl roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
        return roleHierarchy;
    }

    /**
     * Define local language
     * Used in "javax.validation" messages
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
