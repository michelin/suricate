package com.michelin.suricate.utils.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import javax.servlet.http.Cookie;
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
            "rO0ABXNyABlqYXZheC5zZXJ2bGV0Lmh0dHAuQ29va2llAAAAAAAAAAECAAlaAAhodHRwT25seUkABm1"
                + "heEFnZVoABnNlY3VyZUkAB3ZlcnNpb25MAAdjb21tZW50dAASTGphdmEvbGFuZy9TdHJpbmc7TAAGZG9tY"
                + "WlucQB-AAFMAARuYW1lcQB-AAFMAARwYXRocQB-AAFMAAV2YWx1ZXEAfgABeHAA_____wAAAAAAcHB0AAh"
                + "teUNvb2tpZXB0AAV2YWx1ZQ==");
    }

    @Test
    void shouldDeserializeCookieValue() {
        Cookie cookie = new Cookie("myCookie",
            "rO0ABXNyABlqYXZheC5zZXJ2bGV0Lmh0dHAuQ29va2llAAAAAAAAAAECAAlaAAhodHRwT25seUkABm1heE"
                + "FnZVoABnNlY3VyZUkAB3ZlcnNpb25MAAdjb21tZW50dAASTGphdmEvbGFuZy9TdHJpbmc7TAAGZG9tYWlu"
                + "cQB-AAFMAARuYW1lcQB-AAFMAARwYXRocQB-AAFMAAV2YWx1ZXEAfgABeHAA_____wAAAAAAcHB0AAhteUN"
                + "vb2tpZXB0AAV2YWx1ZQ==");

        Cookie deserialized = CookieUtils.deserialize(cookie, Cookie.class);

        assertThat(deserialized).isNotNull();
        assertThat(deserialized.getName()).isEqualTo("myCookie");
        assertThat(deserialized.getValue()).isEqualTo("value");
    }
}
