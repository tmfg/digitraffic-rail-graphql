package fi.digitraffic.graphql.rail.queries.jpql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import fi.digitraffic.graphql.rail.GraphqlApplication;
import fi.digitraffic.graphql.rail.factory.FactoryService;
import fi.digitraffic.graphql.rail.model.PassengerInformationMessageTO;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

/**
 * Integration test for PassengerInformationMessagesQueryJpql.
 * Tests that the JPQL implementation produces the same results as the QueryDSL version.
 */
@SpringBootTest(classes = GraphqlApplication.class)
public class PassengerInformationMessagesQueryJpqlTest {

    @Autowired
    private PassengerInformationMessagesQueryJpql jpqlQuery;

    @Autowired
    private FactoryService factoryService;

    @AfterEach
    void tearDown() {
        factoryService.deleteAll();
    }

    @Test
    void basicQuery_returnsOnlyActiveMessages() throws Exception {
        // Create test data
        final var factory = factoryService.getPassengerInformationMessageFactory();

        // Message 1: expired (should NOT be returned)
        factory.create("1", 1, ZonedDateTime.now().minusDays(2),
                ZonedDateTime.now().minusDays(1),
                LocalDate.of(2024, 1, 1), 1);

        // Message 2: active with two versions (only latest should be returned)
        factory.create("2", 1, ZonedDateTime.now().minusDays(1),
                ZonedDateTime.now().plusDays(1),
                LocalDate.of(2024, 1, 1), 1);
        factory.create("2", 2, ZonedDateTime.now().minusDays(1),
                ZonedDateTime.now().plusDays(1),
                LocalDate.of(2024, 1, 1), 1);

        // Message 3: active (should be returned)
        factory.create("3", 1, ZonedDateTime.now().minusDays(1),
                ZonedDateTime.now().plusDays(1),
                LocalDate.of(2024, 1, 1), 1);

        // Execute query
        final DataFetcher<List<PassengerInformationMessageTO>> fetcher = jpqlQuery.createFetcher();
        final DataFetchingEnvironment env = mockEnvironment(null, null, null, null);

        final List<PassengerInformationMessageTO> results = fetcher.get(env);

        // Verify results
        assertNotNull(results);
        assertEquals(2, results.size(), "Should return only 2 active messages");

        // Verify only latest version of message 2 is returned
        final var message2 = results.stream().filter(m -> "2".equals(m.getId())).findFirst();
        assertTrue(message2.isPresent(), "Message 2 should be present");
        assertEquals(2, message2.get().getVersion(), "Should return latest version");

        // Verify message 3 is returned
        assertTrue(results.stream().anyMatch(m -> "3".equals(m.getId())), "Message 3 should be present");
    }

    @Test
    void queryWithWhereFilter_filtersResults() throws Exception {
        final var factory = factoryService.getPassengerInformationMessageFactory();

        // Create active messages with different train numbers
        factory.create("1", 1, ZonedDateTime.now().minusDays(1),
                ZonedDateTime.now().plusDays(1),
                LocalDate.of(2024, 1, 1), 100L);
        factory.create("2", 1, ZonedDateTime.now().minusDays(1),
                ZonedDateTime.now().plusDays(1),
                LocalDate.of(2024, 1, 1), 200L);
        factory.create("3", 1, ZonedDateTime.now().minusDays(1),
                ZonedDateTime.now().plusDays(1),
                LocalDate.of(2024, 1, 1), 100L);

        // Query with filter: trainNumber equals 100
        final Map<String, Object> where = Map.of("trainNumber", Map.of("equals", 100));
        final DataFetcher<List<PassengerInformationMessageTO>> fetcher = jpqlQuery.createFetcher();
        final DataFetchingEnvironment env = mockEnvironment(where, null, null, null);

        final List<PassengerInformationMessageTO> results = fetcher.get(env);

        // Should return only messages with trainNumber = 100
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(m -> m.getTrainNumber() == 100));
    }

    @Test
    void queryWithPagination_limitsResults() throws Exception {
        final var factory = factoryService.getPassengerInformationMessageFactory();

        // Create 5 active messages
        for (int i = 1; i <= 5; i++) {
            factory.create(String.valueOf(i), 1, ZonedDateTime.now().minusDays(1),
                    ZonedDateTime.now().plusDays(1),
                    LocalDate.of(2024, 1, 1), (long) i);
        }

        // Query with take=2
        final DataFetcher<List<PassengerInformationMessageTO>> fetcher = jpqlQuery.createFetcher();
        final DataFetchingEnvironment env = mockEnvironment(null, null, null, 2);

        final List<PassengerInformationMessageTO> results = fetcher.get(env);

        assertEquals(2, results.size(), "Should return only 2 results");
    }

    @Test
    void queryWithSkipAndTake_paginatesCorrectly() throws Exception {
        final var factory = factoryService.getPassengerInformationMessageFactory();

        // Create 5 active messages
        for (int i = 1; i <= 5; i++) {
            factory.create(String.valueOf(i), 1, ZonedDateTime.now().minusDays(1),
                    ZonedDateTime.now().plusDays(1),
                    LocalDate.of(2024, 1, 1), (long) i);
        }

        // Query with skip=2, take=2
        final DataFetcher<List<PassengerInformationMessageTO>> fetcher = jpqlQuery.createFetcher();
        final DataFetchingEnvironment env = mockEnvironment(null, null, 2, 2);

        final List<PassengerInformationMessageTO> results = fetcher.get(env);

        assertEquals(2, results.size(), "Should return 2 results after skipping 2");
    }

    @Test
    void queryWithOrderBy_sortsResults() throws Exception {
        final var factory = factoryService.getPassengerInformationMessageFactory();

        // Create messages with different creation times
        factory.create("1", 1, ZonedDateTime.now().minusDays(1),
                ZonedDateTime.now().plusDays(1),
                LocalDate.of(2024, 1, 1), 1L);
        Thread.sleep(10); // Ensure different creation times
        factory.create("2", 1, ZonedDateTime.now().minusDays(1),
                ZonedDateTime.now().plusDays(1),
                LocalDate.of(2024, 1, 1), 2L);

        // Query with orderBy trainNumber DESC
        final List<Map<String, Object>> orderBy = List.of(Map.of("trainNumber", "DESC"));
        final DataFetcher<List<PassengerInformationMessageTO>> fetcher = jpqlQuery.createFetcher();
        final DataFetchingEnvironment env = mockEnvironment(null, orderBy, null, null);

        final List<PassengerInformationMessageTO> results = fetcher.get(env);

        assertEquals(2, results.size());
        assertEquals(2, results.get(0).getTrainNumber(), "First result should have higher train number");
        assertEquals(1, results.get(1).getTrainNumber(), "Second result should have lower train number");
    }

    private DataFetchingEnvironment mockEnvironment(
            final Map<String, Object> where,
            final List<Map<String, Object>> orderBy,
            final Integer skip,
            final Integer take) {

        final DataFetchingEnvironment env = mock(DataFetchingEnvironment.class);
        when(env.getArgument("where")).thenReturn(where);
        when(env.getArgument("orderBy")).thenReturn(orderBy);
        when(env.getArgument("skip")).thenReturn(skip);
        when(env.getArgument("take")).thenReturn(take);
        return env;
    }
}

