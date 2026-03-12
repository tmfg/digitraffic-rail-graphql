package fi.digitraffic.graphql.rail.webmvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

/**
 * Integration test for StationsQuery.
 * Integration tests for the stations query.
 */
public class StationsQueryTest extends BaseWebMVCTest {

    @Nested
    class BasicQueries {

        @Test
        public void testBasicStationsQuery() throws Exception {
            factoryService.getStationFactory().create("HKI", 1, "FI");
            factoryService.getStationFactory().create("TPE", 2, "FI");
            factoryService.getStationFactory().create("TKU", 3, "FI");

            final ResultActions result = query("{ stations { shortCode, name, countryCode }}");

            result.andExpect(jsonPath("$.data.stations.length()").value(3));
        }

        @Test
        public void testEmptyResultSet() throws Exception {
            // No stations created
            final ResultActions result = query("{ stations { shortCode }}");

            result.andExpect(jsonPath("$.data.stations.length()").value(0));
        }
    }

    @Nested
    class EqualsOperator {

        @Test
        public void testWhereEqualsString() throws Exception {
            factoryService.getStationFactory().create("HKI", 1, "FI");
            factoryService.getStationFactory().create("TPE", 2, "FI");
            factoryService.getStationFactory().create("STO", 3, "SE");

            final ResultActions result = query("{ stations(where: { countryCode: { equals: \"FI\" }}) { shortCode, countryCode }}");

            result.andExpect(jsonPath("$.data.stations.length()").value(2));
            result.andExpect(jsonPath("$.data.stations[0].countryCode").value("FI"));
            result.andExpect(jsonPath("$.data.stations[1].countryCode").value("FI"));
        }

        @Test
        public void testWhereEqualsBoolean() throws Exception {
            factoryService.getStationFactory().createWithPassengerTraffic("HKI", 1, "FI", true);
            factoryService.getStationFactory().createWithPassengerTraffic("TPE", 2, "FI", true);
            factoryService.getStationFactory().createWithPassengerTraffic("XXX", 3, "FI", false);

            final ResultActions result = query("{ stations(where: { passengerTraffic: { equals: true }}) { shortCode, passengerTraffic }}");

            result.andExpect(jsonPath("$.data.stations.length()").value(2));
        }

        @Test
        public void testWhereEqualsInteger() throws Exception {
            factoryService.getStationFactory().create("HKI", 1, "FI");
            factoryService.getStationFactory().create("TPE", 2, "FI");

            final ResultActions result = query("{ stations(where: { uicCode: { equals: 1 }}) { shortCode, uicCode }}");

            result.andExpect(jsonPath("$.data.stations.length()").value(1));
            result.andExpect(jsonPath("$.data.stations[0].shortCode").value("HKI"));
        }
    }

    @Nested
    class UnequalsOperator {

        @Test
        public void testWhereUnequalsString() throws Exception {
            factoryService.getStationFactory().create("HKI", 1, "FI");
            factoryService.getStationFactory().create("TPE", 2, "FI");
            factoryService.getStationFactory().create("STO", 3, "SE");

            final ResultActions result = query("{ stations(where: { countryCode: { unequals: \"FI\" }}) { shortCode, countryCode }}");

            result.andExpect(jsonPath("$.data.stations.length()").value(1));
            result.andExpect(jsonPath("$.data.stations[0].shortCode").value("STO"));
            result.andExpect(jsonPath("$.data.stations[0].countryCode").value("SE"));
        }

        @Test
        public void testWhereUnequalsBoolean() throws Exception {
            factoryService.getStationFactory().createWithPassengerTraffic("HKI", 1, "FI", true);
            factoryService.getStationFactory().createWithPassengerTraffic("XXX", 2, "FI", false);

            final ResultActions result = query("{ stations(where: { passengerTraffic: { unequals: true }}) { shortCode }}");

            result.andExpect(jsonPath("$.data.stations.length()").value(1));
            result.andExpect(jsonPath("$.data.stations[0].shortCode").value("XXX"));
        }
    }

    @Nested
    class ComparisonOperators {

        @Test
        public void testWhereGreaterThan() throws Exception {
            factoryService.getStationFactory().create("HKI", 1, "FI");
            factoryService.getStationFactory().create("TPE", 2, "FI");
            factoryService.getStationFactory().create("TKU", 3, "FI");

            final ResultActions result = query("{ stations(where: { uicCode: { greaterThan: 1 }}) { shortCode, uicCode }}");

            result.andExpect(jsonPath("$.data.stations.length()").value(2));
        }

