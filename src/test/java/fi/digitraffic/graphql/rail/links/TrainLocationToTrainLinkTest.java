package fi.digitraffic.graphql.rail.links;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.webmvc.BaseWebMVCTest;

public class TrainLocationToTrainLinkTest extends BaseWebMVCTest {

    private static final LocalDate DATE = LocalDate.of(2024, 6, 1);

    @Test
    public void linkShouldWork() throws Exception {
        final Train train = factoryService.getTrainFactory().createBaseTrain(new TrainId(1L, DATE)).getFirst();
        factoryService.getTrainLocationFactory().create(25.0, 60.0, 100, train);

        final ResultActions result = this.query("""
                {
                    latestTrainLocations {
                        speed
                        train {
                            trainNumber
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.data.latestTrainLocations.length()").value(1));
        result.andExpect(jsonPath("$.data.latestTrainLocations[0].train.trainNumber").value(1));
    }
}

