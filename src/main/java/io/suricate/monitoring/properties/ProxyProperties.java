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

package io.suricate.monitoring.properties;

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
     * Proxy host
     */
    private String host;

    /**
     * Proxy port
     */
    private String port;

    /**
     * List of all proxy domain to ignore
     */
    private String noProxyDomains;

    /**
     * Set JVM settings for http proxy
     */
    public void setProxy() {
        if (!StringUtils.isAllEmpty(host, port) && StringUtils.isNumeric(port)) {
            System.setProperty("http.proxyHost", host);
            System.setProperty("http.proxyPort", port);

            if (!StringUtils.isAllEmpty(noProxyDomains)) {
                System.setProperty("http.nonProxyHosts", noProxyDomains);
            }
        }
    }
}
