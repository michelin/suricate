package io.suricate.monitoring.configuration;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableEncryptableProperties
public class StringEncryptorConfiguration {

    @Value("${jasypt.encryptor.password}")
    private String encryptorPassword;

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
