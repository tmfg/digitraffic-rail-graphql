package fi.digitraffic.graphql.rail.webmvc;

import fi.digitraffic.graphql.rail.entities.PassengerInformationMessage;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import static fi.digitraffic.graphql.rail.util.TestDataUtils.*;
import static fi.digitraffic.graphql.rail.util.TestDataUtils.HKI;
import static fi.digitraffic.graphql.rail.util.TestDataUtils.TPE;
import static fi.digitraffic.graphql.rail.util.TestDataUtils.insertRamiMessageStation;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class PassangerInformationMessagesByTextTest extends BaseWebMVCTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    @Transactional
    public void setUp() {
        // cannot use the factory classes to insert rami message station data via JPA repository, because
        // fields from columns rami_message_id and rami_message_version must have "insertable = false" in the JPA entity PassengerInformationMessageStation
        insertRamiMessage(jdbcTemplate, "1", 1, "2023-01-01 00:00:00", ZonedDateTime.now().minusDays(1).format(dateFormat),
                ZonedDateTime.now().plusDays(1).format(dateFormat), null, null, PassengerInformationMessage.MessageType.SCHEDULED_MESSAGE.name());
        insertRamiMessage(jdbcTemplate, "2", 1, "2024-01-01 00:00:00", ZonedDateTime.now().minusDays(1).format(dateFormat),
                ZonedDateTime.now().plusDays(1).format(dateFormat), 1, LocalDate.of(2024, 1, 1).toString(),
                PassengerInformationMessage.MessageType.MONITORED_JOURNEY_SCHEDULED_MESSAGE.name());
        insertRamiMessageStation(jdbcTemplate, "1", 1, HKI);
        insertRamiMessageStation(jdbcTemplate, "1", 1, TPE);
        insertRamiMessageStation(jdbcTemplate, "2", 1, HKI);
        insertRamiMessageStation(jdbcTemplate, "2", 1, TPE);

        jdbcTemplate.update(
                "INSERT INTO rami_message_video (rami_message_id, rami_message_version, text_fi, text_sv, text_en, days_of_week) VALUES (?, ?, ?, ?, ?, ?)",
                "1", 1, "moi", "hej", "hi", 127);
        jdbcTemplate.update(
                "INSERT INTO rami_message_video (rami_message_id, rami_message_version, text_fi, text_sv, text_en, days_of_week) VALUES (?, ?, ?, ?, ?, ?)",
                "2", 1, "moi", "hej", "hi", 127);
    }

    @Test
    public void testPassengerInformationMessagesByTextQuery() throws Exception {
        final ResultActions result =
                this.query("""
                                {
                                  passengerInformationMessages(where:  {
                                     video:  {
                                        text:  {
                                           fi:  {
                                              equals: "moi"
                                           }
                                        }
                                     }
                                  }) {
                                    id
                                  }
                                }""");

        result.andExpect(jsonPath("$.data.passengerInformationMessages.length()").value(2));
    }
}
