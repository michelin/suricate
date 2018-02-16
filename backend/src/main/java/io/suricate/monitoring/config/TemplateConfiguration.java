package io.suricate.monitoring.config;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TemplateConfiguration {

    /**
     * Default mustache factory
     * @return
     */
    @Bean
    protected MustacheFactory mustacheFactory() {
        return new DefaultMustacheFactory();
    }
}
