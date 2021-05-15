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

package io.suricate.monitoring.configuration.security.oauth2;

import io.suricate.monitoring.configuration.ApplicationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

/**
 * OAuth Authorization server : Manage authorization before requesting the resource server
 */
@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    /**
     * Manage authentications
     */
    private final AuthenticationManager authenticationManager;

    /**
     * Service that hold tokens (JWT)
     */
    private final TokenStore tokenStore;

    /**
     * Service used for decoding tokens
     */
    private final JwtAccessTokenConverter jwtAccessTokenConverter;

    /**
     * Application properties
     */
    private final ApplicationProperties applicationProperties;

    /**
     * Password encoder
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor
     *
     * @param authenticationManager Authentication manager
     * @param tokenStore Token store service
     * @param jwtAccessTokenConverter Token converter service
     */
    @Autowired
    public OAuth2AuthorizationServerConfiguration(AuthenticationManager authenticationManager, TokenStore tokenStore,
                                                  JwtAccessTokenConverter jwtAccessTokenConverter, ApplicationProperties applicationProperties,
                                                  PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.tokenStore = tokenStore;
        this.jwtAccessTokenConverter = jwtAccessTokenConverter;
        this.applicationProperties = applicationProperties;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Configure the endpoints by redefine OAuth connection path, inject token and authentication manager
     *
     * @param endpoints configurer for enpoints
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints
            .pathMapping("/oauth/token", "/api/oauth/token")
            .tokenStore(tokenStore)
            .accessTokenConverter(jwtAccessTokenConverter)
            .authenticationManager(authenticationManager);
    }

    /**
     * OAuth authorization server policy
     *
     * @param oauthServer The Oauth server to configure
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
        oauthServer
            .tokenKeyAccess("permitAll()")
            .checkTokenAccess("isAuthenticated()");
    }

    /**
     * Access OAuth server from clients
     *
     * @param clients Define clients who have access to the server
     * @throws Exception When an error throw
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients
            .inMemory()
                .withClient(applicationProperties.oauth.client)
                .secret(passwordEncoder.encode(applicationProperties.oauth.secret))
                .authorizedGrantTypes("password", "refresh_token")
                .scopes("read", "write")
                .accessTokenValiditySeconds(10000);
    }
}
