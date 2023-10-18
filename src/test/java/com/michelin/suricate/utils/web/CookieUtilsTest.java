package com.michelin.suricate.utils.web;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.servlet.http.Cookie;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class CookieUtilsTest {

    @Test
    void shouldGetCookie() {
        Cookie cookie = new Cookie("myCookie", "value");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(cookie);

        Optional<Cookie> actual = CookieUtils.getCookie(request, "myCookie");

        assertThat(actual)
            .isPresent()
            .contains(cookie);
    }

    @Test
    void shouldGetCookieEmpty() {
        assertThat(CookieUtils.getCookie(new MockHttpServletRequest(), "myCookie")).isEmpty();
    }

    @Test
    void shouldNotGetCookieWhenNotFound() {
        Cookie cookie = new Cookie("myCookie", "value");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(cookie);

        assertThat(CookieUtils.getCookie(request, "myNotFoundCookie")).isEmpty();
    }

    @Test
    void shouldAddCookie() {
        MockHttpServletResponse response = new MockHttpServletResponse();

        CookieUtils.addCookie(response, "myCookie", "value", 10);

        Cookie actual = response.getCookie("myCookie");

        assertThat(actual).isNotNull();
        assertThat(actual.getPath()).isEqualTo("/");
        assertThat(actual.getName()).isEqualTo("myCookie");
        assertThat(actual.getValue()).isEqualTo("value");
        assertThat(actual.getMaxAge()).isEqualTo(10);
        assertThat(actual.isHttpOnly()).isTrue();
        assertThat(actual.getSecure()).isTrue();
    }

    @Test
    void shouldDeleteCookie() {
        Cookie cookie = new Cookie("myCookie", "value");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(cookie);

        MockHttpServletResponse response = new MockHttpServletResponse();
        response.addCookie(cookie);

        CookieUtils.deleteCookie(request, response, "myCookie");

        Cookie actual = response.getCookie("myCookie");

        assertThat(actual).isNotNull();
        assertThat(actual.getName()).isEqualTo("myCookie");
        assertThat(actual.getValue()).isEmpty();
        assertThat(actual.getPath()).isEqualTo("/");
        assertThat(actual.getMaxAge()).isZero();
    }

    @Test
    void shouldNotDeleteCookieWhenNotExistOnRequest() {
        Cookie otherCookie = new Cookie("myOtherCookie", "value");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(otherCookie);

        Cookie cookie = new Cookie("myCookie", "value");
        MockHttpServletResponse response = new MockHttpServletResponse();
        response.addCookie(cookie);

        CookieUtils.deleteCookie(request, response, "myCookie");

        Cookie actual = response.getCookie("myCookie");

        assertThat(actual).isNotNull();
        assertThat(actual.getName()).isEqualTo("myCookie");
        assertThat(actual.getValue()).isEqualTo("value");
    }

    @Test
    void shouldSerialize() {
        assertThat(CookieUtils.serialize(new Cookie("myCookie", "value"))).isEqualTo(
            "rO0ABXNyABtqYWthcnRhLnNlcnZsZXQuaHR0cC5Db29raWUAAAAAAAAAAgIAA0"
                + "wACmF0dHJpYnV0ZXN0AA9MamF2YS91dGlsL01hcDtMAARuYW1ldAASTGphdmEvbGF"
                + "uZy9TdHJpbmc7TAAFdmFsdWVxAH4AAnhwcHQACG15Q29va2lldAAFdmFsdWU=");
    }

    @Test
    void shouldDeserializeCookieValue() {
        Cookie cookie = new Cookie("myCookie",
            "rO0ABXNyABtqYWthcnRhLnNlcnZsZXQuaHR0cC5Db29raWUAAAAAAAAAAgIAA0"
                + "wACmF0dHJpYnV0ZXN0AA9MamF2YS91dGlsL01hcDtMAARuYW1ldAASTGphdmEvbGF"
                + "uZy9TdHJpbmc7TAAFdmFsdWVxAH4AAnhwcHQACG15Q29va2lldAAFdmFsdWU");

        Cookie deserialized = CookieUtils.deserialize(cookie, Cookie.class);

        assertThat(deserialized).isNotNull();
        assertThat(deserialized.getName()).isEqualTo("myCookie");
        assertThat(deserialized.getValue()).isEqualTo("value");
    }
}
