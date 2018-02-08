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

import io.suricate.monitoring.config.LoginSuccessHandler;
import io.suricate.monitoring.config.jsf.AuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.header.writers.DelegatingRequestMatcherHeaderWriter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

@Configuration
@Order(2)
@ConditionalOnProperty(name = "security.authentication-provider", havingValue = "ldap") // FIXME remove this with ldif
public class BackendWebSecurityConfig extends AbstractWebSecurityConfig {

    /**
     * Login path
     */
    private static final String LOGIN_PATH = "/login";

    @Value("${management.context-path}")
    private String acuatorPath;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // Ignore csrf for h2 console
        http.csrf()
                .ignoringAntMatchers("/h2-console/**","/ws/**");

        http.authorizeRequests()
                .antMatchers("/ws/**").permitAll()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers(acuatorPath+"/**").permitAll()
                .antMatchers("/content/tv/**","/tv").permitAll()
                .antMatchers("/webjars/**").permitAll()
                .antMatchers(LOGIN_PATH,"/error", "/error.xhtml").permitAll()
                .antMatchers("/assets/**","/javax.faces.resource/**").permitAll()
                .antMatchers("/content/user/**").hasRole("USER")
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage(LOGIN_PATH)
                .successHandler(new LoginSuccessHandler())
                .permitAll()
                .and()
            .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl(LOGIN_PATH)
                .permitAll()
                .and()
            .exceptionHandling()
                .authenticationEntryPoint(new AuthenticationEntryPoint(LOGIN_PATH))
                .accessDeniedPage("/error")
            .and()
                .headers()
                .frameOptions()
                .disable()
                .addHeaderWriter(
                        new DelegatingRequestMatcherHeaderWriter(
                                new NegatedRequestMatcher(new OrRequestMatcher( new RegexRequestMatcher("/tv|/.*/tv/.*",null))),
                                new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN))
                );
    }
}
