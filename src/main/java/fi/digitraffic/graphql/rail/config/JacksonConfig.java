package fi.digitraffic.graphql.rail.config;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.cfg.DateTimeFeature;

@Configuration
public class JacksonConfig {

    @Bean
    public JsonMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> {
            builder.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);
            builder.changeDefaultPropertyInclusion(v -> JsonInclude.Value.construct(JsonInclude.Include.ALWAYS, JsonInclude.Include.ALWAYS));
            builder.configure(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        };
    }
}
