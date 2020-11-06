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

package io.suricate.monitoring.configuration.security;

import io.suricate.monitoring.configuration.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.util.CollectionUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Global Security configurations
 */
@Configuration
@EnableWebSecurity
@EnableOAuth2Client
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    /**
     * The logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfiguration.class);
    /**
     * Application properties from properties file
     */
    private final ApplicationProperties applicationProperties;

    /**
     * Constructor
     *
     * @param applicationProperties Application properties
     */
    @Autowired
    public SecurityConfiguration(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    /**
     * Global Security
     */
    @Override
    public void configure(WebSecurity web) {
        web
            .expressionHandler(defaultWebSecurityExpressionHandler())
            .ignoring()
                .antMatchers(HttpMethod.OPTIONS);
    }

    /**
     * Get the expression handler
     */
    private DefaultWebSecurityExpressionHandler defaultWebSecurityExpressionHandler() {
        DefaultWebSecurityExpressionHandler defaultWebSecurityExpressionHandler = new DefaultWebSecurityExpressionHandler();
        defaultWebSecurityExpressionHandler.setRoleHierarchy(roleHierarchy());

        return defaultWebSecurityExpressionHandler;
    }

    /**
     * Application Role hierarchy for security management
     */
    @Bean
    protected RoleHierarchyImpl roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
        return roleHierarchy;
    }

    /**
     * Cors filter policy from Application properties
     */
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        if (!CollectionUtils.isEmpty(applicationProperties.cors.getAllowedOrigins())) {
            LOGGER.debug("Registering CORS filter");
            source.registerCorsConfiguration("/api/**", applicationProperties.cors);
        }

        FilterRegistrationBean<CorsFilter> filterRegistrationBean = new FilterRegistrationBean<>(new CorsFilter(source));
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return filterRegistrationBean;
    }

    /**
     *
     * @return Default authentication manager
     * @throws Exception
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
