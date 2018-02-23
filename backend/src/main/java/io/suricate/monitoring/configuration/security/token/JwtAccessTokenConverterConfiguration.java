package io.suricate.monitoring.configuration.security.token;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class JwtAccessTokenConverterConfiguration extends JwtAccessTokenConverter {

    /**
     * Use this method if you want to add some extra informations on the token
     *
     * @param accessToken The token
     * @param authentication the authentication
     */
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        Map<String, Object> additionalTokenInformations = new HashMap<>();

        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalTokenInformations);
        return super.enhance(accessToken, authentication);
    }
}
