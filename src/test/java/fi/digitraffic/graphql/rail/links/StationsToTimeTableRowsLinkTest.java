package fi.digitraffic.graphql.rail.links;

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

public class StationsToTimeTableRowsLinkTest extends BaseWebMVCTest {

    private static final LocalDate DATE = LocalDate.of(2024, 6, 1);

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    public void linkShouldWork() throws Exception {
        factoryService.getTrainFactory().createBaseTrain(1, DATE);

        final ResultActions result = this.query("""
                {
                    stations {
                        shortCode
                        timeTableRows {
                            type
                            scheduledTime
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.data.stations[?(@.shortCode == 'HKI')].timeTableRows").isNotEmpty());
    }

    @Test
    void projectionDoesNotTriggerStationQueriesViaStationPath() throws Exception {
        // given — create train with timetable rows at stations
        factoryService.getTrainFactory().createBaseTrain(1, DATE);

        // Enable Hibernate statistics
        final Statistics stats = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        stats.setStatisticsEnabled(true);

        // Baseline: root stations query WITHOUT timeTableRows child link
        stats.clear();
        query("""
                {
                    stations {
                        shortCode
                    }
                }""");
        final long baselineStationLoads = stats.getEntityStatistics(
                "fi.digitraffic.graphql.rail.entities.Station").getLoadCount();

        // when — query stations → timeTableRows with only scalar fields
        stats.clear();
        final ResultActions result = query("""
                {
                    stations {
                        shortCode
                        timeTableRows {
                            type
                            scheduledTime
                            cancelled
                        }
                    }
                }""");

        // then — Station loads should not increase when timeTableRows uses projection
        result.andExpect(jsonPath("$.data.stations[?(@.shortCode == 'HKI')].timeTableRows").isNotEmpty());
        final long stationLoads = stats.getEntityStatistics(
                "fi.digitraffic.graphql.rail.entities.Station").getLoadCount();
        assertEquals(baselineStationLoads, stationLoads,
                "Projection should not trigger additional Station entity loads via TimeTableRow, " +
                "but found " + stationLoads + " vs baseline " + baselineStationLoads);

        stats.setStatisticsEnabled(false);
    }

    @Test
    public void whereOrderByAndTakeShouldWork() throws Exception {
        factoryService.getTrainFactory().createBaseTrain(1, DATE);

        final ResultActions result = this.query("""
                {
                    stations(where: { shortCode: { equals: "TPE" } }) {
                        shortCode
                        timeTableRows(
                            where: { type: { equals: "ARRIVAL" } }
                            orderBy: [{ scheduledTime: DESCENDING }]
                            take: 1
                        ) {
                            type
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.data.stations.length()").value(1));
        result.andExpect(jsonPath("$.data.stations[0].shortCode").value("TPE"));
        result.andExpect(jsonPath("$.data.stations[0].timeTableRows.length()").value(1));
        result.andExpect(jsonPath("$.data.stations[0].timeTableRows[0].type").value("ARRIVAL"));
    }

    @Test
    public void skipShouldWork() throws Exception {
        factoryService.getTrainFactory().createBaseTrain(1, DATE);

        final ResultActions result = this.query("""
                {
                    stations(where: { shortCode: { equals: "HKI" } }) {
                        shortCode
                        timeTableRows(skip: 99) {
                            type
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.data.stations.length()").value(1));
        result.andExpect(jsonPath("$.data.stations[0].timeTableRows").isEmpty());
    }
}

