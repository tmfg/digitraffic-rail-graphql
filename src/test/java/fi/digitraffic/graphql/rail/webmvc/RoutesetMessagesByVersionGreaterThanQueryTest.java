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

    @Test
    public void fullRoutesetStructureIsReadable() throws Exception {
        // Exercises the full chain in a single query: routesetMessage → train, routesections → station.
        // trainNumber, departureDate on RoutesetMessage and stationCode, routesetId on Routesection
        // are hidden fields and cannot be queried.
        final var train = factoryService.getTrainFactory().createBaseTrain(1, DATE).getFirst();
        final var routeset = factoryService.getRoutesetMessageFactory().create(train);
        factoryService.getStationFactory().create("HKI", 1, "FI");
        factoryService.getRoutesectionFactory().create(routeset.id, "HKI", "001");

        final ResultActions result = this.query("""
                {
                    routesetMessagesByVersionGreaterThan(version: "0") {
                        id
                        version
                        messageTime
                        routeType
                        clientSystem
                        train { trainNumber cancelled }
                        routesections {
                            sectionId
                            commercialTrackId
                            station { shortCode name }
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.errors").doesNotExist());
        result.andExpect(jsonPath("$.data.routesetMessagesByVersionGreaterThan[0].train.trainNumber").value(1));
        result.andExpect(jsonPath("$.data.routesetMessagesByVersionGreaterThan[0].routesections[0].sectionId").value("001"));
        result.andExpect(jsonPath("$.data.routesetMessagesByVersionGreaterThan[0].routesections[0].commercialTrackId").value("1"));
        result.andExpect(jsonPath("$.data.routesetMessagesByVersionGreaterThan[0].routesections[0].station.shortCode").value("HKI"));
    }
}
