/*
 *  /*
 *  * Copyright 2012-2018 the original author or authors.
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

package io.suricate.monitoring.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.cors.CorsConfiguration;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Hold the custom properties from "properties.yml" files
 */
@Configuration
@PropertySource("classpath:application.yml")
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
@Getter
@Setter
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
     * The OAuth properties
     */
    public final OAuth oauth = new OAuth();

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
         * JWT Configuration
         */
        public final Jwt jwt = new Jwt();

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
         * LDAP Url
         */
        public String url;

        /**
         * User search filter
         */
        public String userSearchFilter;

        /**
         * LDAP FirstName attribut
         */
        public String firstNameAttributName;

        /**
         * LDAP LastName attribut
         */
        public String lastNameAttributName;

        /**
         * LDAP mail attribut
         */
        public String mailAttributName;
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
        public long tokenValidity;

        /**
         * Token validity in second remember me
         */
        @NotNull
        public long tokenValidityRememberMe;

        /**
         * Jwt secret
         */
        @NotNull
        public String secret;
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
         * Widgets configuration in local folder
         **/
        public final Local local = new Local();

        /**
         * Git widgets configuration
         */
        public final Git git = new Git();

        /**
         * Enable the the widget update (Local and Git)
         */
        @NotNull
        public boolean updateEnable;
    }

    /**
     * Hold the widgets Local properties info
     */
    @Getter
    @Setter
    public static class Local {
        /**
         * The Local folder path
         */
        public String folderPath;
    }

    /**
     * Hold the Widgets Git properties info
     */
    @Getter
    @Setter
    public static class Git {
        /**
         * The Git repository url
         */
        public String url;
        /**
         * The git branch to scan
         */
        public String branch;
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
     * Hold the OAuth properties info
     */
    @Getter
    @Setter
    public static class OAuth {
        /**
         * The client id
         */
        public String client;
        /**
         * The client secret
         */
        public String secret;

    }
}
