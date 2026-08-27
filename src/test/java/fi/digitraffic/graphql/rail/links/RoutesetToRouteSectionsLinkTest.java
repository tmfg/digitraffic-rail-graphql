package fi.digitraffic.graphql.rail.links;

import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.webmvc.BaseWebMVCTest;
import jakarta.persistence.EntityManagerFactory;

public class RoutesetToRouteSectionsLinkTest extends BaseWebMVCTest {

    private static final LocalDate DATE = LocalDate.of(2024, 6, 1);

    @Autowired
    private EntityManagerFactory entityManagerFactory;

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

    @Test
    void projectionDoesNotTriggerStationQueries() throws Exception {
        // given
        final var train = factoryService.getTrainFactory().createBaseTrain(3, DATE).getFirst();
        final var routeset = factoryService.getRoutesetMessageFactory().create(train);
        factoryService.getStationFactory().create("HKI", 1, "FI");
        factoryService.getRoutesectionFactory().create(routeset.id, "HKI", "001");
        factoryService.getRoutesectionFactory().create(routeset.id, "HKI", "002");
        factoryService.getRoutesectionFactory().create(routeset.id, "HKI", "003");

        // Enable Hibernate statistics
        final Statistics stats = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        stats.setStatisticsEnabled(true);
        stats.clear();

        // when — query only scalar fields (no station link)
        final ResultActions result = this.query("""
                {
                    routesetMessagesByVersionGreaterThan(version: "0") {
                        routesections {
                            sectionId
                            commercialTrackId
                        }
                    }
                }""");

        // then — Station must not be loaded
        result.andExpect(jsonPath("$.data.routesetMessagesByVersionGreaterThan[0].routesections.length()").value(3));
        final long stationLoads = stats.getEntityStatistics(
                "fi.digitraffic.graphql.rail.entities.Station").getLoadCount();
        assertEquals(0, stationLoads,
                "Projection should not trigger any Station entity loads, but found " + stationLoads);

        stats.setStatisticsEnabled(false);
    }
}
