package fi.digitraffic.graphql.rail.webmvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests WHERE and ORDER BY filters that navigate the PassengerInformationMessage ↔ Train
 * JPA join path, including orphaned message edge cases.
 */
public class PassengerInformationMessageTrainFilterTest extends BaseWebMVCTest {

    @Test
    public void filterPassengerInformationMessages_byTrainNumber() throws Exception {
        factoryService.getTrainFactory().createBaseTrain(1, LocalDate.of(2024, 1, 1));
        factoryService.getPassengerInformationMessageFactory().create("1", 1,
                ZonedDateTime.now().minusDays(1), ZonedDateTime.now().plusDays(1),
                LocalDate.of(2024, 1, 1), 1L);

        // WHERE filter navigates e.train.trainNumber in JPQL
        final var result = query("""
                {
                  passengerInformationMessages(where: {
                    train: {
                      trainNumber: {
                        equals: 1
                      }
                    }
                  }) {
                    id
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.passengerInformationMessages.length()").value(1));
        result.andExpect(jsonPath("$.data.passengerInformationMessages[0].id").value("1"));
    }

    @Test
    public void filterTrainsByDepartureDate_byPassengerInformationMessages() throws Exception {
        factoryService.getTrainFactory().createBaseTrain(1, LocalDate.of(2024, 1, 1));
        factoryService.getPassengerInformationMessageFactory().create("msg1", 1,
                ZonedDateTime.now().minusDays(1), ZonedDateTime.now().plusDays(1),
                LocalDate.of(2024, 1, 1), 1L);

        // WHERE filter navigates e.passengerInformationMessages (collection) via contains subquery
        final var result = query("""
                {
                  trainsByDepartureDate(departureDate: "2024-01-01", where: {
                    passengerInformationMessages: {
                      contains: {
                        trainNumber: {
                          equals: 1
                        }
                      }
                    }
                  }) {
                    trainNumber
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(1));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].trainNumber").value(1));
    }

    @Test
    public void orphanedMessage_excludedFromWhereFilterByTrain() throws Exception {
        // Message references train (1, 2024-01-01) but no such Train entity exists
        factoryService.getPassengerInformationMessageFactory().create("orphan", 1,
                ZonedDateTime.now().minusDays(1), ZonedDateTime.now().plusDays(1),
                LocalDate.of(2024, 1, 1), 1L);

        // WHERE filter uses implicit inner join — orphaned messages are excluded
        final var result = query("""
                {
                  passengerInformationMessages(where: {
                    train: {
                      trainNumber: {
                        equals: 1
                      }
                    }
                  }) {
                    id
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.passengerInformationMessages.length()").value(0));
    }

    @Test
    public void orphanedMessage_returnedWithoutWhereFilter() throws Exception {
        // Message references train (1, 2024-01-01) but no such Train entity exists
        factoryService.getPassengerInformationMessageFactory().create("orphan", 1,
                ZonedDateTime.now().minusDays(1), ZonedDateTime.now().plusDays(1),
                LocalDate.of(2024, 1, 1), 1L);

        // Without WHERE filter on train, the orphaned message is returned normally.
        // The lazy proxy for train is never initialized — no EntityNotFoundException.
        final var result = query("""
                {
                  passengerInformationMessages {
                    id
                    trainNumber
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.passengerInformationMessages.length()").value(1));
        result.andExpect(jsonPath("$.data.passengerInformationMessages[0].id").value("orphan"));
        result.andExpect(jsonPath("$.data.passengerInformationMessages[0].trainNumber").value(1));
    }

    @Test
    public void orphanedMessage_trainLinkReturnsNull() throws Exception {
        // Message references train (1, 2024-01-01) but no such Train entity exists
        factoryService.getPassengerInformationMessageFactory().create("orphan", 1,
                ZonedDateTime.now().minusDays(1), ZonedDateTime.now().plusDays(1),
                LocalDate.of(2024, 1, 1), 1L);

        // Requesting the train link field — the DataLoader-based link resolver
        // does its own JPQL query and returns null for non-existent trains.
        // The JPA lazy proxy is never touched.
        final var result = query("""
                {
                  passengerInformationMessages {
                    id
                    train {
                      trainNumber
                    }
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.passengerInformationMessages.length()").value(1));
        result.andExpect(jsonPath("$.data.passengerInformationMessages[0].id").value("orphan"));
        result.andExpect(jsonPath("$.data.passengerInformationMessages[0].train").isEmpty());
    }

    @Test
    public void orderByTrainsByDepartureDate_byTrainNumber() throws Exception {
        factoryService.getTrainFactory().createBaseTrain(1, LocalDate.of(2024, 1, 1));
        factoryService.getTrainFactory().createBaseTrain(2, LocalDate.of(2024, 1, 1));

        // ORDER BY on a direct field confirms query infrastructure works
        final var result = query("""
                {
                  trainsByDepartureDate(departureDate: "2024-01-01", orderBy: [{ trainNumber: DESCENDING }]) {
                    trainNumber
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(2));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].trainNumber").value(2));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[1].trainNumber").value(1));
    }

    @Test
    public void orderByTrainsByDepartureDate_byOperatorShortCode() throws Exception {
        // Tests cross-entity ORDER BY: navigates e.operator.shortCode in JPQL
        factoryService.getTrainFactory().createBaseTrain(1, LocalDate.of(2024, 1, 1));
        factoryService.getTrainFactory().createBaseTrain(2, LocalDate.of(2024, 1, 1));

        final var result = query("""
                {
                  trainsByDepartureDate(departureDate: "2024-01-01", orderBy: [{ operator: { shortCode: ASCENDING } }]) {
                    trainNumber
                  }
                }
                """);

        // Just verify it doesn't error — confirms cross-entity orderBy path resolution works
        result.andExpect(jsonPath("$.data.trainsByDepartureDate").isArray());
    }
}
