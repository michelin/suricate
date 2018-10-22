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

package io.suricate.monitoring.configuration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.List;

/**
 * Bean used to manage proxy configuration
 */
@Configuration
@ConfigurationProperties(prefix = "proxy")
@Getter @Setter @NoArgsConstructor
public class ProxyConfiguration {

    /**
     * Proxy host
     */
    private String host;

    /**
     * Proxy port
     */
    private int port;

    /**
     * List of all proxy domain to ignore
     */
    private String noProxyDomains;

    public void setProxy() {
        if (!StringUtils.isAllEmpty(host, Integer.toString(port))) {
            ProxySelector.setDefault(new ProxySelector() {
                final ProxySelector delegate = ProxySelector.getDefault();

                @Override
                public List<Proxy> select(URI uri) {
                    return Arrays.asList(new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(host, port)));
                }

                @Override
                public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
                    if (uri == null || sa == null || ioe == null) {
                        throw new IllegalArgumentException("Arguments can't be null.");
                    }
                }
            });
        }
    }
}
