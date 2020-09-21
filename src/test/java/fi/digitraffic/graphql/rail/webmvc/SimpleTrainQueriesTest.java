package fi.digitraffic.graphql.rail.webmvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.factory.TrainFactory;


public class SimpleTrainQueriesTest extends BaseWebMVCTest {
    @Autowired
    private TrainFactory trainFactory;

//    @Autowired
//    private OperatorRepository operatorRepository;
//
//    @Autowired
//    private TrainRepository trainRepository;

    @Test
    public void simpleFieldQueryShouldWork() throws Exception {
        trainFactory.createBaseTrain(new TrainId(66L, LocalDate.of(2000, 1, 1)));
        trainFactory.createBaseTrain(new TrainId(67L, LocalDate.of(2000, 1, 1)));

        ResultActions result = this.query("{ trainsByDepartureDate(departureDate: \\\"2000-01-01\\\") {   trainNumber, version  }}");
        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(2));
    }

    @Test
    public void oneToOneJoinShouldWork() throws Exception {
        trainFactory.createBaseTrain(new TrainId(66L, LocalDate.of(2000, 1, 1)));

        ResultActions result = this.query("{ trainsByDepartureDate(departureDate: \\\"2000-01-01\\\") {   trainNumber operator { name }  }}");
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].operator.name").value("test"));
    }

    @Test
    public void oneToManyJoinShouldWork() throws Exception {
        trainFactory.createBaseTrain(new TrainId(66L, LocalDate.of(2000, 1, 1)));
        trainFactory.createBaseTrain(new TrainId(68L, LocalDate.of(2000, 1, 1)));

        ResultActions result = this.query("{ trainsByDepartureDate(departureDate: \\\"2000-01-01\\\") {   trainNumber timeTableRows { scheduledTime }  }}");
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].timeTableRows.length()").value("8"));
    }
}
