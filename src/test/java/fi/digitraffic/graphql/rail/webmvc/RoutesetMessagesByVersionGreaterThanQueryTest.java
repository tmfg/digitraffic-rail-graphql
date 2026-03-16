package fi.digitraffic.graphql.rail.webmvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

public class RoutesetMessagesByVersionGreaterThanQueryTest extends BaseWebMVCTest {

    private static final LocalDate DATE = LocalDate.of(2024, 6, 1);

    @Test
    public void returnsMatchingMessages() throws Exception {
        final var train = factoryService.getTrainFactory().createBaseTrain(1, DATE).getFirst();
        factoryService.getRoutesetMessageFactory().create(train);

        final ResultActions result = this.query("""
                {
                    routesetMessagesByVersionGreaterThan(version: "0") {
                        id
                        version
                    }
                }""");

        result.andExpect(jsonPath("$.data.routesetMessagesByVersionGreaterThan.length()").value(1));
    }

    @Test
    public void returnsEmptyWhenNoneMatch() throws Exception {
        final var train = factoryService.getTrainFactory().createBaseTrain(1, DATE).getFirst();
        factoryService.getRoutesetMessageFactory().create(train);

        final ResultActions result = this.query("""
                {
                    routesetMessagesByVersionGreaterThan(version: "9999") {
                        id
                    }
                }""");

        result.andExpect(jsonPath("$.data.routesetMessagesByVersionGreaterThan.length()").value(0));
    }
}
