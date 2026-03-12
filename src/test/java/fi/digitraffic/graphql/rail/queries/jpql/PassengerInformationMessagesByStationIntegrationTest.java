package fi.digitraffic.graphql.rail.queries.jpql;

import static fi.digitraffic.graphql.rail.util.TestDataUtils.HKI;
import static fi.digitraffic.graphql.rail.util.TestDataUtils.TPE;
import static fi.digitraffic.graphql.rail.util.TestDataUtils.dateFormat;
import static fi.digitraffic.graphql.rail.util.TestDataUtils.insertRamiMessage;
import static fi.digitraffic.graphql.rail.util.TestDataUtils.insertRamiMessageStation;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.entities.PassengerInformationMessage;
import fi.digitraffic.graphql.rail.webmvc.BaseWebMVCTest;

/**
 * Integration tests for the passengerInformationMessagesByStation query.
 */
public class PassengerInformationMessagesByStationIntegrationTest extends BaseWebMVCTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void queryByStation_returnsMessagesForStation() throws Exception {
        // Create messages with station associations using JDBC
        // (JPA insert doesn't work for rami_message_station due to insertable=false columns)
        insertRamiMessage(jdbcTemplate, "1", 1, ZonedDateTime.now().minusHours(1).format(dateFormat),
                ZonedDateTime.now().minusDays(1).format(dateFormat),
                ZonedDateTime.now().plusDays(1).format(dateFormat), null, null,
                PassengerInformationMessage.MessageType.SCHEDULED_MESSAGE.name());
        insertRamiMessage(jdbcTemplate, "2", 1, ZonedDateTime.now().minusHours(1).format(dateFormat),
                ZonedDateTime.now().minusDays(1).format(dateFormat),
                ZonedDateTime.now().plusDays(1).format(dateFormat), null, null,
                PassengerInformationMessage.MessageType.SCHEDULED_MESSAGE.name());
        insertRamiMessageStation(jdbcTemplate, "1", 1, HKI);
        insertRamiMessageStation(jdbcTemplate, "2", 1, TPE);

