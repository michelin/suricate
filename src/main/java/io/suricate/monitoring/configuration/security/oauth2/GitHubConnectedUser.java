package io.suricate.monitoring.configuration.security.oauth2;

import io.suricate.monitoring.configuration.security.common.ConnectedUser;
import io.suricate.monitoring.model.enums.AuthenticationMethod;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;

public class GitHubConnectedUser extends ConnectedUser {
    /**
     * Constructor
     * @param username The username
     * @param oAuth2User The OAuth2 user data
     */
    public GitHubConnectedUser(OAuth2User oAuth2User) {
        super(oAuth2User.getAttribute("login").toString().toLowerCase(), StringUtils.EMPTY, true, true, true, true, Collections.emptyList());

        this.firstname = oAuth2User.getAttribute("name").toString().split(" ")[0];
        this.lastname = oAuth2User.getAttribute("name").toString().split(" ")[1];
        this.email = oAuth2User.getAttribute("email");
        this.authenticationMethod = AuthenticationMethod.GITHUB;
    }
}
