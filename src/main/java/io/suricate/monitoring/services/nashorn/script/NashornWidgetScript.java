/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.suricate.monitoring.services.nashorn.script;

import io.suricate.monitoring.model.dto.nashorn.error.FatalError;
import io.suricate.monitoring.model.dto.nashorn.error.RemoteError;
import io.suricate.monitoring.model.dto.nashorn.error.RequestException;
import io.suricate.monitoring.utils.http.OkHttpClientUtils;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

public final class NashornWidgetScript {

    /**
     * Default httpClient
     */
    private static OkHttpClient client = OkHttpClientUtils.getUnsafeOkHttpClient();

    /**
     * private constructor
     */
    private NashornWidgetScript() { }

    /**
     * Create and submit a HTTP request according to the given parameters
     *
     * @param url The URL of the endpoint to call
     * @param headerName The name of the header to add
     * @param headerValue The value to set to the added header
     * @param returnedHeader The name of the header to return
     * @param body The body of the request. Can be null in case of GET HTTP request
     * @param mediaType The requested media type
     * @return The response body of the request or the value of the requested header
     * @throws IOException
     * @throws RemoteError
     * @throws RequestException
     */
    private static String executeRequest(String url, String headerName, String headerValue, String returnedHeader, String body, String mediaType)
            throws IOException, RemoteError, RequestException {
        Request.Builder builder = new Request.Builder().url(url);

        if (StringUtils.isNotBlank(headerName)) {
            builder.addHeader(headerName, headerValue);
        }

        if (StringUtils.isNotBlank(body)) {
            builder.post(RequestBody.create(body, MediaType.parse(mediaType)));
        }

        Request request = builder.build();
        String returnedValue = null;

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                if (StringUtils.isNotBlank(returnedHeader)) {
                    returnedValue = response.header(returnedHeader);
                } else {
                    returnedValue = Objects.requireNonNull(response.body()).string();
                }
            } else {
                if (response.code() >= HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                    throw new RemoteError("Response error: " + response.message() + " code:" + response.code());
                } else {
                    throw new RequestException(
                            response.message() + " - code:" + response.code(),
                            response.body() != null ? response.body().string() : null
                    );
                }
            }
        }

        return returnedValue;
    }

    /**
     * Perform a GET HTTP call
     * This method is directly called by the widgets
     *
     * @param url The URL of the endpoint to call
     * @return The response body of the request
     * @throws RemoteError
     * @throws IOException
     * @throws RequestException
     */
    public static String get(String url) throws RemoteError, IOException, RequestException {
        return NashornWidgetScript.executeRequest(url, null, null, null, null, "application/json");
    }

    /**
     * Perform a GET HTTP call
     * This method is directly called by the widgets
     *
     * Accept the name of a header as parameter and a value to set it to
     * add them to the request
     *
     * @param url The URL of the endpoint to call
     * @param headerName The name of the header to add
     * @param headerValue The value to set to the added header
     * @return The response body of the request
     * @throws RemoteError
     * @throws IOException
     * @throws RequestException
     */
    public static String get(String url, String headerName, String headerValue) throws RemoteError, IOException, RequestException {
        return NashornWidgetScript.executeRequest(url, headerName, headerValue, null, null, "application/json");
    }

    /**
     * Perform a GET HTTP call
     * This method is directly called by the widgets
     *
     * Accept the name of a header as parameter and a value to set it to
     * add them to the request
     *
     * Accept the name of a header as parameter to return its value
     *
     * @param url The URL of the endpoint to call
     * @param headerName The name of the header to add
     * @param headerValue The value to set to the added header
     * @param returnedHeader The name of the header to return
     * @return The requested header
     * @throws RemoteError
     * @throws IOException
     * @throws RequestException
     */
    public static String get(String url, String headerName, String headerValue, String returnedHeader) throws RemoteError, IOException, RequestException {
        return NashornWidgetScript.executeRequest(url, headerName, headerValue, returnedHeader, null, "application/json");
    }

    /**
     * Perform a POST HTTP call
     * This method is directly called by the widgets
     *
     * @param url The URL of the endpoint to call
     * @param body The body of the POST request
     * @return The response body of the request or the value of the requested header
     * @throws RemoteError
     * @throws IOException
     * @throws RequestException
     */
    public static String post(String url, String body) throws RemoteError, IOException, RequestException {
        return NashornWidgetScript.executeRequest(url, null, null, null, body == null ? "{}" : body, "application/json");
    }

    /**
     * Perform a POST HTTP call
     * This method is directly called by the widgets
     *
     * Accept the name of a header as parameter and a value to set it to
     * add them to the request
     *
     * @param url The URL of the endpoint to call
     * @param headerName The name of the header to add
     * @param headerValue The value to set to the added header
     * @param body The body of the POST request
     * @return The response body of the request or the value of the requested header
     * @throws RemoteError
     * @throws IOException
     * @throws RequestException
     */
    public static String post(String url, String headerName, String headerValue, String body) throws RemoteError, IOException, RequestException {
        return NashornWidgetScript.executeRequest(url, headerName, headerValue, null, body == null ? "{}" : body, "application/json");
    }

    /**
     * Perform a POST HTTP call
     * This method is directly called by the widgets
     *
     * Accept the name of a header as parameter and a value to set it to
     * add them to the request
     *
     * Accept the name of a header as parameter to return its value
     *
     * @param url The URL of the endpoint to call
     * @param headerName The name of the header to add
     * @param headerValue The value to set to the added header
     * @param returnedHeader The name of the header to return
     * @param body The body of the POST request
     * @return The response body of the request or the value of the requested header
     * @throws RemoteError
     * @throws IOException
     * @throws RequestException
     */
    public static String post(String url, String headerName, String headerValue, String returnedHeader, String body) throws RemoteError, IOException, RequestException {
        return NashornWidgetScript.executeRequest(url, headerName, headerValue, returnedHeader, body == null ? "{}" : body, "application/json");
    }

    /**
     * Check if a thread is interrupted
     * This method is injected during the Nashorn request preparation
     *
     * @throws InterruptedException an exception if the thread is interrupted
     */
    public static void checkInterrupted() throws InterruptedException {
        if (Thread.currentThread().isInterrupted()){
            throw new InterruptedException("Script Interrupted");
        }
    }

    /**
     * Convert ASCII string to base 64
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
     * Throw a remote error
     *
     * @throws RemoteError The error thrown
     */
    public static void throwError() throws RemoteError {
        throw new RemoteError("Error");
    }

    /**
     * Throw a fatal error
     *
     * @throws FatalError The error thrown
     */
    public static void throwFatalError(String msg) throws FatalError {
        throw new FatalError(msg);
    }

    /**
     * Throw a timeout exception
     *
     * @throws TimeoutException The error thrown
     */
    public static void throwTimeout() throws TimeoutException {
        throw new TimeoutException("Timeout");
    }
}
