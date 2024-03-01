package fi.digitraffic.graphql.rail.webmvc;

import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.factory.TrainFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class EmptyExpressionTest extends BaseWebMVCTest {
    @Autowired
    private TrainFactory trainFactory;

    @Test
    public void emptyExpression() throws Exception {
        trainFactory.createBaseTrain(new TrainId(66L, LocalDate.of(2024, 1, 1)));

        final ResultActions result = this.query("""
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

        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(1));
    }
}
