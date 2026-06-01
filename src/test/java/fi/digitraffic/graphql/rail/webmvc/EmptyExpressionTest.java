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

    @Test
    public void invalidOrderByDirectionShouldReturnError() throws Exception {
        trainFactory.createBaseTrain(66, LocalDate.of(2024, 1, 1));

        final ResultActions result = this.queryAndExpectError("""
                {
                    trainsByDepartureDate(departureDate: "2024-01-01") {
                        trainNumber
                        timeTableRows(orderBy: [{ scheduledTime: SIDEWAYS }]) {
                            scheduledTime
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.errors").isNotEmpty());
    }

    @Test
    public void unknownFilterOperatorShouldReturnError() throws Exception {
        trainFactory.createBaseTrain(66, LocalDate.of(2024, 1, 1));

        final ResultActions result = this.queryAndExpectError("""
                {
                    trainsByDepartureDate(departureDate: "2024-01-01") {
                        trainNumber
                        timeTableRows(where: { type: { bogus: "ARRIVAL" } }) {
                            type
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.errors").isNotEmpty());
    }

    @Test
    public void containsWithInvalidShapeShouldReturnError() throws Exception {
        trainFactory.createBaseTrain(66, LocalDate.of(2024, 1, 1));

        final ResultActions result = this.queryAndExpectError("""
                {
                    trainsByDepartureDate(
                        departureDate: "2024-01-01"
                        where: { timeTableRows: { contains: 1 } }
                    ) {
                        trainNumber
                    }
                }""");

        result.andExpect(jsonPath("$.errors").isNotEmpty());
    }
}
