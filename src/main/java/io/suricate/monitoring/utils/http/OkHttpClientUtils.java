/*
 *
 *  * Copyright 2012-2018 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package io.suricate.monitoring.utils.http;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public final class OkHttpClientUtils {

    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(OkHttpClientUtils.class);

    /**
     * Default timeout
     */
    private static final int DEFAULT_TIMEOUT = 45;

    /**
     * Connect timeout
     */
    private static final int CONNECT_TIMEOUT = 15;

    /**
     * Private constructor
     */
    private OkHttpClientUtils() { }

    /**
     * Get an instance of OkHttpClient without certificates validation
     *
     * @return An OkHttpClient instance
     */
    public static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificates chain
            final TrustManager[] trustManager = new TrustManager[] {
                    new AllTrustingTrustManager()
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustManager, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.level(HttpLoggingInterceptor.Level.BASIC);

            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustManager[0])
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .retryOnConnectionFailure(true)
                .proxySelector(new WidgetProxySelector())
                .connectionSpecs(Arrays.asList(ConnectionSpec.CLEARTEXT, ConnectionSpec.MODERN_TLS))
                .hostnameVerifier((s, sslSession) -> true);

            return builder.build();
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("An error occurred during the OKHttpClient configuration: SSL algorithm not found", e);
        } catch (KeyManagementException e) {
            LOGGER.error("An error occurred during the OKHttpClient configuration: Cannot init the SSL context", e);
        }

        return null;
    }
}
