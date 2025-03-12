/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.michelin.suricate.util.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        assertTrue(actual.isPresent());
        assertEquals(cookie, actual.get());
    }

    @Test
    void shouldGetCookieEmpty() {
        assertTrue(
                CookieUtils.getCookie(new MockHttpServletRequest(), "myCookie").isEmpty());
    }

    @Test
    void shouldNotGetCookieWhenNotFound() {
        Cookie cookie = new Cookie("myCookie", "value");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(cookie);

        assertTrue(CookieUtils.getCookie(request, "myNotFoundCookie").isEmpty());
    }

    @Test
    void shouldAddCookie() {
        MockHttpServletResponse response = new MockHttpServletResponse();

        CookieUtils.addCookie(response, "myCookie", "value", 10);

        Cookie actual = response.getCookie("myCookie");

        assertNotNull(actual);
        assertEquals("/", actual.getPath());
        assertEquals("myCookie", actual.getName());
        assertEquals("value", actual.getValue());
        assertEquals(10, actual.getMaxAge());
        assertTrue(actual.isHttpOnly());
        assertTrue(actual.getSecure());
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

        assertNotNull(actual);
        assertEquals("/", actual.getPath());
        assertEquals("myCookie", actual.getName());
        assertTrue(actual.getValue().isEmpty());
        assertEquals(0, actual.getMaxAge());
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

        assertNotNull(actual);
        assertEquals("myCookie", actual.getName());
        assertEquals("value", actual.getValue());
    }

    @Test
    void shouldSerialize() {
        assertEquals(
                "rO0ABXNyABtqYWthcnRhLnNlcnZsZXQuaHR0cC5Db29raWUAAAAAAAAAAgIAA0"
                        + "wACmF0dHJpYnV0ZXN0AA9MamF2YS91dGlsL01hcDtMAARuYW1ldAASTGphdmEvbGF"
                        + "uZy9TdHJpbmc7TAAFdmFsdWVxAH4AAnhwcHQACG15Q29va2lldAAFdmFsdWU=",
                CookieUtils.serialize(new Cookie("myCookie", "value")));
    }

    @Test
    void shouldDeserializeCookieValue() {
        Cookie cookie = new Cookie(
                "myCookie",
                "rO0ABXNyABtqYWthcnRhLnNlcnZsZXQuaHR0cC5Db29raWUAAAAAAAAAAgIAA0"
                        + "wACmF0dHJpYnV0ZXN0AA9MamF2YS91dGlsL01hcDtMAARuYW1ldAASTGphdmEvbGF"
                        + "uZy9TdHJpbmc7TAAFdmFsdWVxAH4AAnhwcHQACG15Q29va2lldAAFdmFsdWU");

        Cookie deserialized = CookieUtils.deserialize(cookie, Cookie.class);

        assertNotNull(deserialized);
        assertEquals("myCookie", deserialized.getName());
        assertEquals("value", deserialized.getValue());
    }
}
