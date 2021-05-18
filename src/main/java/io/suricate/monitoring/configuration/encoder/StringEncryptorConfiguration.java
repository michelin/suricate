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

package io.suricate.monitoring.configuration.encoder;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * String encryptor configuration
 * Mainly used to encrypt and decrypt secret information for widgets
 */
@Configuration
@EnableEncryptableProperties
public class StringEncryptorConfiguration {

    /**
     * String encryptor password
     */
    @Value("${jasypt.encryptor.password}")
    private String encryptorPassword;

    /**
     * Configure the default string encryptor without salt
     *
     * @return The default encryptor
     */
    @Bean(name = "noSaltEncryptor")
    public StringEncryptor stringEncryptor() {
        return getPooledPBEStringEncryptor(encryptorPassword, "org.jasypt.salt.ZeroSaltGenerator");
    }

    /**
     * Default string encryptor
     *
     * @return The string encryptor
     */
    @Bean("jasyptStringEncryptor")
    public StringEncryptor defaultStringEncryptor() {
        return getPooledPBEStringEncryptor(encryptorPassword, "org.jasypt.salt.RandomSaltGenerator");
    }

    /**
     * Method used to create a String encryptor
     *
     * @param encryptorPassword      encryptor password
     * @param saltGeneratorClassName salt class name
     * @return The encryptor
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
