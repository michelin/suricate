/*
 * Copyright 2012-2018 the original author or authors.
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

package io.suricate.monitoring.config.security;

import io.suricate.monitoring.config.security.token.jwt.JWTConfigurer;
import io.suricate.monitoring.controllers.api.error.ApiAuthenticationFailureHandler;
import io.suricate.monitoring.service.token.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.web.cors.CorsUtils;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final TokenService tokenService;
    private final RoleHierarchyImpl roleHierarchy;
    private final ApiAuthenticationFailureHandler apiAuthenticationFailureHandler;

    @Autowired
    public SecurityConfiguration(RoleHierarchyImpl roleHierarchy, ApiAuthenticationFailureHandler apiAuthenticationFailureHandler, TokenService tokenService) {
        this.roleHierarchy = roleHierarchy;
        this.apiAuthenticationFailureHandler = apiAuthenticationFailureHandler;
        this.tokenService = tokenService;
    }

    /**
     * Global Security
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web
            .expressionHandler(defaultWebSecurityExpressionHandler());
    }

    /**
     * Get the expression handler
     */
    private DefaultWebSecurityExpressionHandler defaultWebSecurityExpressionHandler() {
        DefaultWebSecurityExpressionHandler defaultWebSecurityExpressionHandler = new DefaultWebSecurityExpressionHandler();
        defaultWebSecurityExpressionHandler.setRoleHierarchy(roleHierarchy);

        return defaultWebSecurityExpressionHandler;
    }

    /**
     * Resource Security
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(apiAuthenticationFailureHandler)
                .accessDeniedHandler(apiAuthenticationFailureHandler)
            .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
                .authorizeRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .antMatchers("/api/**").authenticated()
                .antMatchers("/api/login/**").permitAll()
            .and()
                .apply(jwtConfigurerAdapter());
    }

    private JWTConfigurer jwtConfigurerAdapter() {
        return new JWTConfigurer(tokenService);
    }
}
