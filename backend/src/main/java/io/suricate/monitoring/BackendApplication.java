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

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;
import io.suricate.monitoring.config.ApplicationProperties;
import io.suricate.monitoring.service.GitService;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.File;
import java.io.FileNotFoundException;

@SpringBootApplication
@EnableJpaAuditing
@EnableJpaRepositories
@EnableEncryptableProperties
@EnableAsync
@EnableCaching
@EnableConfigurationProperties({ApplicationProperties.class})
public class BackendApplication {

    @Value("${app.trust-store:}")
    private String trustStore;

    @Value("${app.trust-store-password}")
    private String trustStoreKey;

    @Value("${jasypt.encryptor.password}")
    private String encryptorPassword;

    @Autowired
    private GitService gitService;

    /**
     * Main springboot class
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);  // NOSONAR
    }

    /**
     * Application Role hierarchy for security management
     * @return
     */
    @Bean
    protected RoleHierarchyImpl roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
        return roleHierarchy;
    }

    /**
     * Default mustache factory
     * @return
     */
    @Bean
    protected MustacheFactory mustacheFactory() {
        return new DefaultMustacheFactory();
    }

    /**
     * Add trust store to classpath
     */
    @PostConstruct
    protected void init() throws FileNotFoundException {
        // Define trust store properties
        if(StringUtils.isNotBlank(trustStore)) {
            if (!new File(trustStore).exists()) { // NOSONAR
                throw new FileNotFoundException("Trust store not found in '" + trustStore + "'. Edit the config 'app.trust-store'");
            }
            System.setProperty("javax.net.ssl.trustStore", trustStore);
            System.setProperty("javax.net.ssl.trustStorePassword", trustStoreKey);
        }

        // Update widgets
        gitService.updateWidgetFromGit();
    }

    /**
     * Method used to configure the default string encryptor without salt
     * @return the default encryptor
     */
    @Bean(name = "noSaltEncrypter")
    public StringEncryptor stringEncryptor() {
        return getPooledPBEStringEncryptor(encryptorPassword, "org.jasypt.salt.ZeroSaltGenerator");
    }

    /**
     * Default string encryptor
     * @return the string encryptor
     */
    @Bean("jasyptStringEncryptor")
    public StringEncryptor defaultStringEncryptor() {
        return getPooledPBEStringEncryptor(encryptorPassword, "org.jasypt.salt.RandomSaltGenerator");
    }

    /**
     * Method used to create a String encryptor
     * @param encryptorPassword encryptor password
     * @param saltGeneratorClassName salt class name
     * @return the encryptor
     */
    private static PooledPBEStringEncryptor getPooledPBEStringEncryptor(String encryptorPassword, String saltGeneratorClassName) {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(encryptorPassword);
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName(saltGeneratorClassName);
        config.setStringOutputType("hexadecimal");
        encryptor.setConfig(config);
        return encryptor;
    }
}
