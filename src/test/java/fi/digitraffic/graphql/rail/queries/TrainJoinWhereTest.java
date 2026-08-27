package fi.digitraffic.graphql.rail.queries;

import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.webmvc.BaseWebMVCTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class TrainJoinWhereTest extends BaseWebMVCTest {
    @Test
    void routesetWhere() throws Exception {
        final var train1 = factoryService.getTrainFactory().createBaseTrain(new TrainId(66L, LocalDate.of(2020, 9, 17)));
        final var train2 = factoryService.getTrainFactory().createBaseTrain(new TrainId(77L, LocalDate.of(2020, 9, 17)));
        factoryService.getRoutesetMessageFactory().create(train1.getFirst());

        final ResultActions result = query("""
                {
                  trainsByDepartureDate(departureDate: "2020-09-17", where: {
                      routesetMessages: {
                         contains: {
                            routeType: {
                                equals: "A"
                            }
                         }
                      }
                    }) {
                    trainNumber
                    routesetMessages {
                      version
                    }
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.trainsByDepartureDate[?(@.trainNumber==66)]").exists());
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[?(@.trainNumber==77)]").doesNotExist());
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[?(@.trainNumber==66)].routesetMessages.length()").value(1));
    }

    @Test
    void trainTrackingWhere() throws Exception {
        final var train1 = factoryService.getTrainFactory().createBaseTrain(new TrainId(66L, LocalDate.of(2020, 9, 17)));
        final var train2 = factoryService.getTrainFactory().createBaseTrain(new TrainId(77L, LocalDate.of(2020, 9, 17)));
        factoryService.getTrainTrackingMessageFactory().create(train1.getFirst());

        final ResultActions result = query("""
                {
                   trainsByDepartureDate(departureDate: "2020-09-17", where:  {                       \s
                           trainTrackingMessages:  {
                              contains:  {
                               trackSectionCode:  {
                                     equals: "TEST_UNIQUE"
                                 }
                              }
                           }

                     }) {
                     trainNumber
                     trainTrackingMessages {
                       version
                     }
                   }
                 }
                """);

        result.andExpect(jsonPath("$.data.trainsByDepartureDate[?(@.trainNumber==66)]").exists());
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[?(@.trainNumber==66)].trainTrackingMessages.length()").value(1));
    }
}
