package io.suricate.monitoring.configuration.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.PathSelectors.any;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket swaggerSpringMvcPlugin() {
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .apiInfo(apiInfo())
                .select()
                //.paths(Predicates.not(PathSelectors.regex("/error.*")))
                .paths(any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Suricate API")
                .description("Rest API for integrating with backend layer.")
                .contact(new Contact("name", "url", "email"))
                //.license("Apache License Version 2.0")
                //.licenseUrl("https://github.com/springfox/springfox/blob/master/LICENSE")
                //.version("2.0")
                .build();
    }
}