package fi.digitraffic.graphql.rail.queries.jpql;

import static fi.digitraffic.graphql.rail.util.TestDataUtils.dateFormat;
import static fi.digitraffic.graphql.rail.util.TestDataUtils.insertRamiMessage;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.entities.PassengerInformationMessage;

import fi.digitraffic.graphql.rail.webmvc.BaseWebMVCTest;

/**
 * Integration tests for the JPQL PassengerInformationMessagesByTrain query.
 */
public class PassengerInformationMessagesByTrainIntegrationTest extends BaseWebMVCTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void queryByTrain_returnsMessagesForTrain() throws Exception {
        factoryService.getPassengerInformationMessageFactory().create("1", 1,
                ZonedDateTime.now().minusDays(1), ZonedDateTime.now().plusDays(1),
                LocalDate.of(2024, 1, 1), 1L);
        factoryService.getPassengerInformationMessageFactory().create("2", 1,
                ZonedDateTime.now().minusDays(1), ZonedDateTime.now().plusDays(1),
                LocalDate.of(2024, 1, 1), 2L);

        final ResultActions result = query("""
                {
                  passengerInformationMessagesByTrain(trainNumber: 1, departureDate: "2024-01-01") {
                    id
                    trainNumber
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.passengerInformationMessagesByTrain.length()").value(1));
        result.andExpect(jsonPath("$.data.passengerInformationMessagesByTrain[0].trainNumber").value(1));
    }

    @Test
    public void queryByTrain_excludesExpiredMessages() throws Exception {
        // Active
        factoryService.getPassengerInformationMessageFactory().create("1", 1,
                ZonedDateTime.now().minusDays(1), ZonedDateTime.now().plusDays(1),
                LocalDate.of(2024, 1, 1), 1L);
        // Expired
        factoryService.getPassengerInformationMessageFactory().create("2", 1,
                ZonedDateTime.now().minusDays(2), ZonedDateTime.now().minusDays(1),
                LocalDate.of(2024, 1, 1), 1L);

        final ResultActions result = query("""
                {
                  passengerInformationMessagesByTrain(trainNumber: 1, departureDate: "2024-01-01") {
                    id
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.passengerInformationMessagesByTrain.length()").value(1));
        result.andExpect(jsonPath("$.data.passengerInformationMessagesByTrain[0].id").value("1"));
    }

    @Test
    public void queryByTrain_returnsOnlyLatestVersion() throws Exception {
        factoryService.getPassengerInformationMessageFactory().create("1", 1,
                ZonedDateTime.now().minusDays(1), ZonedDateTime.now().plusDays(1),
                LocalDate.of(2024, 1, 1), 1L);
        factoryService.getPassengerInformationMessageFactory().create("1", 2,
                ZonedDateTime.now().minusDays(1), ZonedDateTime.now().plusDays(1),
                LocalDate.of(2024, 1, 1), 1L);

        final ResultActions result = query("""
                {
                  passengerInformationMessagesByTrain(trainNumber: 1, departureDate: "2024-01-01") {
                    id
                    version
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.passengerInformationMessagesByTrain.length()").value(1));
        result.andExpect(jsonPath("$.data.passengerInformationMessagesByTrain[0].version").value(2));
    }

    @Test
    public void queryByTrain_differentDepartureDate_noResults() throws Exception {
        factoryService.getPassengerInformationMessageFactory().create("1", 1,
                ZonedDateTime.now().minusDays(1), ZonedDateTime.now().plusDays(1),
                LocalDate.of(2024, 1, 1), 1L);

        final ResultActions result = query("""
                {
                  passengerInformationMessagesByTrain(trainNumber: 1, departureDate: "2024-02-01") {
                    id
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.passengerInformationMessagesByTrain.length()").value(0));
    }

    @Test
    public void queryByTrain_excludesDeletedMessages() throws Exception {
        // Active, non-deleted message
        factoryService.getPassengerInformationMessageFactory().create("1", 1,
                ZonedDateTime.now().minusDays(1), ZonedDateTime.now().plusDays(1),
                LocalDate.of(2024, 1, 1), 1L);

        // Deleted message for the same train
        insertRamiMessage(jdbcTemplate, "2", 1, ZonedDateTime.now().minusHours(1).format(dateFormat),
                ZonedDateTime.now().minusDays(1).format(dateFormat),
                ZonedDateTime.now().plusDays(1).format(dateFormat), 1, "2024-01-01",
                PassengerInformationMessage.MessageType.SCHEDULED_MESSAGE.name());
        jdbcTemplate.update("UPDATE rami_message SET deleted = ? WHERE id = '2' AND version = 1",
                ZonedDateTime.now().minusMinutes(5).format(dateFormat));

        final ResultActions result = query("""
                {
                  passengerInformationMessagesByTrain(trainNumber: 1, departureDate: "2024-01-01") {
                    id
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.passengerInformationMessagesByTrain.length()").value(1));
        result.andExpect(jsonPath("$.data.passengerInformationMessagesByTrain[0].id").value("1"));
    }
}

