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

package io.suricate.monitoring.properties;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.directory.server.core.api.ReferralHandlingMode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.cors.CorsConfiguration;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Getter
    @Setter
    public static class Authentication {
        private Ldap ldap;
        private Jwt jwt;
        private PersonalAccessToken pat;
        private OAuth2 oauth2;
        @Pattern(regexp = "ldap|database")
        private String provider;
        private List<String> socialProviders;
        private Map<String, SocialProvidersConfig> socialProvidersConfig = new HashMap<>();
    }

    @Getter
    @Setter
    public static class Ldap {
        private String url;
        private String userSearchFilter;
        private String groupRoleAttribute;
        private String groupSearchBase = StringUtils.EMPTY;
        private String groupSearchFilter;
        private String rolePrefix = "ROLE_";
        private String userSearchBase = StringUtils.EMPTY;
        private String userDnPatterns;
        private String username = StringUtils.EMPTY;
        private String password = StringUtils.EMPTY;
        private String firstNameAttributName;
        private String lastNameAttributName;
        private String mailAttributName;
        private String referral = ReferralHandlingMode.IGNORE.getJndiValue();
    }

    @Getter
    @Setter
    public static class Jwt {
        @NotNull
        private long tokenValidityMs;

        @NotNull
        private String signingKey;
    }

    @Getter
    @Setter
    public static class PersonalAccessToken {
        @NotNull
        private String prefix;

        @NotNull
        private String checksumSecret;
    }

    @Getter
    @Setter
    public static class OAuth2 {
        private String defaultTargetUrl;
        private boolean useReferer;
    }

    @Getter
    @Setter
    public static class Ssl {
        private KeyStore keyStore;
        private TrustStore trustStore;
    }

    @Getter
    @Setter
    public static class KeyStore {
        private String path;
        private String password;
        private String type;
    }

    @Getter
    @Setter
    public static class TrustStore {
        private String path;
        private String password;
        private String type;
    }

    @Getter
    @Setter
    public static class Widgets {
        @NotNull
        private boolean updateEnable;
    }

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

    @Getter
    @Setter
    public static class SocialProvidersConfig {
        private boolean nameCaseParse;
    }
}
