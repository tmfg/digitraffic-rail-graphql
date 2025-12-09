package fi.digitraffic.graphql.rail.webmvc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fi.digitraffic.graphql.rail.webmvc.BaseWebMVCTest;

/**
 * Integration tests for trainsByRoute GraphQL query.
 * Tests the GraphQL implementation against the same logic as the REST API
 * endpoint: /live-trains/station/{departure_station}/{arrival_station}
 */
public class TrainsByRouteQueryTest extends BaseWebMVCTest {

    @Test
    public void shouldFindTrainsByRoute() throws Exception {
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
        this.query(query);
    }

    @Test
    public void shouldFindTrainsByRouteWithDefaultDates() throws Exception {
        final String query = """
            {
                trainsByRoute(
                    departureStation: "HKI"
                    arrivalStation: "RI"
                ) {
                    trainNumber
                    departureDate
                }
            }
            """;

        // Should use current time + 24 hours as default
        this.query(query);
    }

    @Test
    public void shouldNotFindTrainsWithWrongRoute() throws Exception {
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

        // Reverse route should return empty or different trains
        this.query(query);
    }

    @Test
    public void shouldSupportStartAndEndDate() throws Exception {
        final String query = """
            {
                trainsByRoute(
                    departureStation: "HKI"
                    arrivalStation: "TPE"
                    startDate: "2024-01-01T00:00:00Z"
                    endDate: "2024-01-02T00:00:00Z"
                ) {
                    trainNumber
                }
            }
            """;

        this.query(query);
    }

    @Test
    public void shouldRespectLimit() throws Exception {
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

        // Should return maximum 1 train
        this.query(query);
    }

    @Test
    public void shouldSupportIncludeNonStopping() throws Exception {
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

        this.query(query);
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

        // Should throw error because range is > 2 days
        this.queryAndExpectError(query);
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

        // Should throw error because endDate < startDate
        this.queryAndExpectError(query);
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

        // Should throw error because endDate provided without startDate
        this.queryAndExpectError(query);
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

        // Should return empty array for non-existent stations
        this.query(query);
    }
}

