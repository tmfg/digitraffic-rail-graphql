package fi.digitraffic.graphql.rail.queries.jpql;

import static fi.digitraffic.graphql.rail.util.TestDataUtils.HKI;
import static fi.digitraffic.graphql.rail.util.TestDataUtils.TPE;
import static fi.digitraffic.graphql.rail.util.TestDataUtils.dateFormat;
import static fi.digitraffic.graphql.rail.util.TestDataUtils.insertRamiMessage;
import static fi.digitraffic.graphql.rail.util.TestDataUtils.insertRamiMessageStation;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.entities.PassengerInformationMessage;
import fi.digitraffic.graphql.rail.webmvc.BaseWebMVCTest;
import jakarta.transaction.Transactional;

/**
 * Integration tests for the JPQL PassengerInformationMessages query and its links.
 * Tests via full GraphQL HTTP requests, same pattern as existing QueryDSL tests.
 */
public class PassengerInformationMessagesIntegrationTest extends BaseWebMVCTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void basicQuery_returnsOnlyActiveMessages() throws Exception {
        // Active message
        factoryService.getPassengerInformationMessageFactory().create("1", 1,
                ZonedDateTime.now().minusDays(1), ZonedDateTime.now().plusDays(1),
                LocalDate.of(2024, 1, 1), 1L);

        // Expired message (should NOT be returned)
        factoryService.getPassengerInformationMessageFactory().create("2", 1,
                ZonedDateTime.now().minusDays(2), ZonedDateTime.now().minusDays(1),
                LocalDate.of(2024, 1, 1), 2L);

        // Future message (should NOT be returned)
        factoryService.getPassengerInformationMessageFactory().create("3", 1,
                ZonedDateTime.now().plusDays(1), ZonedDateTime.now().plusDays(2),
                LocalDate.of(2024, 1, 1), 3L);

