package fi.digitraffic.graphql.rail.links;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.webmvc.BaseWebMVCTest;

class TimeTableRowToCausesLinkTest extends BaseWebMVCTest {
    @Test
    public void linkShouldWork() throws Exception {
        var train = factoryService.getTrainFactory().createBaseTrain(new TrainId(66L, LocalDate.of(2020, 9, 17)));

        factoryService.getCauseFactory().create(train.getSecond().get(0));

        final ResultActions result = this.query("""
            {
              trainsByDepartureDate(departureDate: "2020-09-17") {
                trainNumber
                timeTableRows {
                  causes {
                    __typename
                  }
                }
              }
            }
            """);
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].timeTableRows[0].causes.length()").value(1));
    }
}