        @Test
        public void testWhereLessThan() throws Exception {
            factoryService.getStationFactory().create("HKI", 1, "FI");
            factoryService.getStationFactory().create("TPE", 2, "FI");
            factoryService.getStationFactory().create("TKU", 3, "FI");

            final ResultActions result = query("{ stations(where: { uicCode: { lessThan: 3 }}) { shortCode, uicCode }}");

            result.andExpect(jsonPath("$.data.stations.length()").value(2));
        }
    }

    @Nested
    class LogicalOperators {

        @Test
        public void testWhereAndOperator() throws Exception {
            factoryService.getStationFactory().createWithPassengerTraffic("HKI", 1, "FI", true);
            factoryService.getStationFactory().createWithPassengerTraffic("TPE", 2, "FI", false);
            factoryService.getStationFactory().createWithPassengerTraffic("STO", 3, "SE", true);

            final ResultActions result = query("{ stations(where: { and: [{ countryCode: { equals: \"FI\" }}, { passengerTraffic: { equals: true }}]}) { shortCode }}");

            result.andExpect(jsonPath("$.data.stations.length()").value(1));
            result.andExpect(jsonPath("$.data.stations[0].shortCode").value("HKI"));
        }

        @Test
        public void testWhereOrOperator() throws Exception {
            factoryService.getStationFactory().create("HKI", 1, "FI");
            factoryService.getStationFactory().create("TPE", 2, "FI");
            factoryService.getStationFactory().create("STO", 3, "SE");

            final ResultActions result = query("{ stations(where: { or: [{ shortCode: { equals: \"HKI\" }}, { shortCode: { equals: \"STO\" }}]}) { shortCode }}");

            result.andExpect(jsonPath("$.data.stations.length()").value(2));
        }

        @Test
        public void testWhereNestedAndOr() throws Exception {
            factoryService.getStationFactory().createWithPassengerTraffic("HKI", 1, "FI", true);
            factoryService.getStationFactory().createWithPassengerTraffic("TPE", 2, "FI", false);
            factoryService.getStationFactory().createWithPassengerTraffic("STO", 3, "SE", true);
            factoryService.getStationFactory().createWithPassengerTraffic("OSL", 4, "NO", true);

            // Find stations that: (are in FI OR SE) AND have passenger traffic
            final ResultActions result = query("{ stations(where: { and: [{ passengerTraffic: { equals: true }}, { or: [{ countryCode: { equals: \"FI\" }}, { countryCode: { equals: \"SE\" }}]}]}) { shortCode }}");

            result.andExpect(jsonPath("$.data.stations.length()").value(2));
        }

        @Test
        public void testMultipleConditionsAtSameLevel() throws Exception {
            // This tests the bug fix: multiple conditions at same level should be ANDed
            factoryService.getStationFactory().createWithPassengerTraffic("HKI", 1, "FI", true);
            factoryService.getStationFactory().createWithPassengerTraffic("TPE", 2, "FI", false);
            factoryService.getStationFactory().createWithPassengerTraffic("STO", 3, "SE", false);

            // countryCode != FI AND passengerTraffic = false → should return only STO
            // But if only first condition processed: would return STO
            // If OR instead of AND: would return TPE and STO
            final ResultActions result = query("{ stations(where: { countryCode: { unequals: \"FI\" }, passengerTraffic: { equals: false }}) { shortCode }}");

            result.andExpect(jsonPath("$.data.stations.length()").value(1));
            result.andExpect(jsonPath("$.data.stations[0].shortCode").value("STO"));
        }
    }

    @Nested
    class OrderBy {

        @Test
        public void testOrderByAscending() throws Exception {
            factoryService.getStationFactory().create("TPE", 1, "FI");
            factoryService.getStationFactory().create("HKI", 2, "FI");
            factoryService.getStationFactory().create("TKU", 3, "FI");

            final ResultActions result = query("{ stations(orderBy: [{ name: ASCENDING }]) { shortCode, name }}");

            result.andExpect(jsonPath("$.data.stations[0].name").value("HKI"));
            result.andExpect(jsonPath("$.data.stations[1].name").value("TKU"));
            result.andExpect(jsonPath("$.data.stations[2].name").value("TPE"));
        }

