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

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class AllTrustingTrustManager implements X509TrustManager {

    /**
     * Do not check the trusted client
     *
     * @param x509Certificates
     * @param s
     */
    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
        // Do not check the trusted client
    }

    /**
     * Do not check certificates
     *
     * @param x509Certificates
     * @param s
     */
    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
        // Do not check certificates
    }

    /**
     * Do not get issuers
     *
     * @return An empty list of issuers
     */
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[]{};
    }
}
