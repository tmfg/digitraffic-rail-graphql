package fi.digitraffic.graphql.rail.links;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.webmvc.BaseWebMVCTest;
import jakarta.persistence.EntityManagerFactory;

public class TrainToTimeTableRowLinkTest extends BaseWebMVCTest {

    private static final LocalDate DATE = LocalDate.of(2024, 6, 1);

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    public void linkShouldWork() throws Exception {
        factoryService.getTrainFactory().createBaseTrain(1, DATE);

        final ResultActions result = this.query("""
                {
                    trainsByDepartureDate(departureDate: "2024-06-01") {
                        trainNumber
                        timeTableRows {
                            type
                            scheduledTime
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].timeTableRows.length()").value(8));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].timeTableRows[0].type").value("DEPARTURE"));
    }

    // --- Projection regression tests ---

    @Test
    void allNonHiddenFieldsArePopulatedCorrectly() throws Exception {
        // given
        factoryService.getTrainFactory().createBaseTrain(new TrainId(1L, DATE));

        // when — query all non-hidden fields
        // Hidden: id, trainNumber, departureDate, stationShortCode, stationUICCode, countryCode
        final ResultActions result = query("""
                {
                    trainsByDepartureDate(departureDate: "2024-06-01") {
                        trainNumber
                        timeTableRows {
                            type
                            trainStopping
                            commercialStop
                            commercialTrack
                            cancelled
                            scheduledTime
                            actualTime
                            differenceInMinutes
                            liveEstimateTime
                            estimateSourceType
                            unknownDelay
                            stopSector
                        }
                    }
                }""");

        // then
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].timeTableRows.length()").value(8));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].timeTableRows[0].type").value("DEPARTURE"));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].timeTableRows[0].trainStopping").value(true));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].timeTableRows[0].cancelled").value(false));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].timeTableRows[0].scheduledTime").isNotEmpty());
    }

    @Test
    void whereOnScalarFieldFiltersCorrectly() throws Exception {
        // given — createBaseTrain creates 4 ARRIVAL + 4 DEPARTURE rows
        factoryService.getTrainFactory().createBaseTrain(new TrainId(1L, DATE));

        // when
        final ResultActions result = query("""
                {
                    trainsByDepartureDate(departureDate: "2024-06-01") {
                        timeTableRows(where: {type: {equals: "ARRIVAL"}}) {
                            type
                        }
                    }
                }""");

        // then — only ARRIVAL rows returned
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].timeTableRows.length()").value(4));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].timeTableRows[0].type").value("ARRIVAL"));
    }

    @Test
    void orderByScheduledTimeDescReversesDefaultOrder() throws Exception {
        // given
        factoryService.getTrainFactory().createBaseTrain(new TrainId(1L, DATE));

        // when — default order is scheduledTime ASC, so DESC should reverse
        final ResultActions result = query("""
                {
                    trainsByDepartureDate(departureDate: "2024-06-01") {
                        timeTableRows(orderBy: [{scheduledTime: DESCENDING}]) {
                            type
                            scheduledTime
                        }
                    }
                }""");

        // then — last row (ARRIVAL at OL) should be first
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].timeTableRows[0].type").value("ARRIVAL"));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].timeTableRows.length()").value(8));
    }

    @Test
    void whereOnAssociationPathFiltersCorrectly() throws Exception {
        // given — createBaseTrain creates rows at HKI, PSL, TPE, JY, OL
        factoryService.getTrainFactory().createBaseTrain(new TrainId(1L, DATE));

        // when
        final ResultActions result = query("""
                {
                    trainsByDepartureDate(departureDate: "2024-06-01") {
                        timeTableRows(where: {station: {shortCode: {equals: "HKI"}}}) {
                            type
                        }
                    }
                }""");

        // then — only HKI rows (1 DEPARTURE)
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].timeTableRows.length()").value(1));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].timeTableRows[0].type").value("DEPARTURE"));
    }

    @Test
    void childLinksResolveCorrectly() throws Exception {
        // given
        factoryService.getTrainFactory().createBaseTrain(new TrainId(1L, DATE));

        // when
        final ResultActions result = query("""
                {
                    trainsByDepartureDate(departureDate: "2024-06-01") {
                        timeTableRows {
                            type
                            station { shortCode }
                            train { trainNumber }
                        }
                    }
                }""");

        // then
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].timeTableRows[0].station.shortCode").value("HKI"));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].timeTableRows[0].train.trainNumber").value(1));
    }

    @Test
    void multipleTrainsBatchedCorrectly() throws Exception {
        // given
        factoryService.getTrainFactory().createBaseTrain(new TrainId(10L, DATE));
        factoryService.getTrainFactory().createBaseTrain(new TrainId(20L, DATE));
        factoryService.getTrainFactory().createBaseTrain(new TrainId(30L, DATE));

        // when
        final ResultActions result = query("""
                {
                    trainsByDepartureDate(departureDate: "2024-06-01") {
                        trainNumber
                        timeTableRows { type }
                    }
                }""");

        // then — each train has 8 rows from createBaseTrain
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[?(@.trainNumber==10)].timeTableRows.length()").value(8));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[?(@.trainNumber==20)].timeTableRows.length()").value(8));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[?(@.trainNumber==30)].timeTableRows.length()").value(8));
    }

    @Test
    void projectionDoesNotTriggerStationQueries() throws Exception {
        // given — create a train with 8 timetable rows, each at a station
        factoryService.getTrainFactory().createBaseTrain(new TrainId(1L, DATE));

        // Enable Hibernate statistics to count entity loads
        final Statistics stats = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        stats.setStatisticsEnabled(true);
        stats.clear();

        // when — query only scalar fields (no station/train/causes links)
        final ResultActions result = query("""
                {
                    trainsByDepartureDate(departureDate: "2024-06-01") {
                        timeTableRows {
                            type
                            scheduledTime
                            cancelled
                            trainStopping
                        }
                    }
                }""");

        // then — projection bypasses entity loading, so Station must never be loaded
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].timeTableRows.length()").value(8));
        final long stationLoads = stats.getEntityStatistics(
                "fi.digitraffic.graphql.rail.entities.Station").getLoadCount();
        assertEquals(0, stationLoads,
                "Projection should not trigger any Station entity loads, but found " + stationLoads);

        stats.setStatisticsEnabled(false);
    }
}

