package fi.digitraffic.graphql.rail.webmvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.factory.TrainFactory;

public class EmptyExpressionTest extends BaseWebMVCTest {
    @Autowired
    private TrainFactory trainFactory;

    @Test
    public void emptyExpression() throws Exception {
        trainFactory.createBaseTrain(66, LocalDate.of(2024, 1, 1));

        final ResultActions result = this.queryAndExpectError("""
                {
                    trainsByDepartureDate(departureDate: "2024-01-01") {
                        trainNumber
                        version
                        trainTrackingMessages(where: {nextStation: {shortCode: {equals: "TPE"}, passengerTraffic: {}}}) {
                            nextStation {
                              name
                              location
                            }
                            previousStation {
                              name
                              location
                            }
                          }
                    }
                }""");

        result.andExpect(jsonPath("$.errors.length()").value(2));
    }
}
