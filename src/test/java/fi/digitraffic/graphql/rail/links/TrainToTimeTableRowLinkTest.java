package fi.digitraffic.graphql.rail.links;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.webmvc.BaseWebMVCTest;

public class TrainToTimeTableRowLinkTest extends BaseWebMVCTest {

    private static final LocalDate DATE = LocalDate.of(2024, 6, 1);

    @Test
    public void linkShouldWork() throws Exception {
        factoryService.getTrainFactory().createBaseTrain(1, DATE);

        final ResultActions result = this.query("""
                {
                    trainsByDepartureDate(departureDate: "2024-06-01") {
                        trainNumber
                        timeTableRows {
                            type
                            scheduledTime
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].timeTableRows.length()").value(8));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].timeTableRows[0].type").value("DEPARTURE"));
    }
}

