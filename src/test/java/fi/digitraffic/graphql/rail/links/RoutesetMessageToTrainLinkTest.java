package fi.digitraffic.graphql.rail.links;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.webmvc.BaseWebMVCTest;

public class RoutesetMessageToTrainLinkTest extends BaseWebMVCTest {

    private static final LocalDate DATE = LocalDate.of(2024, 6, 1);

    @Test
    public void trainLinkShouldWork() throws Exception {
        final var train = factoryService.getTrainFactory().createBaseTrain(1, DATE).getFirst();
        factoryService.getRoutesetMessageFactory().create(train);

        final ResultActions result = this.query("""
                {
                    routesetMessagesByVersionGreaterThan(version: "0") {
                        train { trainNumber }
                    }
                }""");

        result.andExpect(jsonPath("$.errors").doesNotExist());
        result.andExpect(jsonPath("$.data.routesetMessagesByVersionGreaterThan[0].train.trainNumber").value(1));
    }

    @Test
    public void nonNumericTrainNumberShouldNotCauseInternalError() throws Exception {
        // Reproduces: DataException: Cannot determine value type from string 'F29657'
        // The @ManyToOne Train association on RoutesetMessage uses referencedColumnName = "trainNumber"
        // which is Long in Train. When trainNumber is a non-numeric string, Hibernate tries to
        // read it as Long and throws DataConversionException.
        factoryService.getRoutesetMessageFactory().createWithStringTrainNumber("F29657", DATE);

        final ResultActions result = this.query("""
                {
                    routesetMessagesByVersionGreaterThan(version: "0") {
                        train { trainNumber }
                    }
                }""");

        result.andExpect(jsonPath("$.errors").doesNotExist());
    }
}

