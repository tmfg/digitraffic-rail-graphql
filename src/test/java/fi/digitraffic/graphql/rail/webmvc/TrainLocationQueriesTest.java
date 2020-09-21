package fi.digitraffic.graphql.rail.webmvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.factory.TrainFactory;
import fi.digitraffic.graphql.rail.factory.TrainLocationFactory;


public class TrainLocationQueriesTest extends BaseWebMVCTest {
    @Autowired
    private TrainLocationFactory trainLocationFactory;

    @Autowired
    private TrainFactory trainFactory;

    @Test
    public void speedOver100ShouldWork() throws Exception {
        Train train66 = trainFactory.createBaseTrain(new TrainId(66L, LocalDate.of(2000, 1, 1))).getLeft();
        Train train67 = trainFactory.createBaseTrain(new TrainId(67L, LocalDate.of(2000, 1, 1))).getLeft();

        trainLocationFactory.create(1, 2, 99, train66);
        trainLocationFactory.create(1, 2, 100, train66);
        trainLocationFactory.create(1, 2, 101, train66);
        trainLocationFactory.create(1, 2, 102, train67);

        ResultActions result = this.query("{   latestTrainLocations(where: {speed: {gt: 100}}) {    speed    train {      trainNumber      departureDate    }  }}");
        result.andExpect(jsonPath("$.data.latestTrainLocations.length()").value(2));
    }


}
