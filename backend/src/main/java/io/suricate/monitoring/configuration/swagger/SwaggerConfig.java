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

package io.suricate.monitoring.configuration.swagger;

import com.google.common.base.Predicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket userApi() {
        Docket docket = createDocket("User", userPaths());
        addUserTags(docket);
        return docket;
    }

    @Bean
    public Docket assetApi() {
        Docket docket = createDocket("Asset", assetPaths());
        addAssetTags(docket);
        return docket;
    }

    @Bean
    public Docket projectsApi() {
        Docket docket = createDocket("Projects", projectsPaths());
        addProjectsTags(docket);
        return docket;
    }

    @Bean
    public Docket widgetsApi() {
        Docket docket = createDocket("Widgets", widgetsPaths());
        addWidgetsTags(docket);
        return docket;
    }

    @Bean
    public Docket configurationsApi() {
        Docket docket = createDocket("Configurations", configurationsPaths());
        addConfigurationsTags(docket);
        return docket;
    }


    private Docket createDocket(String name, Predicate<String> paths) {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName(name)
                .useDefaultResponseMessages(false)
                .apiInfo(apiInfo())
                .select()
                .paths(paths)
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Suricate API")
                .description("Rest API for integrating with backend layer.")
                .contact(new Contact("suricate", "https://github.com/suricate-io/suricate", "email@email.com"))
                .version("1.0")
                .build();
    }

    private Predicate<String> userPaths() {
        return or(regex("/api/users.*"));
    }

    private void addUserTags(Docket docket) {
        Tag userTag = new Tag("User", "User API");
        docket.tags(userTag, userTag);
    }

    private Predicate<String> assetPaths() {
        return or(regex("/api/asset.*"));
    }

    private void addAssetTags(Docket docket) {
        Tag userTag = new Tag("Asset", "Asset API");
        docket.tags(userTag, userTag);
    }

    private Predicate<String> projectsPaths() {
        return or(regex("/api/projects.*"));
    }

    private void addProjectsTags(Docket docket) {
        Tag userTag = new Tag("Projects", "Projects API");
        docket.tags(userTag, userTag);
    }

    private Predicate<String> widgetsPaths() {
        return or(regex("/api/widgets.*"));
    }

    private void addWidgetsTags(Docket docket) {
        Tag userTag = new Tag("Widgets", "Widgets API");
        docket.tags(userTag, userTag);
    }

    private Predicate<String> configurationsPaths() {
        return or(regex("/api/configurations.*"));
    }

    private void addConfigurationsTags(Docket docket) {
        Tag userTag = new Tag("Configurations", "Configurations API");
        docket.tags(userTag, userTag);
    }
}