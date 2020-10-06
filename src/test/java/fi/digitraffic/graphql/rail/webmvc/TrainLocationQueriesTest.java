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

        ResultActions result = this.query("{   latestTrainLocations(where: {speed: {greaterThan: 100}}) {    speed    train {      trainNumber      departureDate    }  }}");
        result.andExpect(jsonPath("$.data.latestTrainLocations.length()").value(2));
    }

//    @Test
//    public void coordinateFilteringShouldWork() throws Exception {
//        Train train66 = trainFactory.createBaseTrain(new TrainId(66L, LocalDate.of(2000, 1, 1))).getLeft();
//        Train train67 = trainFactory.createBaseTrain(new TrainId(67L, LocalDate.of(2000, 1, 1))).getLeft();
//        Train train68 = trainFactory.createBaseTrain(new TrainId(68L, LocalDate.of(2000, 1, 1))).getLeft();
//        Train train69 = trainFactory.createBaseTrain(new TrainId(69L, LocalDate.of(2000, 1, 1))).getLeft();
//        Train train70 = trainFactory.createBaseTrain(new TrainId(70L, LocalDate.of(2000, 1, 1))).getLeft();
//        Train train71 = trainFactory.createBaseTrain(new TrainId(71L, LocalDate.of(2000, 1, 1))).getLeft();
//
//        trainLocationFactory.create(1, 1, 100, train66);
//        trainLocationFactory.create(2, 3, 100, train67);
//        trainLocationFactory.create(4, 4, 100, train68);
//        trainLocationFactory.create(3, 2, 100, train69);
//        trainLocationFactory.create(4, 3, 100, train70);
//        trainLocationFactory.create(5, 1, 100, train71);
//
//        ResultActions result = this.query("{   latestTrainLocations(where: {location: {inside: [3,2,5,4]}}) {    location    train {      trainNumber      departureDate    }  }}");
//        result.andExpect(jsonPath("$.data.latestTrainLocations.length()").value(3));
//    }

    @Test
    public void nestedSortingShouldWork() throws Exception {
        Train train66 = trainFactory.createBaseTrain(new TrainId(66L, LocalDate.of(2000, 1, 1))).getLeft();
        Train train67 = trainFactory.createBaseTrain(new TrainId(67L, LocalDate.of(2000, 1, 1))).getLeft();
        Train train68 = trainFactory.createBaseTrain(new TrainId(68L, LocalDate.of(2000, 1, 1))).getLeft();
        Train train69 = trainFactory.createBaseTrain(new TrainId(69L, LocalDate.of(2000, 1, 1))).getLeft();
        Train train70 = trainFactory.createBaseTrain(new TrainId(70L, LocalDate.of(2000, 1, 1))).getLeft();
        Train train71 = trainFactory.createBaseTrain(new TrainId(71L, LocalDate.of(2000, 1, 1))).getLeft();

        trainLocationFactory.create(1, 1, 100, train66);
        trainLocationFactory.create(2, 3, 100, train70);
        trainLocationFactory.create(4, 4, 100, train68);
        trainLocationFactory.create(3, 2, 100, train69);
        trainLocationFactory.create(4, 3, 100, train67);
        trainLocationFactory.create(5, 1, 100, train71);

        ResultActions result = this.query("{  latestTrainLocations(orderBy: {train:{trainNumber:DESCENDING}}) {    speed    train {      trainNumber      departureDate    }  }}");
        result.andExpect(jsonPath("$.data.latestTrainLocations.length()").value(6));
        result.andExpect(jsonPath("$.data.latestTrainLocations[0].train.trainNumber").value(71));
        result.andExpect(jsonPath("$.data.latestTrainLocations[1].train.trainNumber").value(70));
        result.andExpect(jsonPath("$.data.latestTrainLocations[2].train.trainNumber").value(69));
        result.andExpect(jsonPath("$.data.latestTrainLocations[3].train.trainNumber").value(68));
        result.andExpect(jsonPath("$.data.latestTrainLocations[4].train.trainNumber").value(67));
        result.andExpect(jsonPath("$.data.latestTrainLocations[5].train.trainNumber").value(66));

    }
}
