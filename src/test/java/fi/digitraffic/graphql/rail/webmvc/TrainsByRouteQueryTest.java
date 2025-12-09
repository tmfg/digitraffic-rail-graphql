package fi.digitraffic.graphql.rail.webmvc;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static fi.digitraffic.graphql.rail.util.TestDataUtils.HKI;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Integration tests for trainsByRoute GraphQL query.
 * Tests the GraphQL implementation against the same logic as the REST API
 * endpoint: /live-trains/station/{departure_station}/{arrival_station}
 */
public class TrainsByRouteQueryTest extends BaseWebMVCTest {

    @Test
    public void shouldFindTrainsByRoute() throws Exception {

        factoryService.getTrainFactory().createBaseTrain(1, LocalDate.of(2024, 1, 1));

        final String query = """
            {
                trainsByRoute(
                    departureStation: "HKI"
                    arrivalStation: "TPE"
                    departureDate: "2024-01-01"
                ) {
                    trainNumber
                    departureDate
                    timeTableRows {
                        station {
                            shortCode
                        }
                        type
                        scheduledTime
                    }
                }
            }
            """;

        // Should find trains from Helsinki to Tampere on 2024-01-01
        final ResultActions result = this.query(query);

        result.andExpect(jsonPath("$.data.trainsByRoute[0].timeTableRows.length()").value(8));
        result.andExpect(jsonPath("$.data.trainsByRoute[0].trainNumber").value(1));
    }

    @Test
    public void shouldFindTrainsByRouteWithDefaultDates() throws Exception {
        // Create a train departing today (within default time range)
        final LocalDate today = LocalDate.now();
        factoryService.getTrainFactory().createBaseTrain(42, today);

        final String query = """
            {
                trainsByRoute(
                    departureStation: "HKI"
                    arrivalStation: "TPE"
                ) {
                    trainNumber
                    departureDate
                }
            }
            """;

        final ResultActions result = this.query(query);

        // Should find the train using default time range (now to now + 24h)
        result.andExpect(jsonPath("$.data.trainsByRoute[0].trainNumber").value(42));
        result.andExpect(jsonPath("$.data.trainsByRoute[0].departureDate").value(today.toString()));
    }

    @Test
    public void shouldNotFindTrainsWithWrongRoute() throws Exception {
        // Create train from HKI to TPE
        factoryService.getTrainFactory().createBaseTrain(1, LocalDate.of(2024, 1, 1));

        final String query = """
            {
                trainsByRoute(
                    departureStation: "TPE"
                    arrivalStation: "HKI"
                    departureDate: "2024-01-01"
                ) {
                    trainNumber
                }
            }
            """;

        final ResultActions result = this.query(query);

        // Reverse route should return empty array (train goes HKI->TPE, not TPE->HKI)
        result.andExpect(jsonPath("$.data.trainsByRoute").isEmpty());
    }

    @Test
    public void shouldSupportStartAndEndDate() throws Exception {
        // Create a train on 2024-01-01
        factoryService.getTrainFactory().createBaseTrain(99, LocalDate.of(2024, 1, 1));

        final String query = """
            {
                trainsByRoute(
                    departureStation: "HKI"
                    arrivalStation: "TPE"
                    startDate: "2024-01-01T00:00:00Z"
                    endDate: "2024-01-02T00:00:00Z"
                ) {
                    trainNumber
                    departureDate
                }
            }
            """;

        final ResultActions result = this.query(query);

        // Should find the train using start and end date range
        result.andExpect(jsonPath("$.data.trainsByRoute[0].trainNumber").value(99));
        result.andExpect(jsonPath("$.data.trainsByRoute[0].departureDate").value("2024-01-01"));
    }

    @Test
    public void shouldRespectLimit() throws Exception {
        // Create 3 trains on the same route
        factoryService.getTrainFactory().createBaseTrain(1, LocalDate.of(2024, 1, 1));
        factoryService.getTrainFactory().createBaseTrain(2, LocalDate.of(2024, 1, 1));
        factoryService.getTrainFactory().createBaseTrain(3, LocalDate.of(2024, 1, 1));

        final String query = """
            {
                trainsByRoute(
                    departureStation: "HKI"
                    arrivalStation: "TPE"
                    departureDate: "2024-01-01"
                    limit: 1
                ) {
                    trainNumber
                }
            }
            """;

        final ResultActions result = this.query(query);

        // Should return maximum 1 train even though 3 exist
        result.andExpect(jsonPath("$.data.trainsByRoute.length()").value(1));
    }

    @Test
    public void shouldSupportIncludeNonStopping() throws Exception {
        // Create a train that can be queried with includeNonStopping
        factoryService.getTrainFactory().createBaseTrain(5, LocalDate.of(2024, 1, 1));

        final String query = """
            {
                trainsByRoute(
                    departureStation: "HKI"
                    arrivalStation: "TPE"
                    departureDate: "2024-01-01"
                    includeNonStopping: true
                ) {
                    trainNumber
                }
            }
            """;

        final ResultActions result = this.query(query);

        // Should successfully query with includeNonStopping parameter
        result.andExpect(jsonPath("$.data.trainsByRoute[0].trainNumber").value(5));
    }

    @Test
    public void shouldRejectTooLongDateRange() throws Exception {
        final String query = """
            {
                trainsByRoute(
                    departureStation: "HKI"
                    arrivalStation: "TPE"
                    startDate: "2024-01-01T00:00:00Z"
                    endDate: "2024-01-05T00:00:00Z"
                ) {
                    trainNumber
                }
            }
            """;

        final ResultActions result = this.queryAndExpectError(query);

        // Should throw error because range is > 2 days
        result.andExpect(jsonPath("$.errors").exists());
        result.andExpect(jsonPath("$.errors[0].message").exists());
    }

    @Test
    public void shouldRejectEndDateBeforeStartDate() throws Exception {
        final String query = """
            {
                trainsByRoute(
                    departureStation: "HKI"
                    arrivalStation: "TPE"
                    startDate: "2024-01-05T00:00:00Z"
                    endDate: "2024-01-01T00:00:00Z"
                ) {
                    trainNumber
                }
            }
            """;

        final ResultActions result = this.queryAndExpectError(query);

        // Should throw error because endDate < startDate
        result.andExpect(jsonPath("$.errors").exists());
        result.andExpect(jsonPath("$.errors[0].message").exists());
    }

    @Test
    public void shouldRejectEndDateWithoutStartDate() throws Exception {
        final String query = """
            {
                trainsByRoute(
                    departureStation: "HKI"
                    arrivalStation: "TPE"
                    endDate: "2024-01-01T00:00:00Z"
                ) {
                    trainNumber
                }
            }
            """;

        final ResultActions result = this.queryAndExpectError(query);

        // Should throw error because endDate provided without startDate
        result.andExpect(jsonPath("$.errors").exists());
        result.andExpect(jsonPath("$.errors[0].message").exists());
    }

    @Test
    public void shouldReturnEmptyWhenNoTrains() throws Exception {
        final String query = """
            {
                trainsByRoute(
                    departureStation: "XXX"
                    arrivalStation: "YYY"
                    departureDate: "2024-01-01"
                ) {
                    trainNumber
                }
            }
            """;

        final ResultActions result = this.query(query);

        // Should return empty array for non-existent stations
        result.andExpect(jsonPath("$.data.trainsByRoute").isEmpty());
    }
}