        final ResultActions result = query("""
                {
                  passengerInformationMessagesByStation(stationShortCode: "HKI") {
                    id
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.passengerInformationMessagesByStation.length()").value(1));
        result.andExpect(jsonPath("$.data.passengerInformationMessagesByStation[0].id").value("1"));
    }

    @Test
    public void queryByStation_onlyGeneral_filtersToScheduledMessages() throws Exception {
        insertRamiMessage(jdbcTemplate, "1", 1, ZonedDateTime.now().minusHours(1).format(dateFormat),
                ZonedDateTime.now().minusDays(1).format(dateFormat),
                ZonedDateTime.now().plusDays(1).format(dateFormat), null, null,
                PassengerInformationMessage.MessageType.SCHEDULED_MESSAGE.name());
        insertRamiMessage(jdbcTemplate, "2", 1, ZonedDateTime.now().minusHours(1).format(dateFormat),
                ZonedDateTime.now().minusDays(1).format(dateFormat),
                ZonedDateTime.now().plusDays(1).format(dateFormat), 1, "2024-01-01",
                PassengerInformationMessage.MessageType.MONITORED_JOURNEY_SCHEDULED_MESSAGE.name());
        insertRamiMessageStation(jdbcTemplate, "1", 1, HKI);
        insertRamiMessageStation(jdbcTemplate, "2", 1, HKI);

        final ResultActions result = query("""
                {
                  passengerInformationMessagesByStation(stationShortCode: "HKI", onlyGeneral: true) {
                    id
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.passengerInformationMessagesByStation.length()").value(1));
        result.andExpect(jsonPath("$.data.passengerInformationMessagesByStation[0].id").value("1"));
    }

    @Test
    public void queryByStation_excludesExpiredMessages() throws Exception {
        // Active message at HKI
        insertRamiMessage(jdbcTemplate, "1", 1, ZonedDateTime.now().minusHours(1).format(dateFormat),
                ZonedDateTime.now().minusDays(1).format(dateFormat),
                ZonedDateTime.now().plusDays(1).format(dateFormat), null, null,
                PassengerInformationMessage.MessageType.SCHEDULED_MESSAGE.name());
        // Expired message at HKI
        insertRamiMessage(jdbcTemplate, "2", 1, ZonedDateTime.now().minusHours(1).format(dateFormat),
                ZonedDateTime.now().minusDays(2).format(dateFormat),
                ZonedDateTime.now().minusDays(1).format(dateFormat), null, null,
                PassengerInformationMessage.MessageType.SCHEDULED_MESSAGE.name());
        insertRamiMessageStation(jdbcTemplate, "1", 1, HKI);
        insertRamiMessageStation(jdbcTemplate, "2", 1, HKI);

        final ResultActions result = query("""
                {
                  passengerInformationMessagesByStation(stationShortCode: "HKI") {
                    id
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.passengerInformationMessagesByStation.length()").value(1));
        result.andExpect(jsonPath("$.data.passengerInformationMessagesByStation[0].id").value("1"));
    }

    @Test
    public void queryByStation_returnsOnlyLatestVersion() throws Exception {
        insertRamiMessage(jdbcTemplate, "1", 1, ZonedDateTime.now().minusHours(2).format(dateFormat),
                ZonedDateTime.now().minusDays(1).format(dateFormat),
                ZonedDateTime.now().plusDays(1).format(dateFormat), null, null,
                PassengerInformationMessage.MessageType.SCHEDULED_MESSAGE.name());
        insertRamiMessage(jdbcTemplate, "1", 2, ZonedDateTime.now().minusHours(1).format(dateFormat),
                ZonedDateTime.now().minusDays(1).format(dateFormat),
                ZonedDateTime.now().plusDays(1).format(dateFormat), null, null,
                PassengerInformationMessage.MessageType.SCHEDULED_MESSAGE.name());
        insertRamiMessageStation(jdbcTemplate, "1", 1, HKI);
        insertRamiMessageStation(jdbcTemplate, "1", 2, HKI);

        final ResultActions result = query("""
                {
                  passengerInformationMessagesByStation(stationShortCode: "HKI") {
                    id
                    version
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.passengerInformationMessagesByStation.length()").value(1));
        result.andExpect(jsonPath("$.data.passengerInformationMessagesByStation[0].version").value(2));
    }

    @Test
    public void queryByStation_excludesDeletedMessages() throws Exception {
        // Active message at HKI
        insertRamiMessage(jdbcTemplate, "1", 1, ZonedDateTime.now().minusHours(1).format(dateFormat),
                ZonedDateTime.now().minusDays(1).format(dateFormat),
                ZonedDateTime.now().plusDays(1).format(dateFormat), null, null,
                PassengerInformationMessage.MessageType.SCHEDULED_MESSAGE.name());
        // Deleted message at HKI
        insertRamiMessage(jdbcTemplate, "2", 1, ZonedDateTime.now().minusHours(1).format(dateFormat),
                ZonedDateTime.now().minusDays(1).format(dateFormat),
                ZonedDateTime.now().plusDays(1).format(dateFormat), null, null,
                PassengerInformationMessage.MessageType.SCHEDULED_MESSAGE.name());
        jdbcTemplate.update("UPDATE rami_message SET deleted = ? WHERE id = '2' AND version = 1",
                ZonedDateTime.now().minusMinutes(5).format(dateFormat));
        insertRamiMessageStation(jdbcTemplate, "1", 1, HKI);
        insertRamiMessageStation(jdbcTemplate, "2", 1, HKI);

        final ResultActions result = query("""
                {
                  passengerInformationMessagesByStation(stationShortCode: "HKI") {
                    id
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.passengerInformationMessagesByStation.length()").value(1));
        result.andExpect(jsonPath("$.data.passengerInformationMessagesByStation[0].id").value("1"));
    }
}

