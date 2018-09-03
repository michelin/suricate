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

package io.suricate.monitoring.service.nashorn.script;

import io.suricate.monitoring.model.dto.error.FatalError;
import io.suricate.monitoring.model.dto.error.RemoteError;
import io.suricate.monitoring.model.dto.error.RequestException;
import io.suricate.monitoring.utils.OkHttpClientUtils;
import okhttp3.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.TimeoutException;

public final class Methods {

    /**
     * Default httpClient
     */
    private static OkHttpClient client = OkHttpClientUtils.getUnsafeOkHttpClient();

    /**
     * Method used to call a webservice
     * @param url the url to call
     * @param headerName the header name to add
     * @param headerValue the header value to add
     * @param returnHeader can be null, if it was not null this method return the header value
     * @param body used to send body
     * @return the response body of the request or the header value if returnHeader is defined
     */
    private static String callRaw(String url, String headerName, String headerValue, String returnHeader, String body) throws Exception{

        Request.Builder builder = new Request.Builder().url(url);
        if (StringUtils.isNotBlank(headerName)) {
            builder.addHeader(headerName, headerValue);
        }
        if (StringUtils.isNotBlank(body)){
            builder.post(RequestBody.create(MediaType.parse("application/json"), body));
        }
        Request request = builder.build();
        Response response = null;
        String ret = null;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful()){
                if (StringUtils.isNotBlank(returnHeader)){
                    ret = response.header(returnHeader);
                } else {
                    ret = response.body().string();
                }
            } else {
                if (response.code() >= HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                    throw new RemoteError("Response error: " + response.message()+" code:"+response.code());
                } else {
                    throw new RequestException(response.message() + " - code:" + response.code() , response.body() != null ? response.body().string() : null);
                }
            }
        } finally {
            IOUtils.closeQuietly(response);
            IOUtils.closeQuietly();
        }
        return ret;
    }

    /**
     * Method used to call a webservice
     * @param url the url to call
     * @param headerName the header name to add
     * @param headerValue the header value to add
     * @param returnHeader can be null, if it was not null this method return the header value
     * @return the response body of the request or the header value if returnHeader is defined
     */
    public static String call(String url, String headerName, String headerValue, String returnHeader) throws Exception{
        return callRaw(url, headerName, headerValue, returnHeader, null);
    }

    /**
     * Method used to call a webservice
     * @param url the url to call
     * @param headerName the header name to add
     * @param headerValue the header value to add
     * @param body used to send body
     * @return the response body of the request or the header value if returnHeader is defined
     */
    public static String callWithHeaderBody(String url, String headerName, String headerValue, String body) throws Exception{
        return callRaw(url, headerName, headerValue, null, body);
    }


    /**
     * Method used to call a webservice
     * @param url the url to call
     * @param body can be null, if it was not null this method return the header value
     * @return the response body of the request or the header value if returnHeader is defined
     */
    public static String callWithBody(String url, String body) throws Exception{
        return callRaw(url, null, null, null, body);
    }


    /**
     * Method used to isValid is a thread is interrupted
     * @throws InterruptedException an exception if the thread is interrupted
     */
    public static void checkInterupted() throws InterruptedException {
        if (Thread.currentThread().isInterrupted()){
            throw new InterruptedException("Script Interrupted");
        }
    }


    /**
     * Method used to convert ASCII string to base 64
     * @param data string to convert
     * @return Base64 string
     */
    public static String btoa(String data){
        if (StringUtils.isBlank(data)){
            return null;
        }
        return Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Method used to throw new error
     * @throws RemoteError error throws
     */
    public static void throwError() throws RemoteError {
        throw new RemoteError("Error");
    }

    /**
     * Method used to throw new fatal error
     * @throws FatalError the exception throw
     */
    public static void throwFatalError(String msg) throws FatalError {
        throw new FatalError(msg);
    }

    /**
     * Method used to throw new timeout exception
     * @throws TimeoutException timeout exception
     */
    public static void throwTimeout() throws TimeoutException {
        throw new TimeoutException("Timeout");
    }

    /**
     * private constructor
     */
    private Methods() {
    }
}
