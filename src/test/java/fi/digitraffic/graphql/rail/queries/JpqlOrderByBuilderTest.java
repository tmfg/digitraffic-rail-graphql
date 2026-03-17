package fi.digitraffic.graphql.rail.queries;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import graphql.execution.AbortExecutionException;

/**
 * Unit tests for JpqlOrderByBuilder.
 */
class JpqlOrderByBuilderTest {

    private JpqlOrderByBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new JpqlOrderByBuilder();
    }

    @Nested
    class BasicFunctionality {

        @Test
        void simpleAscending() {
            final var result = builder.build("e", List.of(Map.of("trainNumber", "ASCENDING")));
            assertEquals("e.trainNumber ASC", result);
        }

        @Test
        void simpleDescending() {
            final var result = builder.build("e", List.of(Map.of("trainNumber", "DESCENDING")));
            assertEquals("e.trainNumber DESC", result);
        }

        @Test
        void nestedField() {
            final var result = builder.build("e", List.of(Map.of("trainType", Map.of("name", "ASCENDING"))));
            assertEquals("e.trainType.name ASC", result);
        }

        @Test
        void multipleOrderClauses() {
            final var result = builder.build("e", List.of(
                    Map.of("trainNumber", "ASCENDING"),
                    Map.of("departureDate", "DESCENDING")));
            assertEquals("e.trainNumber ASC, e.departureDate DESC", result);
        }

        @Test
        void nullReturnsEmpty() {
            assertEquals("", builder.build("e", null));
        }

        @Test
        void emptyListReturnsEmpty() {
            assertEquals("", builder.build("e", List.of()));
        }

        @Test
        void emptyMapInListIsSkipped() {
            final var result = builder.build("e", List.of(Map.of()));
            assertEquals("", result);
        }

        @Test
        void emptyMapAmongValidEntriesIsSkipped() {
            final List<Map<String, Object>> orderByList = new ArrayList<>();
            orderByList.add(Map.of("trainNumber", "ASCENDING"));
            orderByList.add(Map.of());
            orderByList.add(Map.of("departureDate", "DESCENDING"));
            final var result = builder.build("e", orderByList);
            assertEquals("e.trainNumber ASC, e.departureDate DESC", result);
        }

        @Test
        void unknownDirectionDefaultsToDesc() {
            final var result = builder.build("e", List.of(Map.of("trainNumber", "UNKNOWN")));
            assertEquals("e.trainNumber DESC", result);
        }
    }

    @Nested
    class InjectionPrevention {

        @Test
        void rejectsFieldNameWithSpace() {
            assertThrows(AbortExecutionException.class, () ->
                    builder.build("e", List.of(Map.of("field name", "ASCENDING"))));
        }

        @Test
        void rejectsFieldNameWithSemicolon() {
            assertThrows(AbortExecutionException.class, () ->
                    builder.build("e", List.of(Map.of("a; DROP TABLE x", "ASCENDING"))));
        }

        @Test
        void rejectsFieldNameWithParentheses() {
            assertThrows(AbortExecutionException.class, () ->
                    builder.build("e", List.of(Map.of("func()", "ASCENDING"))));
        }

        @Test
        void rejectsFieldNameWithDot() {
            assertThrows(AbortExecutionException.class, () ->
                    builder.build("e", List.of(Map.of("a.b", "ASCENDING"))));
        }

        @Test
        void rejectsFieldNameStartingWithDigit() {
            assertThrows(AbortExecutionException.class, () ->
                    builder.build("e", List.of(Map.of("1field", "ASCENDING"))));
        }

        @Test
        void rejectsNestedFieldNameWithInjection() {
            assertThrows(AbortExecutionException.class, () ->
                    builder.build("e", List.of(Map.of("station", Map.of("name; DELETE", "ASCENDING")))));
        }

        @Test
        void rejectsNullDirectionValue() {
            final Map<String, Object> map = new java.util.HashMap<>();
            map.put("trainNumber", null);
            assertThrows(AbortExecutionException.class, () ->
                    builder.build("e", List.of(map)));
        }

        @Test
        void rejectsNonStringDirectionValue() {
            assertThrows(AbortExecutionException.class, () ->
                    builder.build("e", List.of(Map.of("trainNumber", 42))));
        }

        @Test
        void acceptsValidFieldNames() {
            // camelCase
            assertEquals("e.trainNumber ASC", builder.build("e", List.of(Map.of("trainNumber", "ASCENDING"))));

            // with underscore
            assertEquals("e.train_number ASC", builder.build("e", List.of(Map.of("train_number", "ASCENDING"))));

            // starting with underscore
            assertEquals("e._field ASC", builder.build("e", List.of(Map.of("_field", "ASCENDING"))));
        }
    }
}

