package fi.digitraffic.graphql.rail.webmvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;
import java.util.List;

import fi.digitraffic.graphql.rail.entities.TimeTableRow;
import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.repositories.TimeTableRowRepository;
import fi.digitraffic.graphql.rail.repositories.TrainRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.factory.TrainFactory;

public class SimpleTrainQueriesTest extends BaseWebMVCTest {
    @Autowired
    private TrainFactory trainFactory;

    @Autowired
    private TimeTableRowRepository timeTableRowRepository;

    @Test
    public void simpleFieldQueryShouldWork() throws Exception {
        trainFactory.createBaseTrain(new TrainId(66L, LocalDate.of(2000, 1, 1)));
        trainFactory.createBaseTrain(new TrainId(67L, LocalDate.of(2000, 1, 1)));

        final ResultActions result = this.query("{ trainsByDepartureDate(departureDate: \"2000-01-01\") {   trainNumber, version  }}");
        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(2));
    }

    @Test
    @Disabled
    public void oneToOneJoinShouldWork() throws Exception {
        trainFactory.createBaseTrain(new TrainId(66L, LocalDate.of(2000, 1, 1)));

        final ResultActions result = this.query("{ trainsByDepartureDate(departureDate: \"2000-01-01\") {   trainNumber operator { name }  }}");
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].operator.name").value("test"));
    }

    @Test
    public void oneToManyJoinShouldWork() throws Exception {
        trainFactory.createBaseTrain(new TrainId(66L, LocalDate.of(2000, 1, 1)));
        trainFactory.createBaseTrain(new TrainId(68L, LocalDate.of(2000, 1, 1)));

        final ResultActions result =
                this.query("{ trainsByDepartureDate(departureDate: \"2000-01-01\") {   trainNumber timeTableRows { scheduledTime }  }}");
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].timeTableRows.length()").value("8"));
    }

    @Test
    public void versionComparisonShouldWork() throws Exception {
        trainFactory.createBaseTrain(66, LocalDate.of(2000, 1, 1), 1L);
        trainFactory.createBaseTrain(67, LocalDate.of(2000, 1, 1), 2L);

        final ResultActions result = this.query(
                "{ trainsByDepartureDate(departureDate: \"2000-01-01\" where: {version: { greaterThan: \"1\" }}) {   trainNumber, version  }}");
        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(1));

        final ResultActions secondResult = this.query(
                "{ trainsByDepartureDate(departureDate: \"2000-01-01\" where: {version: { equals: \"1\" }}) {   trainNumber, version  }}");
        secondResult.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(1));
    }

    @Test
    public void stopSector() throws Exception {
        final var pair = trainFactory.createBaseTrain(new TrainId(66L, LocalDate.of(2000, 1, 1)));
        final var row = pair.getSecond().get(1);

        row.stopSector = "A1";
        timeTableRowRepository.save(row);

        final ResultActions result = this.query("""
                {
                    trainsByDepartureDate(departureDate: "2000-01-01") {
                        trainNumber, version,
                        timeTableRows {
                            scheduledTime, stopSector
                        }
                    }
                }
                """);
        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(1));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].timeTableRows[1].stopSector").value("A1"));
    }

    @Test
    public void trainWithoutTrackingMessages() throws Exception {
        trainFactory.createBaseTrain(66, LocalDate.of(2024, 1, 1));

        final ResultActions result = this.query("""
                {
                    trainsByDepartureDate(departureDate: "2024-01-01") {
                        trainNumber
                        version
                        trainTrackingMessages(where: {nextStation: {shortCode: {equals: "TPE"}}}) {
                            nextStation {
                              name
                              location
                            }
                          }
                    }
                }""");

        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(1));
    }

    @Test
    public void trainWithoutRoutesets() throws Exception {
        trainFactory.createBaseTrain(66, LocalDate.of(2024, 1, 1));

        final ResultActions result = this.query("""
                {
                    trainsByDepartureDate(departureDate: "2024-01-01") {
                        trainNumber
                        version
                        routesetMessages {
                            id, messageTime
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(1));
    }
}