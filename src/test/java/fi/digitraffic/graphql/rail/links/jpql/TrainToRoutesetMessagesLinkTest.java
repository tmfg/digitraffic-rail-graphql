package fi.digitraffic.graphql.rail.links.jpql;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.webmvc.BaseWebMVCTest;

class TrainToRoutesetMessagesLinkTest extends BaseWebMVCTest {

    @Test
    void linkReturnsRoutesetMessagesForTrain() throws Exception {
        final var train = factoryService.getTrainFactory().createBaseTrain(new TrainId(66L, LocalDate.of(2020, 9, 17)));
        factoryService.getRoutesetMessageFactory().create(train.getFirst());

        final ResultActions result = query("""
                {
                  trainsByDepartureDate(departureDate: "2020-09-17") {
                    trainNumber
                    routesetMessages {
                      version
                      routeType
                    }
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].routesetMessages.length()").value(1));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].routesetMessages[0].version").value("1"));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].routesetMessages[0].routeType").value("A"));
    }

    @Test
    void linkReturnsEmptyListWhenNoRoutesetMessages() throws Exception {
        factoryService.getTrainFactory().createBaseTrain(new TrainId(66L, LocalDate.of(2020, 9, 17)));

        final ResultActions result = query("""
                {
                  trainsByDepartureDate(departureDate: "2020-09-17") {
                    trainNumber
                    routesetMessages {
                      version
                    }
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].routesetMessages.length()").value(0));
    }

    @Test
    void linkOnlyReturnsMessagesForCorrectTrain() throws Exception {
        final var train1 = factoryService.getTrainFactory().createBaseTrain(new TrainId(66L, LocalDate.of(2020, 9, 17)));
        final var train2 = factoryService.getTrainFactory().createBaseTrain(new TrainId(77L, LocalDate.of(2020, 9, 17)));
        factoryService.getRoutesetMessageFactory().create(train1.getFirst());

        final ResultActions result = query("""
                {
                  trainsByDepartureDate(departureDate: "2020-09-17") {
                    trainNumber
                    routesetMessages {
                      version
                    }
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.trainsByDepartureDate[?(@.trainNumber==66)].routesetMessages.length()").value(1));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[?(@.trainNumber==77)].routesetMessages.length()").value(0));
    }
}

