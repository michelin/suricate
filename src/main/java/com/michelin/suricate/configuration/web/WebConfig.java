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
import com.michelin.suricate.security.oauth2.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private ApplicationProperties applicationProperties;

    /**
     * CORS configuration
     * @param registry The CORS registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/api/**")
                .combine(applicationProperties.getCors());
    }

    /**
     * The view resolver
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
                protected Resource getResource(String resourcePath, Resource location) throws IOException {
                    Resource requestedResource = location.createRelative(resourcePath);
                    return requestedResource.exists() && requestedResource.isReadable() ? requestedResource : new ClassPathResource("/public/index.html");
                }
            });
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationFailureEntryPoint authenticationFailureEntryPoint,
                                           HttpCookieOAuth2AuthorizationRequestRepository authRequestRepository, OAuth2UserService userService,
                                           OIDCUserService oidcUserService, @Autowired(required = false) OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler,
                                           OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler) throws Exception {
        http
                .cors()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationFailureEntryPoint)
                .accessDeniedHandler(authenticationFailureEntryPoint)
                .and()
                .headers()
                .frameOptions().disable()
                .and()
                .anonymous()
                .and()
                .authorizeRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/api/*/auth/signin").permitAll()
                .antMatchers("/api/*/users/signup").permitAll()
                .antMatchers("/api/*/configurations/authentication-providers").permitAll()
                .antMatchers("/api/*/projects/{projectToken}").permitAll()
                .antMatchers("/api/*/projectWidgets/{projectToken}/projectWidgets").permitAll()
                .antMatchers("/api/*/projectWidgets/{projectWidgetId}").permitAll()
                .antMatchers("/api/*/widgets/{widgetId}").permitAll()
                .antMatchers("/api/*/settings").permitAll()
                .antMatchers("/api/*/assets/**").permitAll()
                .antMatchers("/ws/**").permitAll()
                .antMatchers("/api/oauth2/authorization/**").permitAll()
                .antMatchers("/api/**").authenticated()
                .and()
                .addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(personalAccessTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        if (oAuth2AuthenticationSuccessHandler.getOAuth2AuthorizedClientRepository() != null) {
            http.oauth2Login()
                    .authorizationEndpoint()
                    // Store auth request in a http cookie on the IDP response
                    .authorizationRequestRepository(authRequestRepository)
                    // Override default "oauth2/authorization/" endpoint by adding "/api"
                    // Endpoint that triggers the OAuth2 auth to given IDP
                    .baseUri("/api/oauth2/authorization")
                    .and()
                    .userInfoEndpoint()
                    .userService(userService)
                    .userService(userService)
                    .oidcUserService(oidcUserService)
                    .and()
                    .successHandler(oAuth2AuthenticationSuccessHandler)
                    .failureHandler(oAuth2AuthenticationFailureHandler);
        }

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception {
        return authConfiguration.getAuthenticationManager();
    }

    /**
     * HTTP filter processing the given jwt token in header
     * @return The token authentication filter bean
     */
    @Bean
    public JwtTokenFilter jwtTokenFilter() {
        return new JwtTokenFilter();
    }

    /**
     * HTTP filter processing the given personal access token in header
     * @return The token authentication filter bean
     */
    @Bean
    public PersonalAccessTokenFilter personalAccessTokenFilter() {
        return new PersonalAccessTokenFilter();
    }

    /**
     * Define a role hierarchy
     * @return The role hierarchy bean
     */
    @Bean
    protected RoleHierarchyImpl roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
        return roleHierarchy;
    }
}
