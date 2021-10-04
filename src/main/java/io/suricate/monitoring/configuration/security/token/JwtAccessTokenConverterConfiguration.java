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

package io.suricate.monitoring.configuration.security.token;

import io.suricate.monitoring.configuration.security.ConnectedUser;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.HashMap;
import java.util.Map;

/**
 * Token informations holder
 */
@Configuration
public class JwtAccessTokenConverterConfiguration extends JwtAccessTokenConverter {

    /**
     * Use this method if you want to add some extra informations on the token
     *
     * Hidden data have to be inserted inside authentication
     *
     * @param accessToken The token
     * @param authentication the authentication
     */
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        ConnectedUser connectedUser = (ConnectedUser) authentication.getUserAuthentication().getPrincipal();

        Map<String, Object> additionalTokenInformation = new HashMap<>();
        additionalTokenInformation.put("firstname", connectedUser.getFirstname());
        additionalTokenInformation.put("lastname", connectedUser.getLastname());
        additionalTokenInformation.put("mail", connectedUser.getMail());

        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalTokenInformation);
        return super.enhance(accessToken, authentication);
    }
}
