package com.michelin.suricate.utils.web;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.Base64;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.SerializationUtils;

/**
 * Utility class for cookies.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CookieUtils {
    /**
     * Get cookie by name.
     *
     * @param request The request where the cookie is attached
     * @param name    The name of the cookie
     * @return The cookie if it exists
     */
    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return Optional.of(cookie);
                }
            }
        }

        return Optional.empty();
    }

    /**
     * Add an HTTP cookie to the given HTTP response.
     *
     * @param response The HTTP response
     * @param name     The name of the cookie
     * @param value    The value of the cookie
     * @param maxAge   The age of the cookie
     */
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        cookie.setSecure(true);
        response.addCookie(cookie);
    }

    /**
     * Delete cookie from the response by adding a new empty cookie with no max age.
     *
     * @param request  The request that currently contains the cookie
     * @param response The response of the request, that won't contain the cookie anymore
     * @param name     The name of the cookie to delete
     */
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
    }

    /**
     * Serialize a given object to base64.
     *
     * @param object The object to serialize
     * @return The encoded object as base64
     */
    public static String serialize(Serializable object) {
        return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(object));
    }

    /**
     * Deserialize the value of a given cookie into the given class.
     *
     * @param cookie The cookie from which to deserialize the value
     * @param cls    The target class into deserialize
     * @param <T>    The class type
     * @return The deserialized cookie value
     */
    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(SerializationUtils.deserialize(Base64.getUrlDecoder().decode(cookie.getValue())));
    }
}
