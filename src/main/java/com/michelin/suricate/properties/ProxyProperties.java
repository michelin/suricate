/*
 * Copyright 2012-2021 the original author or authors.
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

package com.michelin.suricate.properties;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Manage the proxy configuration
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "proxy")
public class ProxyProperties {
    /**
     * Proxy http host
     */
    private String httpHost;

    /**
     * Proxy http port
     */
    private String httpPort;

    /**
     * Proxy https host
     */
    private String httpsHost;

    /**
     * Proxy https port
     */
    private String httpsPort;

    /**
     * List of all proxy domain to ignore
     */
    private String nonProxyHosts;

    /**
     * Set JVM settings for http proxy
     */
    public void setProxy() {
        if (!StringUtils.isAllEmpty(httpHost, httpPort) && StringUtils.isNumeric(httpPort)) {
            System.setProperty("http.proxyHost", httpHost);
            System.setProperty("http.proxyPort", httpPort);
        }

        if (!StringUtils.isAllEmpty(httpsHost, httpsPort) && StringUtils.isNumeric(httpsPort)) {
            System.setProperty("https.proxyHost", httpsHost);
            System.setProperty("https.proxyPort", httpsPort);
        }

        if (!StringUtils.isAllEmpty(nonProxyHosts)) {
            System.setProperty("http.nonProxyHosts", nonProxyHosts);
        }
    }
}
