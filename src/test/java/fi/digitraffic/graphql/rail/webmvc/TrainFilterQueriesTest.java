package fi.digitraffic.graphql.rail.webmvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.factory.TrainFactory;


public class TrainFilterQueriesTest extends BaseWebMVCTest {
    @Autowired
    private TrainFactory trainFactory;

    @Test
    public void rootFieldFilterShouldWork() throws Exception {
        trainFactory.createBaseTrain(new TrainId(66L, LocalDate.of(2000, 1, 1)));
        trainFactory.createBaseTrain(new TrainId(67L, LocalDate.of(2000, 1, 1)));
        trainFactory.createBaseTrain(new TrainId(68L, LocalDate.of(2000, 1, 1)));
        trainFactory.createBaseTrain(new TrainId(69L, LocalDate.of(2000, 1, 1)));

        ResultActions result = this.query("{ trainsByDepartureDate(departureDate: \\\"2000-01-01\\\", where:{trainNumber:{eq:68}}) {   trainNumber, version  }}");
        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(1));
    }
}
