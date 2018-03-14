package io.suricate.monitoring.configuration.security.oauth2;

import io.suricate.monitoring.controllers.api.error.ApiAuthenticationFailureHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.web.cors.CorsUtils;

@Configuration
@EnableResourceServer
public class OAuth2ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    private final DefaultTokenServices defaultTokenServices;
    private final ApiAuthenticationFailureHandler apiAuthenticationFailureHandler;

    @Autowired
    public OAuth2ResourceServerConfiguration(ApiAuthenticationFailureHandler apiAuthenticationFailureHandler, DefaultTokenServices defaultTokenServices) {
        this.apiAuthenticationFailureHandler = apiAuthenticationFailureHandler;
        this.defaultTokenServices = defaultTokenServices;
    }

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
                    .antMatchers("/api/**").authenticated();
    }
}
