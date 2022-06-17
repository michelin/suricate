package io.suricate.monitoring.security.oauth2;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import io.suricate.monitoring.utils.web.CookieUtils;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Authorization request repository class.
 * Used to store the authorization request in an HTTP cookie to be stateless
 */
@Component
public class HttpCookieOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
    /**
     * Name of the cookie containing the authorization request
     */
    public static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";

    /**
     * Name of the cookie containing the "redirect_uri" frontend parameter
     */
    public static final String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri";

    /**
     * Cookie expiration time
     */
    private static final int COOKIE_EXPIRE_SECONDS = 180;

    /**
     * Load the authorization request from the cookie stored in the IDP response we set before authenticating
     * @param request The IDP response
     * @return The authorization request
     */
    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return CookieUtils.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME).map(cookie -> CookieUtils.deserialize(cookie, OAuth2AuthorizationRequest.class))
                .orElse(null);
    }

    /**
     * Save the authorization request and the "redirect_uri" frontend parameter in cookies
     * and attach them in the IDP response.
     * The authorization request needs to be saved somewhere because it contains a state parameter
     * sent to the IDP and expected in the IDP response to be the same, otherwise
     * the authentication fails. It works like a CSRF token.
     * The authentication request is saved into a cookie to be stateless (stored in session by default).
     * The "redirect_uri" frontend parameter is also saved in a cookie of the IDP response to be retrieved later
     * and used to respond to the frontend.
     * @param authorizationRequest The authorization request that will be sent to the IDP
     * @param request The frontend request. Can contain a custom "redirect_uri" parameter to redirect
     * @param response The IDP response. Empty for now, contains only the attached cookies
     */
    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        if (authorizationRequest == null) {
            CookieUtils.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
            CookieUtils.deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME);
            return;
        }

        CookieUtils.addCookie(response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, CookieUtils.serialize(authorizationRequest), COOKIE_EXPIRE_SECONDS);
        String redirectUriAfterLogin = request.getParameter(REDIRECT_URI_PARAM_COOKIE_NAME);
        if (StringUtils.isNotBlank(redirectUriAfterLogin)) {
            CookieUtils.addCookie(response, REDIRECT_URI_PARAM_COOKIE_NAME, redirectUriAfterLogin, COOKIE_EXPIRE_SECONDS);
        }
    }

    /**
     * Remove the authorization request.
     * Strange behavior here, the method needs to return the authorization request, not delete it because
     * loadAuthorizationRequest is never invoked otherwise.
     * @param request The IDP response. Contains the cookies we set before authenticating
     * @return The authorization request
     */
    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {
        return loadAuthorizationRequest(request);
    }

    /**
     * Remove authorization cookies from a given request
     * @param request The request that contains the cookies to remove
     * @param response The response of the request that won't contain the cookies
     */
    public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
        CookieUtils.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        CookieUtils.deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME);
    }
}
