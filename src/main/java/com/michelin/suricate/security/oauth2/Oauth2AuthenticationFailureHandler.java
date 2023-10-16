package com.michelin.suricate.security.oauth2;

import static com.michelin.suricate.security.oauth2.HttpCookieOauth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

import com.michelin.suricate.properties.ApplicationProperties;
import com.michelin.suricate.utils.web.CookieUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Handle OAuth2 authentication failure.
 */
@Slf4j
@Component
public class Oauth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    /**
     * The authentication request repository
     * Store the authentication request in an HTTP cookie on the IDP response.
     */
    @Autowired
    private HttpCookieOauth2AuthorizationRequestRepository authorizationRequestRepository;

    /**
     * The application properties.
     */
    @Autowired
    private ApplicationProperties applicationProperties;

    /**
     * Trigger after OAuth2 authentication has failed.
     *
     * @param request   The request which is the response of the IDP
     * @param response  The response to send to the host that authenticated successfully
     * @param exception The authentication exception
     * @throws IOException Any IO Exception
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        Optional<String> redirectUri =
            CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME).map(Cookie::getValue);

        if (redirectUri.isEmpty() && applicationProperties.getAuthentication().getOauth2().isUseReferer()) {
            redirectUri = Optional.ofNullable(request.getHeader(HttpHeaders.REFERER));
            redirectUri.ifPresent(
                redirect -> log.debug("Using url {} from Referer header", request.getHeader(HttpHeaders.REFERER)));
        }

        String targetUrl =
            redirectUri.orElse(applicationProperties.getAuthentication().getOauth2().getDefaultTargetUrl());
        if (StringUtils.isBlank(targetUrl)) {
            targetUrl = "/";
        }

        targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
            .queryParam("error", exception.getLocalizedMessage())
            .build().toUriString();

        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
