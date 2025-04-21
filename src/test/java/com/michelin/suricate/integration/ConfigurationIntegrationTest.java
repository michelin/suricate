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
package com.michelin.suricate.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("configuration-test")
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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
