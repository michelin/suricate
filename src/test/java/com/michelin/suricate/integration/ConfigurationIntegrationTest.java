package com.michelin.suricate.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("configuration-test")
@SpringBootTest
class ConfigurationIntegrationTest {

    @AfterEach
    void tearDown() {
        System.clearProperty("javax.net.ssl.trustStore");
        System.clearProperty("javax.net.ssl.trustStorePassword");
        System.clearProperty("javax.net.ssl.trustStoreType");

        System.clearProperty("javax.net.ssl.keyStore");
        System.clearProperty("javax.net.ssl.keyStorePassword");
        System.clearProperty("javax.net.ssl.keyStoreType");

        System.clearProperty("http.proxyHost");
        System.clearProperty("http.proxyPort");

        System.clearProperty("https.proxyHost");
        System.clearProperty("https.proxyPort");

        System.clearProperty("http.nonProxyHosts");
    }

    @Test
    void shouldSetKeyStoreAndTrustStoreSystemProperties() {
        assertEquals("src/test/resources/fake-store", System.getProperty("javax.net.ssl.trustStore"));
        assertEquals("trustStorePassword", System.getProperty("javax.net.ssl.trustStorePassword"));
        assertEquals("trustStoreType", System.getProperty("javax.net.ssl.trustStoreType"));

        assertEquals("src/test/resources/fake-store", System.getProperty("javax.net.ssl.keyStore"));
        assertEquals("keyStorePassword", System.getProperty("javax.net.ssl.keyStorePassword"));
        assertEquals("keyStoreType", System.getProperty("javax.net.ssl.keyStoreType"));

        assertEquals("httpHost", System.getProperty("http.proxyHost"));
        assertEquals("8080", System.getProperty("http.proxyPort"));

        assertEquals("httpsHost", System.getProperty("https.proxyHost"));
        assertEquals("443", System.getProperty("https.proxyPort"));

        assertEquals("nonProxyHosts", System.getProperty("http.nonProxyHosts"));
    }
}
