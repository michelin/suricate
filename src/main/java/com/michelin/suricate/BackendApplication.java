/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.michelin.suricate;

import com.michelin.suricate.property.ApplicationProperties;
import com.michelin.suricate.property.ProxyProperties;
import com.michelin.suricate.service.git.GitService;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Spring Boot application class.
 */
@EnableAsync
@EnableCaching
@EnableJpaRepositories
@SpringBootApplication
@EnableConfigurationProperties(ApplicationProperties.class)
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class BackendApplication {
    static {
        Locale.setDefault(Locale.ENGLISH);
    }

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private GitService gitService;

    @Autowired
    private ProxyProperties proxyConfiguration;

    /**
     * Main Spring Boot class.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    /**
     * Add trust store and key store to classpath.
     */
    @PostConstruct
    protected void init() throws FileNotFoundException {
        // Define Trust Store properties
        if (StringUtils.isNotBlank(applicationProperties.getSsl().getTrustStore().getPath())) {
            if (!new File(applicationProperties.getSsl().getTrustStore().getPath()).exists()) {
                throw new FileNotFoundException(
                    "Trust store not found under path : '" + applicationProperties.getSsl().getTrustStore().getPath());
            }
            System.setProperty("javax.net.ssl.trustStore", applicationProperties.getSsl().getTrustStore().getPath());
            System.setProperty("javax.net.ssl.trustStorePassword",
                applicationProperties.getSsl().getTrustStore().getPassword());

            if (StringUtils.isNotBlank(applicationProperties.getSsl().getTrustStore().getType())) {
                System.setProperty("javax.net.ssl.trustStoreType",
                    applicationProperties.getSsl().getTrustStore().getType());
            }
        }

        //Define Key Store properties
        if (StringUtils.isNotBlank(applicationProperties.getSsl().getKeyStore().getPath())) {
            if (!new File(applicationProperties.getSsl().getKeyStore().getPath()).exists()) {
                throw new FileNotFoundException(
                    "Key store not found under path : '" + applicationProperties.getSsl().getKeyStore().getPath());
            }
            System.setProperty("javax.net.ssl.keyStore", applicationProperties.getSsl().getKeyStore().getPath());
            System.setProperty("javax.net.ssl.keyStorePassword",
                applicationProperties.getSsl().getKeyStore().getPassword());

            if (StringUtils.isNotBlank(applicationProperties.getSsl().getKeyStore().getType())) {
                System.setProperty("javax.net.ssl.keyStoreType",
                    applicationProperties.getSsl().getKeyStore().getType());
            }
        }

        // Set proxy
        proxyConfiguration.setProxy();

        // Update widgets
        gitService.updateWidgetFromEnabledGitRepositoriesAsync();
    }
}
