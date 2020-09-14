package fi.digitraffic.graphql.rail;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GraphqlApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Etc/UTC"));
        SpringApplication.run(GraphqlApplication.class, args);
    }
}
