package fi.digitraffic.graphql.rail.webmvc;

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
import jakarta.transaction.Transactional;

public class PassengerInformationMessagesByStationQueryTest extends BaseWebMVCTest {

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
    }

    @Test
    public void testPassengerInformationMessagesByStationQuery() throws Exception {
        final ResultActions result =
                this.query("{ passengerInformationMessagesByStation(stationShortCode: \"HKI\", onlyGeneral: true) { id }}");

        // only messages of type SCHEDULED_MESSAGE should be returned when onlyGeneral is true
        result.andExpect(jsonPath("$.data.passengerInformationMessagesByStation.length()").value(1));
        result.andExpect(jsonPath("$.data.passengerInformationMessagesByStation[?(@.id==1)]").exists());

        final ResultActions secondResult =
                this.query("{ passengerInformationMessagesByStation(stationShortCode: \"HKI\", onlyGeneral: false) { id }}");
        final ResultActions thirdResult =
                this.query("{ passengerInformationMessagesByStation(stationShortCode: \"HKI\") { id }}");

        // message type does not matter if onlyGeneral is false or not given
        secondResult.andExpect(jsonPath("$.data.passengerInformationMessagesByStation.length()").value(2));
        thirdResult.andExpect(jsonPath("$.data.passengerInformationMessagesByStation.length()").value(2));
    }
}
