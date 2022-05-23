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
import java.util.List;

/**
 * Hold the custom properties from properties.yml files
 */
@Getter
@Setter
@Configuration
@PropertySource("classpath:application.properties")
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    /**
     * Cors properties
     */
    public final CorsConfiguration cors = new CorsConfiguration();

    /**
     * Authentication properties
     */
    public final Authentication authentication = new Authentication();

    /**
     * SSL properties
     */
    public final Ssl ssl = new Ssl();

    /**
     * Widgets properties
     */
    public final Widgets widgets = new Widgets();

    /**
     * The swagger properties
     */
    public final Swagger swagger = new Swagger();

    /**
     * Hold the Authentication properties info
     */
    @Getter
    @Setter
    public static class Authentication {
        /**
         * LDAP configuration
         */
        public final Ldap ldap = new Ldap();

        /**
         * JWT configuration
         */
        public final Jwt jwt = new Jwt();

        /**
         * OAuth2 configuration
         */
        public final OAuth2 oAuth2 = new OAuth2();

        /**
         * Authentication provider
         */
        @NotNull
        @Pattern(regexp = "ldap|database")
        public String provider;
    }

    /**
     * Hold the LDAP properties info
     */
    @Getter
    @Setter
    public static class Ldap {
        /**
         * The LDAP URL
         */
        public String url;
        /**
         * The filter to search user
         */
        public String userSearchFilter;
        /**
         * Attribute for user group role
         */
        public String groupRoleAttribute;
        /**
         * The group search base
         */
        public String groupSearchBase = StringUtils.EMPTY;
        /**
         * Filter to search group
         */
        public String groupSearchFilter;
        /**
         * Role prefix
         */
        public String rolePrefix = "ROLE_";
        /**
         * The user search base
         */
        public String userSearchBase = StringUtils.EMPTY;
        /**
         * The user  Distinguished Name patterns
         */
        public String userDnPatterns;
        /**
         * The username used to start the connection with the LDAP
         */
        public String username = StringUtils.EMPTY;
        /**
         * The password used to start the connection with the LDAP
         */
        public String password = StringUtils.EMPTY;
        /**
         * The LDAP attribute to retrieve the user firstname
         */
        public String firstNameAttributName;
        /**
         * The LDAP attribute to retrieve the user lastname
         */
        public String lastNameAttributName;
        /**
         * The LDAP attribute to retrieve the user mail
         */
        public String mailAttributName;
        /**
         * The Ldap referral (behavior when the LDAP search executor find a reference to another LDAP server)
         */
        public String referral = ReferralHandlingMode.IGNORE.getJndiValue();
    }

    /**
     * Hold the JWT properties info
     */
    @Getter
    @Setter
    public static class Jwt {

        /**
         * Token validity in second
         */
        @NotNull
        public long tokenValidityMs;

        /**
         * Jwt signing key
         */
        @NotNull
        public String signingKey;
    }

    /**
     * Hold the SSL properties info
     */
    @Getter
    @Setter
    public static class Ssl {
        /**
         * Key Store configuration
         */
        public final KeyStore keyStore = new KeyStore();

        /**
         * Trust store configuration
         */
        public final TrustStore trustStore = new TrustStore();
    }

    /**
     * Hold the KeyStore properties info
     */
    @Getter
    @Setter
    public static class KeyStore {
        /**
         * Key Store path
         */
        public String path;

        /**
         * Key Store password
         */
        public String password;

        /**
         * Key Store type
         */
        public String type;
    }

    /**
     * Hold the TrustStore properties info
     */
    @Getter
    @Setter
    public static class TrustStore {
        /**
         * Trust Store path
         */
        public String path;

        /**
         * Trust Store password
         */
        public String password;

        /**
         * Trust Store type
         */
        public String type;
    }

    /**
     * Hold the Widgets properties info
     */
    @Getter
    @Setter
    public static class Widgets {
        /**
         * Enable the widget update (Local and Git)
         */
        @NotNull
        public boolean updateEnable;
    }

    /**
     * Hold the swagger properties info
     */
    @Getter
    @Setter
    public static class Swagger {
        /**
         * The Swagger API Title
         */
        public String title;
        /**
         * The Swagger API Description
         */
        public String description;
        /**
         * The API Version
         */
        public String version;
        /**
         * The site url for terms of service
         */
        public String termsOfServiceUrl;
        /**
         * The licence name
         */
        public String license;
        /**
         * The licence URL
         */
        public String licenseUrl;
        /**
         * The API group name
         */
        public String groupName;
        /**
         * The list of protocols
         */
        public String protocols;
        /**
         * The default include pattern
         */
        public String defaultIncludePattern;
        /**
         * The swagger contact name
         */
        public String contactName;
        /**
         * The swagger contact url
         */
        public String contactUrl;
        /**
         * The swagger contact email
         */
        public String contactEmail;
    }

    /**
     * Hold the OAuth2 properties
     */
    @Getter
    @Setter
    public static class OAuth2 {
        /**
         * List of authorized uris this backend can redirect after successful OAuth2 authentication
         */
        public List<String> authorizedRedirectUris;
    }
}
