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
package com.michelin.suricate.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.michelin.suricate.util.exception.ProjectTokenInvalidException;
import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

@ExtendWith(MockitoExtension.class)
class IdUtilsTest {
    @Mock
    private ApplicationContext applicationContext;

    @Test
    void shouldHandleExceptionWhenDecrypting() {
        try (MockedStatic<SpringContextUtils> mocked = mockStatic(SpringContextUtils.class)) {
            when(applicationContext.getBean(anyString())).thenReturn(new StringEncryptor() {
                @Override
                public String encrypt(String s) {
                    return "encrypted";
                }

                @Override
                public String decrypt(String s) {
                    return null;
                }
            });

            mocked.when(SpringContextUtils::getApplicationContext).thenReturn(applicationContext);

            ProjectTokenInvalidException exception =
                    assertThrows(ProjectTokenInvalidException.class, () -> IdUtils.decrypt(null));

            assertEquals("Cannot decrypt token : null", exception.getMessage());
        }
    }

    @Test
    void shouldDecrypt() {
        try (MockedStatic<SpringContextUtils> mocked = mockStatic(SpringContextUtils.class)) {
            when(applicationContext.getBean(anyString())).thenReturn(new StringEncryptor() {
                @Override
                public String encrypt(String s) {
                    return "encrypted";
                }

                @Override
                public String decrypt(String s) {
                    return "10";
                }
            });

            mocked.when(SpringContextUtils::getApplicationContext).thenReturn(applicationContext);

            Long actual = IdUtils.decrypt("token");

            assertEquals(10L, actual);
        }
    }

    @Test
    void shouldEncryptNull() {
        try (MockedStatic<SpringContextUtils> mocked = mockStatic(SpringContextUtils.class)) {
            when(applicationContext.getBean(anyString())).thenReturn(new StringEncryptor() {
                @Override
                public String encrypt(String s) {
                    return "encrypted";
                }

                @Override
                public String decrypt(String s) {
                    return "10";
                }
            });

            mocked.when(SpringContextUtils::getApplicationContext).thenReturn(applicationContext);

            String actual = IdUtils.encrypt(null);

            assertNull(actual);
        }
    }

    @Test
    void shouldEncrypt() {
        try (MockedStatic<SpringContextUtils> mocked = mockStatic(SpringContextUtils.class)) {
            when(applicationContext.getBean(anyString())).thenReturn(new StringEncryptor() {
                @Override
                public String encrypt(String s) {
                    return "encrypted";
                }

                @Override
                public String decrypt(String s) {
                    return "10";
                }
            });

            mocked.when(SpringContextUtils::getApplicationContext).thenReturn(applicationContext);

            String actual = IdUtils.encrypt(10L);

            assertEquals("encrypted", actual);
        }
    }

    @Test
    void shouldThrowExceptionWhenEncrypting() {
        try (MockedStatic<SpringContextUtils> mocked = mockStatic(SpringContextUtils.class)) {
            when(applicationContext.getBean(anyString())).thenReturn(new StringEncryptor() {
                @Override
                public String encrypt(String s) {
                    throw new RuntimeException("error");
                }

                @Override
                public String decrypt(String s) {
                    return "10";
                }
            });

            mocked.when(SpringContextUtils::getApplicationContext).thenReturn(applicationContext);

            String actual = IdUtils.encrypt(10L);

            assertNull(actual);
        }
    }
}
