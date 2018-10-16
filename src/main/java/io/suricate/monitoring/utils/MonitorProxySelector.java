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

package io.suricate.monitoring.utils;

import io.suricate.monitoring.configuration.ProxyConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Custom proxy selector for Monitoring application
 */
public class MonitorProxySelector extends ProxySelector {

    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MonitorProxySelector.class);

    @Override
    public List<Proxy> select(URI uri) {
        Proxy ret = Proxy.NO_PROXY;
        ProxyConfiguration config = SpringContextHolder.getApplicationContext().getBean(ProxyConfiguration.class);
        if(StringUtils.isNotBlank(config.getNoProxyDomains())) {
            try (Stream<String> stream = Arrays.stream(config.getNoProxyDomains().split(","))) {
                if ( StringUtils.isNotBlank(config.getNoProxyDomains()) &&
                     stream.noneMatch(h -> StringUtils.containsIgnoreCase(uri.getHost(), h)) ) {
                    ret = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(config.getHost(), config.getPort()));
                }
            }
        }
        return Collections.singletonList(ret);
    }

    @Override
    public void connectFailed(URI uri, SocketAddress socketAddress, IOException e) {
        LOGGER.error(e.getMessage(), e);
    }
}