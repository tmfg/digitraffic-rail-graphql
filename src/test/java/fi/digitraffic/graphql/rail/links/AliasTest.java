package fi.digitraffic.graphql.rail.links;

import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.webmvc.BaseWebMVCTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class AliasTest extends BaseWebMVCTest {

    @Test
    public void aliasesShouldWork() throws Exception {
        final var train = factoryService.getTrainFactory().createBaseTrain(new TrainId(66L, LocalDate.of(2020, 9, 17)));

        final ResultActions result = this.query("""
            {
              trainsByDepartureDate(departureDate: "2020-09-17") {
                trainNumber
                timeTableFirst: timeTableRows(orderBy: { scheduledTime: ASCENDING }, take: 1) {
                  station {
                    name
                  }
                }
                timeTableLast: timeTableRows(orderBy: { scheduledTime: DESCENDING }, take: 1) {
                  station {
                    name
                  }
                }
              }
            }
            """);

        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].timeTableFirst[0].station.name").value("HKI"));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].timeTableLast[0].station.name").value("OL"));
    }

    @Test
    public void complexQuery() throws Exception {
        final var train = factoryService.getTrainFactory().createBaseTrain(new TrainId(66L, LocalDate.of(2020, 9, 17)));

        final ResultActions result = this.query("""
            {
              trainsByDepartureDate(departureDate: "2020-09-17") {
                trainNumber
                timeTableFirst: timeTableRows(orderBy: { scheduledTime: ASCENDING }, take: 1) {
                  station {
                    name
                  }
                }
                timeTableLast: timeTableRows(orderBy: { scheduledTime: DESCENDING }, take: 1) {
                  station {
                    name
                  }
                }
                tt: timeTableRows {
                  station {
                    name
                  }
                }
              }
            }
            """);

        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].timeTableFirst.length()").value(1));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].timeTableFirst[0].station.name").value("HKI"));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].timeTableLast.length()").value(1));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].timeTableLast[0].station.name").value("OL"));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].tt.length()").value(8));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].tt[0].station.name").value("HKI"));
    }

}
