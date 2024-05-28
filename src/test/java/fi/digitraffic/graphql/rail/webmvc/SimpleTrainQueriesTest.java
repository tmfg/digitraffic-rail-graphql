package fi.digitraffic.graphql.rail.webmvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.factory.TrainFactory;

public class SimpleTrainQueriesTest extends BaseWebMVCTest {
    @Autowired
    private TrainFactory trainFactory;

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
}