        @Test
        public void testOrderByDescending() throws Exception {
            factoryService.getStationFactory().create("HKI", 1, "FI");
            factoryService.getStationFactory().create("TPE", 2, "FI");
            factoryService.getStationFactory().create("TKU", 3, "FI");

            final ResultActions result = query("{ stations(orderBy: [{ name: DESCENDING }]) { shortCode, name }}");

            result.andExpect(jsonPath("$.data.stations[0].name").value("TPE"));
            result.andExpect(jsonPath("$.data.stations[1].name").value("TKU"));
            result.andExpect(jsonPath("$.data.stations[2].name").value("HKI"));
        }

        @Test
        public void testOrderByMultipleFields() throws Exception {
            factoryService.getStationFactory().create("HKI", 1, "FI");
            factoryService.getStationFactory().create("TPE", 2, "FI");
            factoryService.getStationFactory().create("STO", 3, "SE");

            final ResultActions result = query("{ stations(orderBy: [{ countryCode: ASCENDING }, { name: DESCENDING }]) { shortCode, countryCode }}");

            // FI first (ascending), then within FI: TPE before HKI (descending name)
            result.andExpect(jsonPath("$.data.stations[0].shortCode").value("TPE"));
            result.andExpect(jsonPath("$.data.stations[1].shortCode").value("HKI"));
            result.andExpect(jsonPath("$.data.stations[2].shortCode").value("STO"));
        }
    }

    @Nested
    class Pagination {

        @Test
        public void testSkip() throws Exception {
            factoryService.getStationFactory().create("HKI", 1, "FI");
            factoryService.getStationFactory().create("TPE", 2, "FI");
            factoryService.getStationFactory().create("TKU", 3, "FI");

            final ResultActions result = query("{ stations(orderBy: [{ name: ASCENDING }], skip: 1) { shortCode }}");

            result.andExpect(jsonPath("$.data.stations.length()").value(2));
            result.andExpect(jsonPath("$.data.stations[0].shortCode").value("TKU"));
        }

        @Test
        public void testTake() throws Exception {
            factoryService.getStationFactory().create("HKI", 1, "FI");
            factoryService.getStationFactory().create("TPE", 2, "FI");
            factoryService.getStationFactory().create("TKU", 3, "FI");

            final ResultActions result = query("{ stations(orderBy: [{ name: ASCENDING }], take: 2) { shortCode }}");

            result.andExpect(jsonPath("$.data.stations.length()").value(2));
        }

        @Test
        public void testSkipAndTake() throws Exception {
            factoryService.getStationFactory().create("HKI", 1, "FI");
            factoryService.getStationFactory().create("TPE", 2, "FI");
            factoryService.getStationFactory().create("TKU", 3, "FI");
            factoryService.getStationFactory().create("OUL", 4, "FI");

            final ResultActions result = query("{ stations(orderBy: [{ name: ASCENDING }], skip: 1, take: 2) { shortCode }}");

            result.andExpect(jsonPath("$.data.stations.length()").value(2));
            result.andExpect(jsonPath("$.data.stations[0].shortCode").value("OUL"));
            result.andExpect(jsonPath("$.data.stations[1].shortCode").value("TKU"));
        }

        @Test
        public void testSkipBeyondResults() throws Exception {
            factoryService.getStationFactory().create("HKI", 1, "FI");

            final ResultActions result = query("{ stations(skip: 10) { shortCode }}");

            result.andExpect(jsonPath("$.data.stations.length()").value(0));
        }
    }

    @Nested
    class CombinedOperations {

        @Test
        public void testWhereWithOrderByAndPagination() throws Exception {
            factoryService.getStationFactory().createWithPassengerTraffic("HKI", 1, "FI", true);
            factoryService.getStationFactory().createWithPassengerTraffic("TPE", 2, "FI", true);
            factoryService.getStationFactory().createWithPassengerTraffic("TKU", 3, "FI", true);
            factoryService.getStationFactory().createWithPassengerTraffic("XXX", 4, "FI", false);

            final ResultActions result = query("{ stations(where: { passengerTraffic: { equals: true }}, orderBy: [{ name: ASCENDING }], skip: 1, take: 1) { shortCode }}");

            result.andExpect(jsonPath("$.data.stations.length()").value(1));
            result.andExpect(jsonPath("$.data.stations[0].shortCode").value("TKU"));
        }
    }
}

