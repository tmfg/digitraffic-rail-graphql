package fi.digitraffic.graphql.rail.links;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.webmvc.BaseWebMVCTest;

class TrainToTrainTrackingMessagesLinkTest extends BaseWebMVCTest {

    @Test
    void linkReturnsTrackingMessagesForTrain() throws Exception {
        final var train = factoryService.getTrainFactory().createBaseTrain(new TrainId(66L, LocalDate.of(2020, 9, 17)));
        factoryService.getTrainTrackingMessageFactory().create(train.getFirst());

        final ResultActions result = query("""
                {
                  trainsByDepartureDate(departureDate: "2020-09-17") {
                    trainNumber
                    trainTrackingMessages {
                      version
                    }
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].trainTrackingMessages.length()").value(1));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].trainTrackingMessages[0].version").value("1"));
    }

    @Test
    void linkReturnsEmptyListWhenNoTrackingMessages() throws Exception {
        factoryService.getTrainFactory().createBaseTrain(new TrainId(66L, LocalDate.of(2020, 9, 17)));

        final ResultActions result = query("""
                {
                  trainsByDepartureDate(departureDate: "2020-09-17") {
                    trainNumber
                    trainTrackingMessages {
                      version
                    }
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].trainTrackingMessages.length()").value(0));
    }

    @Test
    void linkOnlyReturnsMessagesForCorrectTrain() throws Exception {
        final var train1 = factoryService.getTrainFactory().createBaseTrain(new TrainId(66L, LocalDate.of(2020, 9, 17)));
        final var train2 = factoryService.getTrainFactory().createBaseTrain(new TrainId(77L, LocalDate.of(2020, 9, 17)));
        factoryService.getTrainTrackingMessageFactory().create(train1.getFirst());

        final ResultActions result = query("""
                {
                  trainsByDepartureDate(departureDate: "2020-09-17") {
                    trainNumber
                    trainTrackingMessages {
                      version
                    }
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.trainsByDepartureDate[?(@.trainNumber==66)].trainTrackingMessages.length()").value(1));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[?(@.trainNumber==77)].trainTrackingMessages.length()").value(0));
    }
}

