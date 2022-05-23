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

package io.suricate.monitoring.configuration.security.web;

import io.suricate.monitoring.configuration.security.oauth2.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.OpaqueTokenAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

import static io.suricate.monitoring.configuration.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

/**
 * Global Security configurations
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    /**
     * The authentication failure entrypoint
     */
    @Autowired
    private AuthenticationFailureEntryPoint authenticationFailureEntryPoint;

    /**
     * The authentication request repository
     * Store the authentication request in an HTTP cookie on the IDP response
     */
    @Autowired
    private HttpCookieOAuth2AuthorizationRequestRepository authRequestRepository;

    /**
     * The OAuth2 user loader service
     */
    @Autowired
    private OAuth2UserService userService;

    /**
     * The authentication success handler
     */
    @Autowired
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @Autowired
    private OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    /**
     * Configure the web security of the application
     */
    @Override
    public void configure(WebSecurity webSecurity) {
        webSecurity
            .ignoring()
            .antMatchers(HttpMethod.OPTIONS);
    }

    /**
     * Resource Security
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
            .cors()
                .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
            .csrf().disable()
            .exceptionHandling()
            //.authenticationEntryPoint(authenticationFailureEntryPoint)
            //.accessDeniedHandler(authenticationFailureEntryPoint)
                .and()
            .headers()
            .frameOptions().disable()
                .and()
            .anonymous()
                .and()
            .authorizeRequests()
            .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
            .antMatchers("/h2-console/**").permitAll()
            .antMatchers("/api/*/users/register").permitAll()
            .antMatchers("/api/*/configurations/authentication-provider").permitAll()
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
            .oauth2Login()
            .authorizationEndpoint()
            // Store auth request in a http cookie on the IDP response
            .authorizationRequestRepository(authRequestRepository)
            // Override default "oauth2/authorization/" endpoint by adding "/api"
            // Endpoint that triggers the OAuth2 auth to given IDP
            .baseUri("/api/oauth2/authorization")
                .and()
            .userInfoEndpoint()
            .userService(userService)
                .and()
            .successHandler(oAuth2AuthenticationSuccessHandler)
            .failureHandler(oAuth2AuthenticationFailureHandler);
    }

    /**
     * Configure the role hierarchy
     */
    @Bean
    protected RoleHierarchyImpl roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
        return roleHierarchy;
    }

    /**
     * Authentication Manager
     * @return Default authentication manager
     * @throws Exception Any triggered exception during the authentication process
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
