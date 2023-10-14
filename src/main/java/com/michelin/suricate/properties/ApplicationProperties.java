/*
 *  /*
 *  * Copyright 2012-2021 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 *
 */

package com.michelin.suricate.properties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.cors.CorsConfiguration;

/**
 * Application properties.
 */
@Getter
@Setter
@Configuration
@PropertySource("classpath:application.properties")
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {
    private CorsConfiguration cors;
    private Authentication authentication;
    private Ssl ssl;
    private Widgets widgets;
    private Swagger swagger;

    /**
     * Authentication properties.
     */
    @Getter
    @Setter
    public static class Authentication {
        private Ldap ldap;
        private Jwt jwt;
        private PersonalAccessToken pat;
        private Oauth2 oauth2;
        @Pattern(regexp = "ldap|database")
        private String provider;
        private List<String> socialProviders;
        private Map<String, SocialProvidersConfig> socialProvidersConfig = new HashMap<>();
    }

    /**
     * LDAP properties.
     */
    @Getter
    @Setter
    public static class Ldap {
        private String url;
        private String userSearchFilter;
        private String userSearchBase = StringUtils.EMPTY;
        private String userDnPatterns = StringUtils.EMPTY;
        private String username = StringUtils.EMPTY;
        private String password = StringUtils.EMPTY;
        private String firstNameAttributeName;
        private String lastNameAttributeName;
        private String mailAttributeName;
    }

    /**
     * JWT properties.
     */
    @Getter
    @Setter
    public static class Jwt {
        @NotNull
        private long tokenValidityMs;

        @NotNull
        private String signingKey;
    }

    /**
     * Personal Access Token properties.
     */
    @Getter
    @Setter
    public static class PersonalAccessToken {
        @NotNull
        private String prefix;

        @NotNull
        private String checksumSecret;
    }

    /**
     * OAuth2 properties.
     */
    @Getter
    @Setter
    public static class Oauth2 {
        private String defaultTargetUrl;
        private boolean useReferer;
    }

    /**
     * SSL properties.
     */
    @Getter
    @Setter
    public static class Ssl {
        private KeyStore keyStore;
        private TrustStore trustStore;
    }

    /**
     * KeyStore properties.
     */
    @Getter
    @Setter
    public static class KeyStore {
        private String path;
        private String password;
        private String type;
    }

    /**
     * TrustStore properties.
     */
    @Getter
    @Setter
    public static class TrustStore {
        private String path;
        private String password;
        private String type;
    }

    /**
     * Widgets properties.
     */
    @Getter
    @Setter
    public static class Widgets {
        @NotNull
        private boolean updateEnable;
        private String cloneDir = "/tmp";
    }

    /**
     * Swagger properties.
     */
    @Getter
    @Setter
    public static class Swagger {
        private String title;
        private String description;
        private String version;
        private String termsOfServiceUrl;
        private String license;
        private String licenseUrl;
        private String groupName;
        private String protocols;
        private String contactName;
        private String contactUrl;
        private String contactEmail;
    }

    /**
     * Social providers properties.
     */
    @Getter
    @Setter
    public static class SocialProvidersConfig {
        private boolean nameCaseParse;
    }
}
