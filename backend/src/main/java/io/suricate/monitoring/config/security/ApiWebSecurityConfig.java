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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsUtils;

@Configuration
@Order(1)
public class ApiWebSecurityConfig extends AbstractWebSecurityConfig {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
                .csrf()
                .disable()
            .cors()
            .and()
                .authorizeRequests()
                .antMatchers("/"+apiPrefix+"/asset/**")
                .permitAll()
            .and()
                .antMatcher("/"+apiPrefix+"/login")
                .httpBasic()
                .authenticationEntryPoint(basicAuthenticationEntryPoint())
            .and()
                .antMatcher("/"+apiPrefix+"/**")
                .authorizeRequests()
                    .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                    .anyRequest().permitAll()
        ;
    }


    @Override
    public void configure(WebSecurity web) throws Exception {
        // Ignore public assets
        web.ignoring().antMatchers("/"+apiPrefix+"/asset/**");
    }

    /**
     * Authentication entry point for basic authentication
     * @return
     */
    @Bean
    public BasicAuthenticationEntryPoint basicAuthenticationEntryPoint(){
        BasicAuthenticationEntryPoint entryPoint =  new BasicAuthenticationEntryPoint();
        entryPoint.setRealmName("API Authentication");
        return entryPoint;
    }
}
