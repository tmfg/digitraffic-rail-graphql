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

public class TimeTableRowToTrainLinkTest extends BaseWebMVCTest {

    private static final LocalDate DATE = LocalDate.of(2024, 6, 1);

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    public void linkShouldWork() throws Exception {
        factoryService.getTrainFactory().createBaseTrain(new TrainId(1L, DATE));

        final ResultActions result = this.query("""
                {
                    trainsByDepartureDate(departureDate: "2024-06-01") {
                        timeTableRows {
                            train {
                                trainNumber
                            }
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(1));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].timeTableRows[0].train.trainNumber").value(1));
    }

    // --- Projection regression tests ---

    @Test
    void trainIsResolvedWithScalarFieldsViaProjection() throws Exception {
        // given
        factoryService.getTrainFactory().createBaseTrain(new TrainId(1L, DATE));

        // when
        final ResultActions result = query("""
                {
                    trainsByDepartureDate(departureDate: "2024-06-01") {
                        timeTableRows {
                            type
                            train {
                                trainNumber
                                departureDate
                                cancelled
                                runningCurrently
                                commuterLineid
                            }
                        }
                    }
                }""");

        // then
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].timeTableRows[0].train.trainNumber").value(1));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].timeTableRows[0].train.cancelled").value(false));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].timeTableRows[0].train.runningCurrently").value(true));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].timeTableRows[0].train.commuterLineid").value("Z"));
    }

    @Test
    void projectionDoesNotTriggerOperatorQueries() throws Exception {
        // given — createBaseTrain creates a train with an Operator (non-PK join via operator_short_code)
        factoryService.getTrainFactory().createBaseTrain(new TrainId(1L, DATE));

        // Enable Hibernate statistics
        final Statistics stats = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        stats.setStatisticsEnabled(true);

        // Baseline: root train query WITHOUT timeTableRows → train child link
        stats.clear();
        query("""
                {
                    trainsByDepartureDate(departureDate: "2024-06-01") {
                        trainNumber
                    }
                }""");
        final long baselineOperatorLoads = stats.getEntityStatistics(
                "fi.digitraffic.graphql.rail.entities.Operator").getLoadCount();

        // when — query timeTableRows → train with only scalar fields (no operator link)
        stats.clear();
        final ResultActions result = query("""
                {
                    trainsByDepartureDate(departureDate: "2024-06-01") {
                        timeTableRows {
                            train {
                                trainNumber
                                cancelled
                                runningCurrently
                            }
                        }
                    }
                }""");

        // then — Operator loads should not increase beyond baseline (root Train's eager @ManyToOne Operator)
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].timeTableRows[0].train.trainNumber").value(1));
        final long operatorLoads = stats.getEntityStatistics(
                "fi.digitraffic.graphql.rail.entities.Operator").getLoadCount();
        assertEquals(baselineOperatorLoads, operatorLoads,
                "Projection should not trigger additional Operator entity loads, " +
                "but found " + operatorLoads + " vs baseline " + baselineOperatorLoads);

        stats.setStatisticsEnabled(false);
    }
}

