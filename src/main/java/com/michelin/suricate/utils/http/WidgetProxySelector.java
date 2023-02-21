/*
 *
 *  * Copyright 2012-2021 the original author or authors.
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

package com.michelin.suricate.utils.http;

import com.michelin.suricate.properties.ProxyProperties;
import com.michelin.suricate.utils.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class WidgetProxySelector extends ProxySelector {
    /**
     * Set the proxy for the URI that will be called by the widget HTTP client
     * @param uri The URI
     * @return A proxy
     */
    @Override
    public List<Proxy> select(URI uri) {
        Proxy proxy = Proxy.NO_PROXY;
        ProxyProperties proxyProperties = SpringContextUtils.getApplicationContext().getBean(ProxyProperties.class);

        if (StringUtils.isNotBlank(proxyProperties.getNonProxyHosts())) {
            try (Stream<String> domains = Arrays.stream(proxyProperties.getNonProxyHosts().split("\\|"))) {
                // Check if the URI is defined in the "no proxy domains" config before setting the proxy
                if (domains.noneMatch(domain -> StringUtils.containsIgnoreCase(uri.getHost(), domain.replace("*", StringUtils.EMPTY)))) {
                    proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyProperties.getHttpHost(), Integer.parseInt(proxyProperties.getHttpPort())));
                }
            }
        }

        return Collections.singletonList(proxy);
    }

    /**
     * Handle the connection fails to the given proxy
     *
     * @param uri The URI to call behind the proxy
     * @param socketAddress The socket address
     * @param e The exception
     */
    @Override
    public void connectFailed(URI uri, SocketAddress socketAddress, IOException e) {
        log.error("An error occurred trying to connect to the configured proxy for the URI {} during the widget execution", uri, e);
    }
}
