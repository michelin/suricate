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

package io.suricate.monitoring;

import io.suricate.monitoring.configuration.ApplicationProperties;
import io.suricate.monitoring.configuration.web.ProxyConfiguration;
import io.suricate.monitoring.service.GitService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * Main java class
 */
@SpringBootApplication
@EnableAsync
@EnableCaching
@EnableConfigurationProperties({ApplicationProperties.class})
@EnableJpaRepositories
public class BackendApplication {

    /**
     * Application properties
     */
    private final ApplicationProperties applicationProperties;

    /**
     * Git Service
     */
    private final GitService gitService;

    /**
     * Proxy configuration
     */
    private final ProxyConfiguration proxyConfiguration;

    /**
     * The constructor
     *
     * @param applicationProperties application properties to inject
     * @param gitService            git service to inject
     */
    @Autowired
    public BackendApplication(final ApplicationProperties applicationProperties, final GitService gitService, final ProxyConfiguration proxyConfiguration) {
        this.applicationProperties = applicationProperties;
        this.gitService = gitService;
        this.proxyConfiguration = proxyConfiguration;
    }

    /**
     * Main springboot class
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    /**
     * Add trust store and key store to classpath
     */
    @PostConstruct
    protected void init() throws FileNotFoundException {
        // Define Trust Store properties
        if (StringUtils.isNotBlank(applicationProperties.ssl.trustStore.path)) {
            if (!new File(applicationProperties.ssl.trustStore.path).exists()) {
                throw new FileNotFoundException("Trust store not found under path : '" + applicationProperties.ssl.trustStore.path);
            }
            System.setProperty("javax.net.ssl.trustStore", applicationProperties.ssl.trustStore.path);
            System.setProperty("javax.net.ssl.trustStorePassword", applicationProperties.ssl.trustStore.password);

            if (StringUtils.isNotBlank(applicationProperties.ssl.trustStore.type)) {
                System.setProperty("javax.net.ssl.trustStoreType", applicationProperties.ssl.trustStore.type);
            }
        }

        //Define Key Store properties
        if (StringUtils.isNotBlank(applicationProperties.ssl.keyStore.path)) {
            if (!new File(applicationProperties.ssl.keyStore.path).exists()) {
                throw new FileNotFoundException("Key store not found under path : '" + applicationProperties.ssl.keyStore.path);
            }
            System.setProperty("javax.net.ssl.keyStore", applicationProperties.ssl.keyStore.path);
            System.setProperty("javax.net.ssl.keyStorePassword", applicationProperties.ssl.keyStore.password);

            if (StringUtils.isNotBlank(applicationProperties.ssl.keyStore.type)) {
                System.setProperty("javax.net.ssl.keyStoreType", applicationProperties.ssl.keyStore.type);
            }
        }

        // Set proxy
        proxyConfiguration.setProxy();

        // Update widgets
        gitService.updateWidgetFromEnabledGitRepositories();
    }
}
