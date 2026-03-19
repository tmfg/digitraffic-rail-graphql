package fi.digitraffic.graphql.rail.links;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.webmvc.BaseWebMVCTest;

public class RoutesetToRouteSectionsLinkTest extends BaseWebMVCTest {

    private static final LocalDate DATE = LocalDate.of(2024, 6, 1);

    @Test
    public void linkShouldWork() throws Exception {
        final var train = factoryService.getTrainFactory().createBaseTrain(1, DATE).getFirst();
        final var routeset = factoryService.getRoutesetMessageFactory().create(train);
        factoryService.getStationFactory().create("HKI", 1, "FI");
        factoryService.getRoutesectionFactory().create(routeset.id, "HKI", "001");
        factoryService.getRoutesectionFactory().create(routeset.id, "HKI", "002");

        final ResultActions result = this.query("""
                {
                    routesetMessagesByVersionGreaterThan(version: "0") {
                        id
                        routesections {
                            sectionId
                            station { shortCode }
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.errors").doesNotExist());
        result.andExpect(jsonPath("$.data.routesetMessagesByVersionGreaterThan[0].routesections.length()").value(2));
        result.andExpect(jsonPath("$.data.routesetMessagesByVersionGreaterThan[0].routesections[0].station.shortCode").value("HKI"));
    }

    @Test
    public void nullCommercialTrackIdShouldNotCauseInternalError() throws Exception {
        // Reproduces: NullValueInNonNullableField for commercialTrackId
        // The DB column commercial_track_id is DEFAULT NULL but was declared String! in the schema.
        final var train = factoryService.getTrainFactory().createBaseTrain(2, DATE).getFirst();
        final var routeset = factoryService.getRoutesetMessageFactory().create(train);
        factoryService.getStationFactory().create("HKI", 1, "FI");
        factoryService.getRoutesectionFactory().createWithNullCommercialTrackId(routeset.id, "HKI", "001");

        final ResultActions result = this.query("""
                {
                    routesetMessagesByVersionGreaterThan(version: "0") {
                        routesections {
                            sectionId
                            commercialTrackId
                            station { shortCode }
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.errors").doesNotExist());
        result.andExpect(jsonPath("$.data.routesetMessagesByVersionGreaterThan[0].routesections[0].commercialTrackId").value(nullValue()));
    }
}