        final ResultActions result = query("""
                {
                  passengerInformationMessages {
                    id
                    trainNumber
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.passengerInformationMessages.length()").value(1));
        result.andExpect(jsonPath("$.data.passengerInformationMessages[0].id").value("1"));
    }

    @Test
    public void basicQuery_returnsOnlyLatestVersion() throws Exception {
        // Two versions of the same message — only version 2 should be returned
        factoryService.getPassengerInformationMessageFactory().create("1", 1,
                ZonedDateTime.now().minusDays(1), ZonedDateTime.now().plusDays(1),
                LocalDate.of(2024, 1, 1), 1L);
        factoryService.getPassengerInformationMessageFactory().create("1", 2,
                ZonedDateTime.now().minusDays(1), ZonedDateTime.now().plusDays(1),
                LocalDate.of(2024, 1, 1), 1L);

        final ResultActions result = query("""
                {
                  passengerInformationMessages {
                    id
                    version
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.passengerInformationMessages.length()").value(1));
        result.andExpect(jsonPath("$.data.passengerInformationMessages[0].version").value(2));
    }

    @Test
    public void queryWithWhereFilter_trainNumber() throws Exception {
        factoryService.getPassengerInformationMessageFactory().create("1", 1,
                ZonedDateTime.now().minusDays(1), ZonedDateTime.now().plusDays(1),
                LocalDate.of(2024, 1, 1), 100L);
        factoryService.getPassengerInformationMessageFactory().create("2", 1,
                ZonedDateTime.now().minusDays(1), ZonedDateTime.now().plusDays(1),
                LocalDate.of(2024, 1, 1), 200L);
        factoryService.getPassengerInformationMessageFactory().create("3", 1,
                ZonedDateTime.now().minusDays(1), ZonedDateTime.now().plusDays(1),
                LocalDate.of(2024, 1, 1), 100L);

        final ResultActions result = query("""
                {
                  passengerInformationMessages(where: { trainNumber: { equals: 100 } }) {
                    id
                    trainNumber
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.passengerInformationMessages.length()").value(2));
    }

    @Test
    public void queryWithPagination_take() throws Exception {
        for (int i = 1; i <= 5; i++) {
            factoryService.getPassengerInformationMessageFactory().create(String.valueOf(i), 1,
                    ZonedDateTime.now().minusDays(1), ZonedDateTime.now().plusDays(1),
                    LocalDate.of(2024, 1, 1), (long) i);
        }

        final ResultActions result = query("""
                {
                  passengerInformationMessages(take: 2) {
                    id
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.passengerInformationMessages.length()").value(2));
    }

    @Test
    public void queryWithPagination_skipAndTake() throws Exception {
        for (int i = 1; i <= 5; i++) {
            factoryService.getPassengerInformationMessageFactory().create(String.valueOf(i), 1,
                    ZonedDateTime.now().minusDays(1), ZonedDateTime.now().plusDays(1),
                    LocalDate.of(2024, 1, 1), (long) i);
        }

        final ResultActions result = query("""
                {
                  passengerInformationMessages(skip: 2, take: 2) {
                    id
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.passengerInformationMessages.length()").value(2));
    }

    @Test
    public void queryWithOrderBy_trainNumberDesc() throws Exception {
        factoryService.getPassengerInformationMessageFactory().create("1", 1,
                ZonedDateTime.now().minusDays(1), ZonedDateTime.now().plusDays(1),
                LocalDate.of(2024, 1, 1), 1L);
        factoryService.getPassengerInformationMessageFactory().create("2", 1,
                ZonedDateTime.now().minusDays(1), ZonedDateTime.now().plusDays(1),
                LocalDate.of(2024, 1, 1), 2L);

        final ResultActions result = query("""
                {
                  passengerInformationMessages(orderBy: [{ trainNumber: DESCENDING }]) {
                    trainNumber
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.passengerInformationMessages[0].trainNumber").value(2));
        result.andExpect(jsonPath("$.data.passengerInformationMessages[1].trainNumber").value(1));
    }

    @Test
    public void queryWithOrFilter() throws Exception {
        factoryService.getPassengerInformationMessageFactory().create("1", 1,
                ZonedDateTime.now().minusDays(1), ZonedDateTime.now().plusDays(1),
                LocalDate.of(2024, 1, 1), 100L);
        factoryService.getPassengerInformationMessageFactory().create("2", 1,
                ZonedDateTime.now().minusDays(1), ZonedDateTime.now().plusDays(1),
                LocalDate.of(2024, 1, 1), 200L);
        factoryService.getPassengerInformationMessageFactory().create("3", 1,
                ZonedDateTime.now().minusDays(1), ZonedDateTime.now().plusDays(1),
                LocalDate.of(2024, 1, 1), 300L);

        final ResultActions result = query("""
                {
                  passengerInformationMessages(where: {
                    or: [
                      { trainNumber: { equals: 100 } },
                      { trainNumber: { equals: 300 } }
                    ]
                  }) {
                    id
                    trainNumber
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.passengerInformationMessages.length()").value(2));
    }

    @Test
    public void queryWithAndFilter() throws Exception {
        factoryService.getPassengerInformationMessageFactory().create("1", 1,
                ZonedDateTime.now().minusDays(1), ZonedDateTime.now().plusDays(1),
                LocalDate.of(2024, 1, 1), 100L);
        factoryService.getPassengerInformationMessageFactory().create("2", 1,
                ZonedDateTime.now().minusDays(1), ZonedDateTime.now().plusDays(1),
                LocalDate.of(2024, 1, 1), 200L);

        final ResultActions result = query("""
                {
                  passengerInformationMessages(where: {
                    and: [
                      { trainNumber: { greaterThan: 50 } },
                      { trainNumber: { lessThan: 150 } }
                    ]
                  }) {
                    id
                    trainNumber
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.passengerInformationMessages.length()").value(1));
        result.andExpect(jsonPath("$.data.passengerInformationMessages[0].trainNumber").value(100));
    }

    @Test
    public void deletedMessagesAreFiltered() throws Exception {
        // Create an active message
        factoryService.getPassengerInformationMessageFactory().create("1", 1,
                ZonedDateTime.now().minusDays(1), ZonedDateTime.now().plusDays(1),
                LocalDate.of(2024, 1, 1), 1L);

        // Create a deleted message via JDBC (factory doesn't support setting deleted)
        insertRamiMessage(jdbcTemplate, "2", 1, ZonedDateTime.now().minusHours(1).format(dateFormat),
                ZonedDateTime.now().minusDays(1).format(dateFormat),
                ZonedDateTime.now().plusDays(1).format(dateFormat), 2, null,
                PassengerInformationMessage.MessageType.SCHEDULED_MESSAGE.name());
        // Mark it as deleted
        jdbcTemplate.update("UPDATE rami_message SET deleted = ? WHERE id = '2' AND version = 1",
                ZonedDateTime.now().minusMinutes(5).format(dateFormat));

        final ResultActions result = query("""
                {
                  passengerInformationMessages {
                    id
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.passengerInformationMessages.length()").value(1));
        result.andExpect(jsonPath("$.data.passengerInformationMessages[0].id").value("1"));
    }

    @Test
    public void queryWithWhereFilter_dateTimeGreaterThan() throws Exception {
        // Message created 2 hours ago
        insertRamiMessage(jdbcTemplate, "1", 1,
                ZonedDateTime.now().minusHours(2).format(dateFormat),
                ZonedDateTime.now().minusDays(1).format(dateFormat),
                ZonedDateTime.now().plusDays(1).format(dateFormat), 1, null,
                PassengerInformationMessage.MessageType.SCHEDULED_MESSAGE.name());
        // Message created 10 hours ago
        insertRamiMessage(jdbcTemplate, "2", 1,
                ZonedDateTime.now().minusHours(10).format(dateFormat),
                ZonedDateTime.now().minusDays(1).format(dateFormat),
                ZonedDateTime.now().plusDays(1).format(dateFormat), 2, null,
                PassengerInformationMessage.MessageType.SCHEDULED_MESSAGE.name());

        final String fiveHoursAgo = ZonedDateTime.now().minusHours(5).toOffsetDateTime().toString();

        final ResultActions result = query("""
                {
                  passengerInformationMessages(where: { creationDateTime: { greaterThan: "%s" } }) {
                    id
                  }
                }
                """.formatted(fiveHoursAgo));

        result.andExpect(jsonPath("$.data.passengerInformationMessages.length()").value(1));
        result.andExpect(jsonPath("$.data.passengerInformationMessages[0].id").value("1"));
    }

    @Test
    public void deepNestedLinkNavigation_messageStationsToStation() throws Exception {
        factoryService.getStationFactory().create(HKI, 1, "FI");
        factoryService.getStationFactory().create(TPE, 2, "FI");

        insertRamiMessage(jdbcTemplate, "1", 1, ZonedDateTime.now().minusHours(1).format(dateFormat),
                ZonedDateTime.now().minusDays(1).format(dateFormat),
                ZonedDateTime.now().plusDays(1).format(dateFormat), null, null,
                PassengerInformationMessage.MessageType.SCHEDULED_MESSAGE.name());
        insertRamiMessageStation(jdbcTemplate, "1", 1, HKI);
        insertRamiMessageStation(jdbcTemplate, "1", 1, TPE);

        final ResultActions result = query("""
                {
                  passengerInformationMessages {
                    id
                    messageStations {
                      stationShortCode
                      station {
                        shortCode
                        name
                        countryCode
                      }
                    }
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.passengerInformationMessages.length()").value(1));
        result.andExpect(jsonPath("$.data.passengerInformationMessages[0].messageStations.length()").value(2));
        // Verify that the nested station link resolves correctly
        result.andExpect(jsonPath("$.data.passengerInformationMessages[0].messageStations[?(@.stationShortCode=='HKI')].station.countryCode").value("FI"));
    }
}

