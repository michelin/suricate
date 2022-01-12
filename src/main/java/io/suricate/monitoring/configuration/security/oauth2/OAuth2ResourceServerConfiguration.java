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

package io.suricate.monitoring.configuration.security.oauth2;

import io.suricate.monitoring.controllers.handlers.ApiAuthenticationFailureHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.web.cors.CorsUtils;

/**
 * The resource server configuration
 */
@Configuration
@EnableResourceServer
public class OAuth2ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    /**
     * Token service management
     */
    private final DefaultTokenServices defaultTokenServices;

    /**
     * Authentication failure manager
     */
    private final ApiAuthenticationFailureHandler apiAuthenticationFailureHandler;

    /**
     * Constructor
     *
     * @param apiAuthenticationFailureHandler The authentication failure manager
     * @param defaultTokenServices            The default token service
     */
    @Autowired
    public OAuth2ResourceServerConfiguration(ApiAuthenticationFailureHandler apiAuthenticationFailureHandler, DefaultTokenServices defaultTokenServices) {
        this.apiAuthenticationFailureHandler = apiAuthenticationFailureHandler;
        this.defaultTokenServices = defaultTokenServices;
    }

    /**
     * Configure the resource server
     *
     * @param config The configuration
     */
    @Override
    public void configure(ResourceServerSecurityConfigurer config) {
        config.tokenServices(defaultTokenServices);
    }

    /**
     * Resource Security
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(apiAuthenticationFailureHandler)
                .accessDeniedHandler(apiAuthenticationFailureHandler)
            .and()
                .headers()
                .frameOptions().disable()
            .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
                .anonymous()
            .and()
                .authorizeRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/api/oauth/token").permitAll()
                .antMatchers("/api/*/users/register").permitAll()
                .antMatchers("/api/*/configurations/authentication-provider").permitAll()
                .antMatchers("/api/*/projects/{projectToken}").permitAll()
                .antMatchers("/api/*/projectWidgets/{projectToken}/projectWidgets").permitAll()
                .antMatchers("/api/*/projectWidgets/{projectWidgetId}").permitAll()
                .antMatchers("/api/*/widgets/{widgetId}").permitAll()
                .antMatchers("/api/*/settings").permitAll()
                .antMatchers("/api/*/assets/**").permitAll()
                .antMatchers("/ws/**").permitAll()
                .antMatchers("/api/**").authenticated();
    }
}
