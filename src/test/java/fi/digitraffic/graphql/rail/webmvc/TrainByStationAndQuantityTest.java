package fi.digitraffic.graphql.rail.webmvc;

import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.factory.TrainFactory;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;

public class TrainByStationAndQuantityTest extends BaseWebMVCTest {
    @Autowired
    private TrainFactory trainFactory;

    @Test
    public void byStation() throws Exception {
        trainFactory.createBaseTrain(new TrainId(66L, LocalDate.now()));

        final ResultActions result = this.query("{ trainsByStationAndQuantity(station: \\\"HKI\\\") {   trainNumber, version  }}");
        result.andExpect(jsonPath("$.data.trainsByStationAndQuantity.length()").value(0));
    }

    @Test
    public void byStationAndArrivedTrains() throws Exception {
        trainFactory.createBaseTrain(new TrainId(66L, LocalDate.now()));

        final ResultActions result = this.query("{ trainsByStationAndQuantity(station: \\\"HKI\\\", arrivedTrains: 1) {   trainNumber, version  }}");
        result.andExpect(jsonPath("$.data.trainsByStationAndQuantity.length()").value(0));
    }

    @Test
    public void byStationAndArrivingTrains() throws Exception {
        trainFactory.createBaseTrain(new TrainId(66L, LocalDate.now()));

        final ResultActions result = this.query("{ trainsByStationAndQuantity(station: \\\"HKI\\\", arrivingTrains: 1) {   trainNumber, version  }}");
        result.andExpect(jsonPath("$.data.trainsByStationAndQuantity.length()").value(0));
    }

    @Test
    public void byStationAndDepartedTrains() throws Exception {
        trainFactory.createBaseTrain(new TrainId(66L, LocalDate.now()));

        final ResultActions result = this.query("{ trainsByStationAndQuantity(station: \\\"HKI\\\", departedTrains: 1) {   trainNumber, version  }}");
        result.andExpect(jsonPath("$.data.trainsByStationAndQuantity.length()").value(0));
    }

    @Test
    public void byStationAndDepartingTrains() throws Exception {
        trainFactory.createBaseTrain(new TrainId(66L, LocalDate.now()));

        final ResultActions result = this.query("{ trainsByStationAndQuantity(station: \\\"HKI\\\", departingTrains: 1) {   trainNumber, version  }}");
        result.andExpect(jsonPath("$.data.trainsByStationAndQuantity.length()").value(0));
    }

    @Test
    public void byStationAndIncludeNonStopping() throws Exception {
        trainFactory.createBaseTrain(new TrainId(66L, LocalDate.now()));

        final ResultActions result = this.query("{ trainsByStationAndQuantity(station: \\\"HKI\\\", includeNonStopping: false) {   trainNumber, version  }}");
        result.andExpect(jsonPath("$.data.trainsByStationAndQuantity.length()").value(0));
    }

}
