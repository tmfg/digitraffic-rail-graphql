package fi.digitraffic.graphql.rail.links;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.beans.factory.annotation.Autowired;

import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.webmvc.BaseWebMVCTest;
import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.repositories.TrainRepository;


@SpringBootTest(properties = { "digitraffic.batch-load-size=4" })
public class BaseLinkTest extends BaseWebMVCTest {

    @Autowired
    private TrainRepository trainRepository;
    @Test
    public void partitioningShouldWork() throws Exception {
        final Train train66 = factoryService.getTrainFactory().createBaseTrain(new TrainId(66L, LocalDate.of(2000, 1, 1))).getFirst();
        final Train train67 = factoryService.getTrainFactory().createBaseTrain(new TrainId(67L, LocalDate.of(2000, 1, 1))).getFirst();
        final Train train68 = factoryService.getTrainFactory().createBaseTrain(new TrainId(68L, LocalDate.of(2000, 1, 1))).getFirst();
        final Train train69 = factoryService.getTrainFactory().createBaseTrain(new TrainId(69L, LocalDate.of(2000, 1, 1))).getFirst();
        final Train train70 = factoryService.getTrainFactory().createBaseTrain(new TrainId(70L, LocalDate.of(2000, 1, 1))).getFirst();
        final Train train71 = factoryService.getTrainFactory().createBaseTrain(new TrainId(71L, LocalDate.of(2000, 1, 1))).getFirst();

        trainRepository.saveAll(List.of(train66, train67, train68, train69, train70, train71));

        final ResultActions result = this.query("{  trainsByDepartureDate(departureDate: \"2000-01-01\") {   timeTableRows {       type       scheduledTime       station {         shortCode       }       commercialTrack     }  }}");
        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(6));
    }
}
