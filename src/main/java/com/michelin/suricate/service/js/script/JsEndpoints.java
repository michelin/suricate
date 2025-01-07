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

package com.michelin.suricate.service.js.script;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.michelin.suricate.util.exception.js.FatalException;
import com.michelin.suricate.util.exception.js.RemoteException;
import com.michelin.suricate.util.exception.js.RequestException;
import com.michelin.suricate.util.http.OkHttpClientUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.concurrent.TimeoutException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

/**
 * Javascript endpoints called by the widgets.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JsEndpoints {
    /**
     * Create and submit an HTTP request according to the given parameters.
     *
     * @param url            The URL of the endpoint to call
     * @param headerName     The name of the header to add
     * @param headerValue    The value to set to the added header
     * @param headerToReturn The name of the header to return
     * @param body           The body of the request. Can be null in case of GET HTTP request
     * @return The response body of the request or the value of the requested header
     * @throws IOException      If an error occurred during the execution of the request
     * @throws RemoteException  If an error occurred during the execution of the request
     * @throws RequestException If an error occurred during the execution of the request
     */
    private static String executeRequest(String url, String headerName, String headerValue, String headerToReturn,
                                         String body, boolean returnCode)
        throws IOException, RemoteException, RequestException {
        Request.Builder builder = new Request.Builder().url(url);

        if (StringUtils.isNotBlank(headerName)) {
            builder.addHeader(headerName, headerValue);
        }

        if (StringUtils.isNotBlank(body)) {
            builder.post(
                RequestBody.create(body, MediaType.parse(APPLICATION_JSON_VALUE)));
        }

        Request request = builder.build();
        String returnedValue;

        try (Response response = OkHttpClientUtils.getUnsafeOkHttpClient().newCall(request).execute()) {
            if (returnCode) {
                return String.valueOf(response.code());
            }
            if (response.isSuccessful()) {
                if (StringUtils.isNotBlank(headerToReturn)) {
                    returnedValue = response.header(headerToReturn);
                } else {
                    returnedValue = Objects.requireNonNull(response.body()).string();
                }
            } else {
                if (response.code() >= HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                    throw new RemoteException("A server error occurred during the execution of the request /"
                        + response.request().method() + " " + response.request().url() + " (code " + response.code()
                        + ").");
                } else {
                    throw new RequestException("A request error occurred during the execution of the request /"
                        + response.request().method() + " " + response.request().url() + " (code " + response.code()
                        + "). Error body details: "
                        + (response.body() != null ? Objects.requireNonNull(response.body()).string() : "Empty body"));
                }
            }
        }

        return returnedValue;
    }

    /**
     * Perform a GET HTTP call.
     *
     * @param url The URL of the endpoint to call
     * @return The response body of the request
     */
    public static String get(String url) throws RemoteException, IOException, RequestException {
        return JsEndpoints.executeRequest(url, null, null, null, null, false);
    }

    /**
     * Perform a GET HTTP call.
     *
     * @param url        The URL of the endpoint to call
     * @param returnCode true if you want to only return the http status code, false otherwise
     * @return The http status code
     */
    public static String get(String url, boolean returnCode) throws IOException, RemoteException, RequestException {
        return JsEndpoints.executeRequest(url, null, null, null, null, returnCode);
    }

    /**
     * Perform a GET HTTP call.
     *
     * @param url         The URL of the endpoint to call
     * @param headerName  The name of the header to add
     * @param headerValue The value to set to the added header
     * @return The response body of the request
     */
    public static String get(String url, String headerName, String headerValue)
        throws RemoteException, IOException, RequestException {
        return JsEndpoints.executeRequest(url, headerName, headerValue, null, null, false);
    }

    /**
     * Perform a GET HTTP call.
     *
     * @param url         The URL of the endpoint to call
     * @param headerName  The name of the header to add
     * @param headerValue The value to set to the added header
     * @param returnCode  true if you want to only return the http status code, false otherwise
     * @return The response body of the request
     */
    public static String get(String url, String headerName, String headerValue, boolean returnCode)
        throws RemoteException, IOException, RequestException {
        return JsEndpoints.executeRequest(url, headerName, headerValue, null, null, returnCode);
    }

    /**
     * Perform a GET HTTP call.
     *
     * @param url            The URL of the endpoint to call
     * @param headerName     The name of the header to add
     * @param headerValue    The value to set to the added header
     * @param headerToReturn The name of the header to return
     * @return The requested header
     */
    public static String get(String url, String headerName, String headerValue, String headerToReturn)
        throws RemoteException, IOException, RequestException {
        return JsEndpoints.executeRequest(url, headerName, headerValue, headerToReturn, null, false);
    }

    /**
     * Perform a POST HTTP call.
     *
     * @param url  The URL of the endpoint to call
     * @param body The body of the POST request
     * @return The response body of the request
     */
    public static String post(String url, String body) throws RemoteException, IOException, RequestException {
        return JsEndpoints.executeRequest(url, null, null, null, StringUtils.isBlank(body) ? "{}" : body,
            false);
    }

    /**
     * Perform a POST HTTP call.
     *
     * @param url        The URL of the endpoint to call
     * @param body       The body of the POST request
     * @param returnCode true if you want to only return the http status code, false otherwise
     * @return The response body of the request
     */
    public static String post(String url, String body, boolean returnCode)
        throws RemoteException, IOException, RequestException {
        return JsEndpoints.executeRequest(url, null, null, null, StringUtils.isBlank(body) ? "{}" : body,
            returnCode);
    }

    /**
     * Perform a POST HTTP call.
     *
     * @param url         The URL of the endpoint to call
     * @param body        The body of the POST request
     * @param headerName  The name of the header to add
     * @param headerValue The value to set to the added header
     * @return The response body of the request
     */
    public static String post(String url, String body, String headerName, String headerValue)
        throws RemoteException, IOException, RequestException {
        return JsEndpoints.executeRequest(url, headerName, headerValue, null, StringUtils.isBlank(body) ? "{}" : body,
            false);
    }

    /**
     * Perform a POST HTTP call.
     *
     * @param url         The URL of the endpoint to call
     * @param body        The body of the POST request
     * @param headerName  The name of the header to add
     * @param headerValue The value to set to the added header
     * @param returnCode  true if you want to only return the http status code, false otherwise
     * @return The response body of the request
     */
    public static String post(String url, String body, String headerName, String headerValue, boolean returnCode)
        throws RemoteException, IOException, RequestException {
        return JsEndpoints.executeRequest(url, headerName, headerValue, null, StringUtils.isBlank(body) ? "{}" : body,
            returnCode);
    }

    /**
     * Check if a thread is interrupted.
     * This method is injected during the Js execution preparation
     *
     * @throws InterruptedException an exception if the thread is interrupted
     */
    public static void checkInterrupted() throws InterruptedException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException("Script Interrupted");
        }
    }

    /**
     * Convert ASCII string to base 64.
     * This method is directly called by the widgets especially to encrypt credentials
     *
     * @param data The string to convert
     * @return A String encoded with Base64
     */
    public static String btoa(String data) {
        if (StringUtils.isBlank(data)) {
            return null;
        }
        return Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Throw a remote error.
     *
     * @throws RemoteException The error thrown
     */
    public static void throwError() throws RemoteException {
        throw new RemoteException("Error");
    }

    /**
     * Throw a fatal error.
     *
     * @throws FatalException The error thrown
     */
    public static void throwFatalError(String msg) throws FatalException {
        throw new FatalException(msg);
    }

    /**
     * Throw a timeout exception.
     *
     * @throws TimeoutException The error thrown
     */
    public static void throwTimeout() throws TimeoutException {
        throw new TimeoutException("Timeout");
    }
}
