package fi.digitraffic.graphql.rail.query;

import static org.junit.jupiter.api.Assertions.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import fi.digitraffic.graphql.rail.queries.EnumConverter;
import fi.digitraffic.graphql.rail.queries.JpqlWhereBuilder;
import graphql.execution.AbortExecutionException;

/**
 * Unit tests for JpqlWhereBuilder.
 * Validates JPQL generation matches expected output for all operators.
 */
class JpqlWhereBuilderTest {

    private JpqlWhereBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new JpqlWhereBuilder();
    }

    @Nested
    class EqualsOperator {

        @Test
        void simpleEquals() {
            final var result = builder.build("e", Map.of("trainNumber", Map.of("equals", 123)));

            assertEquals("e.trainNumber = :p0", result.jpql());
            assertEquals(123, result.params().get("p0"));
        }

        @Test
        void equalsNull() {
            final var innerMap = new HashMap<String, Object>();
            innerMap.put("equals", null);
            final var result = builder.build("e", Map.of("trainNumber", innerMap));

            assertEquals("e.trainNumber IS NULL", result.jpql());
            assertTrue(result.params().isEmpty());
        }

        @Test
        void equalsString() {
            final var result = builder.build("e", Map.of("name", Map.of("equals", "Helsinki")));

            assertEquals("e.name = :p0", result.jpql());
            assertEquals("Helsinki", result.params().get("p0"));
        }

        @Test
        void nestedFieldEquals() {
            final var result = builder.build("e", Map.of("station", Map.of("shortCode", Map.of("equals", "HKI"))));

            assertEquals("e.station.shortCode = :p0", result.jpql());
            assertEquals("HKI", result.params().get("p0"));
        }

        @Test
        void deeplyNestedEquals() {
            final var result = builder.build("e", Map.of(
                    "timeTableRows", Map.of(
                            "station", Map.of(
                                    "name", Map.of("equals", "Helsinki")))));

            assertEquals("e.timeTableRows.station.name = :p0", result.jpql());
            assertEquals("Helsinki", result.params().get("p0"));
        }
    }

    @Nested
    class UnequalsOperator {

        @Test
        void simpleUnequals() {
            final var result = builder.build("e", Map.of("cancelled", Map.of("unequals", true)));

            assertEquals("e.cancelled <> :p0", result.jpql());
            assertEquals(true, result.params().get("p0"));
        }

        @Test
        void unequalsNull() {
            final var innerMap = new HashMap<String, Object>();
            innerMap.put("unequals", null);
            final var result = builder.build("e", Map.of("deleted", innerMap));

            assertEquals("e.deleted IS NOT NULL", result.jpql());
            assertTrue(result.params().isEmpty());
        }
    }

    @Nested
    class ComparisonOperators {

        @Test
        void greaterThan() {
            final var result = builder.build("e", Map.of("trainNumber", Map.of("greaterThan", 100)));

            assertEquals("e.trainNumber > :p0", result.jpql());
            assertEquals(100, result.params().get("p0"));
        }

        @Test
        void greaterThanWithNullThrows() {
            final var innerMap = new HashMap<String, Object>();
            innerMap.put("greaterThan", null);

            assertThrows(AbortExecutionException.class, () ->
                    builder.build("e", Map.of("trainNumber", innerMap)));
        }

        @Test
        void lessThan() {
            final var result = builder.build("e", Map.of("trainNumber", Map.of("lessThan", 500)));

            assertEquals("e.trainNumber < :p0", result.jpql());
            assertEquals(500, result.params().get("p0"));
        }

        @Test
        void lessThanWithNullThrows() {
            final var innerMap = new HashMap<String, Object>();
            innerMap.put("lessThan", null);

            assertThrows(AbortExecutionException.class, () ->
                    builder.build("e", Map.of("trainNumber", innerMap)));
        }

        @Test
        void greaterThanWithDateTime() {
            final var dateTime = OffsetDateTime.of(2026, 3, 1, 10, 0, 0, 0, ZoneOffset.UTC);
            final var result = builder.build("e", Map.of("departureDate", Map.of("greaterThan", dateTime)));

            assertEquals("e.departureDate > :p0", result.jpql());
            assertEquals(dateTime.toZonedDateTime(), result.params().get("p0"));
        }
    }

    @Nested
    class LogicalOperators {

        @Test
        void andWithTwoConditions() {
            final var result = builder.build("e", Map.of(
                    "and", List.of(
                            Map.of("trainNumber", Map.of("equals", 123)),
                            Map.of("cancelled", Map.of("equals", false))
                    )));

            assertEquals("(e.trainNumber = :p0 AND e.cancelled = :p1)", result.jpql());
            assertEquals(123, result.params().get("p0"));
            assertEquals(false, result.params().get("p1"));
        }

        @Test
        void orWithTwoConditions() {
            final var result = builder.build("e", Map.of(
                    "or", List.of(
                            Map.of("trainNumber", Map.of("equals", 1)),
                            Map.of("trainNumber", Map.of("equals", 2))
                    )));

            assertEquals("(e.trainNumber = :p0 OR e.trainNumber = :p1)", result.jpql());
            assertEquals(1, result.params().get("p0"));
            assertEquals(2, result.params().get("p1"));
        }

        @Test
        void nestedAndOr() {
            final var result = builder.build("e", Map.of(
                    "and", List.of(
                            Map.of("cancelled", Map.of("equals", false)),
                            Map.of("or", List.of(
                                    Map.of("trainNumber", Map.of("equals", 1)),
                                    Map.of("trainNumber", Map.of("equals", 2))
                            ))
                    )));

            assertEquals("(e.cancelled = :p0 AND (e.trainNumber = :p1 OR e.trainNumber = :p2))", result.jpql());
        }

        @Test
        void emptyAndReturnsEmpty() {
            final var result = builder.build("e", Map.of("and", List.of()));

            assertEquals("", result.jpql());
        }

        @Test
        void emptyOrReturnsEmpty() {
            final var result = builder.build("e", Map.of("or", List.of()));

            assertEquals("", result.jpql());
        }
    }

    @Nested
    class MultipleConditionsAtSameLevel {

        @Test
        void twoFieldsAtSameLevelAreAndedTogether() {
            // This is the key test: {countryCode: {unequals: "FI"}, passengerTraffic: {equals: false}}
            // Should produce: (e.countryCode <> :p0 AND e.passengerTraffic = :p1)
            final var where = new HashMap<String, Object>();
            where.put("countryCode", Map.of("unequals", "FI"));
            where.put("passengerTraffic", Map.of("equals", false));

            final var result = builder.build("e", where);

            // Both conditions should be present
            assertTrue(result.jpql().contains("e.countryCode <> :p"), "Should contain countryCode condition");
            assertTrue(result.jpql().contains("e.passengerTraffic = :p"), "Should contain passengerTraffic condition");
            // They should be ANDed together
            assertTrue(result.jpql().contains(" AND "), "Conditions should be ANDed together");
            // Should be wrapped in parentheses
            assertTrue(result.jpql().startsWith("(") && result.jpql().endsWith(")"), "Should be wrapped in parentheses");
            // Should have 2 parameters
            assertEquals(2, result.params().size());
        }

        @Test
        void threeFieldsAtSameLevelAreAllAndedTogether() {
            final var where = new HashMap<String, Object>();
            where.put("field1", Map.of("equals", "a"));
            where.put("field2", Map.of("equals", "b"));
            where.put("field3", Map.of("equals", "c"));

            final var result = builder.build("e", where);

            assertTrue(result.jpql().contains("e.field1 = :p"));
            assertTrue(result.jpql().contains("e.field2 = :p"));
            assertTrue(result.jpql().contains("e.field3 = :p"));
            // Split on " AND " to verify 3 conditions joined by 2 ANDs
            final String[] parts = result.jpql().split(" AND ");
            assertEquals(3, parts.length, "Should have 3 conditions joined by 2 ANDs");
            assertEquals(3, result.params().size());
        }

        @Test
        void singleConditionNotWrappedInParentheses() {
            final var result = builder.build("e", Map.of("field", Map.of("equals", "value")));

            assertEquals("e.field = :p0", result.jpql());
            assertFalse(result.jpql().startsWith("("), "Single condition should not be wrapped");
        }

        @Test
        void nestedObjectWithMultipleFields() {
            // {station: {name: {equals: "Helsinki"}, countryCode: {equals: "FI"}}}
            final var stationConditions = new HashMap<String, Object>();
            stationConditions.put("name", Map.of("equals", "Helsinki"));
            stationConditions.put("countryCode", Map.of("equals", "FI"));

            final var result = builder.build("e", Map.of("station", stationConditions));

            assertTrue(result.jpql().contains("e.station.name = :p"));
            assertTrue(result.jpql().contains("e.station.countryCode = :p"));
            assertTrue(result.jpql().contains(" AND "));
            assertEquals(2, result.params().size());
        }
    }

    @Nested
    class ContainsOperator {

        @Test
        void containsSimple() {
            final var result = builder.build("e", Map.of(
                    "timeTableRows", Map.of(
                            "contains", Map.of(
                                    "station", Map.of(
                                            "shortCode", Map.of("equals", "HKI"))))));

            // Contains generates an EXISTS subquery for collection filtering
            assertEquals("EXISTS (SELECT sub0 FROM e.timeTableRows sub0 WHERE sub0.station.shortCode = :p0)", result.jpql());
            assertEquals("HKI", result.params().get("p0"));
        }

        @Test
        void containsWithMultipleConditions() {
            final var result = builder.build("e", Map.of(
                    "timeTableRows", Map.of(
                            "contains", Map.of(
                                    "and", List.of(
                                            Map.of("station", Map.of("shortCode", Map.of("equals", "HKI"))),
                                            Map.of("type", Map.of("equals", "DEPARTURE"))
                                    )))));

            // Should generate EXISTS subquery with AND
            assertTrue(result.jpql().contains("EXISTS (SELECT sub0 FROM e.timeTableRows sub0 WHERE"));
            assertTrue(result.jpql().contains("sub0.station.shortCode = :p0"));
            assertTrue(result.jpql().contains("sub0.type = :p1"));
            assertTrue(result.jpql().contains(" AND "));
            assertEquals(2, result.params().size());
    }
    }

    @Nested
    class InsideOperator {

        @Test
        void insideBoundingBox() {
            final var result = builder.build("e", Map.of(
                    "location", Map.of(
                            "inside", List.of(24.0, 60.0, 25.0, 61.0))));

            assertTrue(result.jpql().contains("e.location.x >= :p0"));
            assertTrue(result.jpql().contains("e.location.y >= :p1"));
            assertTrue(result.jpql().contains("e.location.x <= :p2"));
            assertTrue(result.jpql().contains("e.location.y <= :p3"));
            assertEquals(24.0, result.params().get("p0"));
            assertEquals(60.0, result.params().get("p1"));
            assertEquals(25.0, result.params().get("p2"));
            assertEquals(61.0, result.params().get("p3"));
        }

        @Test
        void insideWithNullCoordsThrows() {
            final var where = new HashMap<String, Object>();
            where.put("location", new HashMap<>(Map.of("inside", "placeholder")));
            // Replace with null via a mutable map
            ((Map<String, Object>) where.get("location")).put("inside", null);

            assertThrows(AbortExecutionException.class, () -> builder.build("e", where));
        }

        @Test
        void insideWithEmptyListThrows() {
            assertThrows(AbortExecutionException.class, () ->
                    builder.build("e", Map.of("location", Map.of("inside", List.of()))));
        }

        @Test
        void insideWithTooFewCoordsThrows() {
            assertThrows(AbortExecutionException.class, () ->
                    builder.build("e", Map.of("location", Map.of("inside", List.of(24.0, 60.0)))));
        }

        @Test
        void insideWithTooManyCoordsThrows() {
            assertThrows(AbortExecutionException.class, () ->
                    builder.build("e", Map.of("location", Map.of(
                            "inside", List.of(24.0, 60.0, 25.0, 61.0, 26.0, 62.0)))));
        }
    }

    @Nested
    class EdgeCases {

        @Test
        void nullWhereReturnsEmpty() {
            final var result = builder.build("e", null);

            assertEquals("", result.jpql());
            assertTrue(result.params().isEmpty());
        }

        @Test
        void emptyWhereReturnsEmpty() {
            final var result = builder.build("e", Map.of());

            assertEquals("", result.jpql());
            assertTrue(result.params().isEmpty());
        }

        @Test
        void invalidExpressionThrows() {
            assertThrows(AbortExecutionException.class, () ->
                    builder.build("e", Map.of("field", "not a map")));
        }

        @Test
        void emptyNestedMapThrows() {
            assertThrows(AbortExecutionException.class, () ->
                    builder.build("e", Map.of("field", Map.of())));
        }

        @Test
        void multipleParamsGetUniqueNames() {
            final var result = builder.build("e", Map.of(
                    "and", List.of(
                            Map.of("a", Map.of("equals", 1)),
                            Map.of("b", Map.of("equals", 2)),
                            Map.of("c", Map.of("equals", 3)),
                            Map.of("d", Map.of("equals", 4))
                    )));

            assertEquals(4, result.params().size());
            assertTrue(result.params().containsKey("p0"));
            assertTrue(result.params().containsKey("p1"));
            assertTrue(result.params().containsKey("p2"));
            assertTrue(result.params().containsKey("p3"));
        }
    }

    @Nested
    class InjectionPrevention {

        @Test
        void rejectsFieldNameWithSpace() {
            assertThrows(AbortExecutionException.class, () ->
                    builder.build("e", Map.of("field name", Map.of("equals", 1))));
        }

        @Test
        void rejectsFieldNameWithSemicolon() {
            assertThrows(AbortExecutionException.class, () ->
                    builder.build("e", Map.of("a; DROP TABLE", Map.of("equals", 1))));
        }

        @Test
        void rejectsFieldNameWithParentheses() {
            assertThrows(AbortExecutionException.class, () ->
                    builder.build("e", Map.of("func()", Map.of("equals", 1))));
        }

        @Test
        void rejectsFieldNameWithDot() {
            // Dots are used internally for path building, but individual keys must not contain them
            assertThrows(AbortExecutionException.class, () ->
                    builder.build("e", Map.of("a.b", Map.of("equals", 1))));
        }

        @Test
        void rejectsFieldNameStartingWithDigit() {
            assertThrows(AbortExecutionException.class, () ->
                    builder.build("e", Map.of("1field", Map.of("equals", 1))));
        }

        @Test
        void rejectsFieldNameWithJpqlKeywordInjection() {
            assertThrows(AbortExecutionException.class, () ->
                    builder.build("e", Map.of("x OR 1=1 --", Map.of("equals", 1))));
        }

        @Test
        void acceptsValidFieldNames() {
            // camelCase
            final var result1 = builder.build("e", Map.of("trainNumber", Map.of("equals", 1)));
            assertTrue(result1.jpql().contains("e.trainNumber"));

            // with underscore
            final var result2 = builder.build("e", Map.of("train_number", Map.of("equals", 1)));
            assertTrue(result2.jpql().contains("e.train_number"));

            // starting with underscore
            final var result3 = builder.build("e", Map.of("_field", Map.of("equals", 1)));
            assertTrue(result3.jpql().contains("e._field"));
        }

        @Test
        void containsNestedInContains() {
            final var result = builder.build("e", Map.of(
                    "timeTableRows", Map.of(
                            "contains", Map.of(
                                    "causes", Map.of(
                                            "contains", Map.of(
                                                    "categoryCodeOid", Map.of("equals", "B")))))));

            // Outer: sub0 (aliasCounter 0), inner: sub1 (aliasCounter 1), param: p0 (paramCounter 0)
            assertTrue(result.jpql().contains("EXISTS (SELECT sub0 FROM e.timeTableRows sub0 WHERE"));
            assertTrue(result.jpql().contains("EXISTS (SELECT sub1 FROM sub0.causes sub1 WHERE"));
            assertTrue(result.jpql().contains("sub1.categoryCodeOid = :p0"));
            assertEquals("B", result.params().get("p0"));
            assertEquals(1, result.params().size());
        }

        @Test
        void rejectsNestedFieldNameWithInjection() {
            assertThrows(AbortExecutionException.class, () ->
                    builder.build("e", Map.of(
                            "station", Map.of(
                                    "name; DELETE FROM", Map.of("equals", "x")))));
        }
    }

    @Nested
    class WithEnumConverter {

        @Test
        void convertsEnumValues() {
            final var enumConverter = new EnumConverter();
            enumConverter.setup();
            final var builderWithEnum = new JpqlWhereBuilder(enumConverter);

            final var result = builderWithEnum.build("e", Map.of(
                    "timetableType", Map.of("equals", "REGULAR")));

            assertEquals("e.timetableType = :p0", result.jpql());
            // EnumConverter should have converted the string to enum
            assertNotNull(result.params().get("p0"));
        }
    }
}






