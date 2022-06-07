package io.suricate.monitoring.security.oauth2;

import io.suricate.monitoring.utils.jwt.JwtUtils;
import io.suricate.monitoring.utils.web.CookieUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static io.suricate.monitoring.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    /**
     * The logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler.class);

    /**
     * The authentication request repository
     * Store the authentication request in an HTTP cookie on the IDP response
     */
    @Autowired
    private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    /**
     * Store the OAuth2 authorized client
     */
    @Autowired
    private OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository;

    /**
     * The token provider
     */
    @Autowired
    private JwtUtils tokenProvider;

    /**
     * Trigger after OAuth2 authentication has been successful
     * @param request The request which is the response of the IDP
     * @param response The response to send to the host that authenticated successfully
     * @param authentication The authentication data
     * @throws IOException Any IO Exception
     * @throws ServletException Any servlet exception
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
       String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            LOGGER.debug("Response has already been committed. Unable to redirect to " + targetUrl);
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

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        OAuth2AuthenticationToken auth = (OAuth2AuthenticationToken) authentication;
        OAuth2AuthorizedClient authorizedClient = oAuth2AuthorizedClientRepository.loadAuthorizedClient(auth.getAuthorizedClientRegistrationId(), authentication, request);

        LOGGER.debug("Access token {} generated by {}. Self-generated JWT token will be used instead.", authorizedClient.getAccessToken().getTokenValue(), authorizedClient.getClientRegistration().getRegistrationId());

        String token = tokenProvider.createToken(authentication, false);

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", token)
                .build().toUriString();
    }

    /**
     * Remove authorization cookies from the IDP response before transiting it to frontend
     * @param request The request, which is the response of the IDP, that contains the cookies to remove
     * @param response The response of the request that won't contain the cookies
     */
    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }
}
