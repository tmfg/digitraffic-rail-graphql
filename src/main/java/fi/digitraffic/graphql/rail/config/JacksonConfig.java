package fi.digitraffic.graphql.rail.config;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.ext.javatime.ser.ZonedDateTimeSerializer;
import tools.jackson.databind.module.SimpleModule;

@Configuration
public class JacksonConfig {
    public static final DateTimeFormatter ISO_FIXED_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(
            ZoneId.of("Z"));

    @Bean
    public JsonMapperBuilderCustomizer jacksonCustomizer() {
        final SimpleModule dateModule = new SimpleModule();
        dateModule.addSerializer(ZonedDateTime.class, new ZonedDateTimeSerializer(ISO_FIXED_FORMAT));

        return builder -> {
            builder.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);
            builder.changeDefaultPropertyInclusion(v -> JsonInclude.Value.construct(JsonInclude.Include.ALWAYS, JsonInclude.Include.ALWAYS));
            builder.configure(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            builder.addModule(dateModule);
        };
    }
}
