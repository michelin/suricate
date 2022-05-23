package io.suricate.monitoring.configuration.security.oauth2;

import io.suricate.monitoring.configuration.security.jwt.TokenProvider;
import io.suricate.monitoring.configuration.security.web.AuthenticationFailureEntryPoint;
import io.suricate.monitoring.model.entities.User;
import io.suricate.monitoring.properties.ApplicationProperties;
import io.suricate.monitoring.services.api.UserService;
import io.suricate.monitoring.utils.web.CookieUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.log.LogMessage;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import static io.suricate.monitoring.configuration.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    /**
     * The logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler.class);

    /**
     * The user service
     */
    @Autowired
    private UserService userService;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
       String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    /**
     * Determine the host to redirect after being successfully authenticated.
     * First, try to get the host from the HTTP cookie attached on the IDP response.
     * If empty, try to get the host from the referer.
     * If still empty, get a default / host.
     * Build a JWT token and add it as a parameter to the response
     * @param request The request which is the response of the IDP
     * @param response The response to send to the host that authenticated successfully
     * @param authentication The authentication data
     * @return The host to redirect
     */
    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME).map(Cookie::getValue);

        if (!redirectUri.isPresent()) {
            redirectUri = Optional.of(request.getHeader("Referer"));
            LOGGER.debug(String.format("Using url %s from Referer header", request.getHeader("Referer")));
        }

        if (StringUtils.hasLength(redirectUri.get()) && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw new RuntimeException(String.format("An error occurred: the redirect URI %s is not authorized", redirectUri));
        }

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        String username = ((ConnectedOAuth2User) authentication.getPrincipal()).getAttributes().get("login").toString();
        User user = userService.getOneByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User " + username + " was not authorized"));
        String token = tokenProvider.createToken(user);

        OAuth2AuthenticationToken auth = (OAuth2AuthenticationToken) authentication;
        OAuth2AuthorizedClient authorizedClient = oAuth2AuthorizedClientRepository.loadAuthorizedClient(auth.getAuthorizedClientRegistrationId(), authentication, request);

        LOGGER.debug("Access token {} generated from {}. Going to use self-generated {} JWT token instead.",
                authorizedClient.getAccessToken().getTokenValue(), authorizedClient.getClientRegistration().getRegistrationId(), token);

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", token)
                .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    /**
     * Check if the given URI to redirect is authorized by the Back-End
     * @param uri The URI to check
     * @return true if it is authorized, false otherwise
     */
    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);

        return applicationProperties.getAuthentication().getOAuth2().getAuthorizedRedirectUris()
            .stream()
            .anyMatch(authorizedRedirectUri -> {
                // Only validate host and port. Let the clients use different paths if they want
                URI authorizedURI = URI.create(authorizedRedirectUri);
                return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost()) && authorizedURI.getPort() == clientRedirectUri.getPort();
            });
    }
}
