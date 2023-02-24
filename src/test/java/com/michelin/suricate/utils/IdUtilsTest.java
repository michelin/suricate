package com.michelin.suricate.utils;

import com.michelin.suricate.utils.exceptions.ProjectTokenInvalidException;
import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdUtilsTest {
    @Mock
    private ApplicationContext applicationContext;

    @Test
    void shouldHandleExceptionWhenDecrypting() {
        try (MockedStatic<SpringContextUtils> mocked = mockStatic(SpringContextUtils.class)) {
            when(applicationContext.getBean(anyString()))
                    .thenReturn(new StringEncryptor() {
                        @Override
                        public String encrypt(String s) {
                            return "encrypted";
                        }

                        @Override
                        public String decrypt(String s) {
                            return null;
                        }
                    });

            mocked.when(SpringContextUtils::getApplicationContext)
                    .thenReturn(applicationContext);

            assertThatThrownBy(() -> IdUtils.decrypt(null))
                    .isInstanceOf(ProjectTokenInvalidException.class)
                    .hasMessage("Cannot decrypt token : null");
        }
    }

    @Test
    void shouldDecrypt() {
        try (MockedStatic<SpringContextUtils> mocked = mockStatic(SpringContextUtils.class)) {
            when(applicationContext.getBean(anyString()))
                    .thenReturn(new StringEncryptor() {
                        @Override
                        public String encrypt(String s) {
                            return "encrypted";
                        }

                        @Override
                        public String decrypt(String s) {
                            return "10";
                        }
                    });

            mocked.when(SpringContextUtils::getApplicationContext)
                    .thenReturn(applicationContext);

            Long actual = IdUtils.decrypt("token");

            assertThat(actual).isEqualTo(10L);
        }
    }

    @Test
    void shouldEncryptNull() {
        try (MockedStatic<SpringContextUtils> mocked = mockStatic(SpringContextUtils.class)) {
            when(applicationContext.getBean(anyString()))
                    .thenReturn(new StringEncryptor() {
                        @Override
                        public String encrypt(String s) {
                            return "encrypted";
                        }

                        @Override
                        public String decrypt(String s) {
                            return "10";
                        }
                    });

            mocked.when(SpringContextUtils::getApplicationContext)
                    .thenReturn(applicationContext);

            String actual = IdUtils.encrypt(null);

            assertThat(actual).isNull();
        }
    }

    @Test
    void shouldEncrypt() {
        try (MockedStatic<SpringContextUtils> mocked = mockStatic(SpringContextUtils.class)) {
            when(applicationContext.getBean(anyString()))
                    .thenReturn(new StringEncryptor() {
                        @Override
                        public String encrypt(String s) {
                            return "encrypted";
                        }

                        @Override
                        public String decrypt(String s) {
                            return "10";
                        }
                    });

            mocked.when(SpringContextUtils::getApplicationContext)
                    .thenReturn(applicationContext);

            String actual = IdUtils.encrypt(10L);

            assertThat(actual).isEqualTo("encrypted");
        }
    }

    @Test
    void shouldThrowExceptionWhenEncrypting() {
        try (MockedStatic<SpringContextUtils> mocked = mockStatic(SpringContextUtils.class)) {
            when(applicationContext.getBean(anyString()))
                    .thenReturn(new StringEncryptor() {
                        @Override
                        public String encrypt(String s) {
                            throw new RuntimeException("error");
                        }

                        @Override
                        public String decrypt(String s) {
                            return "10";
                        }
                    });

            mocked.when(SpringContextUtils::getApplicationContext)
                    .thenReturn(applicationContext);

            String actual = IdUtils.encrypt(10L);

            assertThat(actual).isNull();
        }
    }
}
