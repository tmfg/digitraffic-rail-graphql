package fi.digitraffic.graphql.rail.links;

import static fi.digitraffic.graphql.rail.util.TestDataUtils.HKI;
import static fi.digitraffic.graphql.rail.util.TestDataUtils.TPE;
import static fi.digitraffic.graphql.rail.util.TestDataUtils.dateFormat;
import static fi.digitraffic.graphql.rail.util.TestDataUtils.insertRamiMessage;
import static fi.digitraffic.graphql.rail.util.TestDataUtils.insertRamiMessageStation;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.entities.PassengerInformationMessage;
import fi.digitraffic.graphql.rail.webmvc.BaseWebMVCTest;
import jakarta.transaction.Transactional;

public class StationToPassengerInformationMessagesLinkTest extends

        BaseWebMVCTest {

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
                ZonedDateTime.now().plusDays(1).format(dateFormat), null, null, PassengerInformationMessage.MessageType.SCHEDULED_MESSAGE.name());
        // inactive message - shouldn't be returned
        insertRamiMessage(jdbcTemplate, "3", 1, "2024-01-01 00:00:00", ZonedDateTime.now().plusDays(1).format(dateFormat),
                ZonedDateTime.now().plusDays(2).format(dateFormat), null, null, PassengerInformationMessage.MessageType.SCHEDULED_MESSAGE.name());
        insertRamiMessageStation(jdbcTemplate, "1", 1, HKI);
        insertRamiMessageStation(jdbcTemplate, "1", 1, TPE);
        insertRamiMessageStation(jdbcTemplate, "2", 1, TPE);
        insertRamiMessageStation(jdbcTemplate, "3", 1, TPE);
    }

    @Test
    public void linkShouldWork() throws Exception {

        factoryService.getStationFactory().create(HKI, 1, "FI");
        factoryService.getStationFactory().create(TPE, 2, "FI");

        final ResultActions result = this.query("""
                {
                  stations {
                    shortCode             
                    stationMessages {
                      message {
                        id
                      }
                    }
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.stations[?(@.shortCode=='HKI')].stationMessages.length()").value(1));
        result.andExpect(jsonPath("$.data.stations[?(@.shortCode=='TPE')].stationMessages.length()").value(2));
        result.andExpect(jsonPath("$.data.stations[?(@.shortCode=='HKI')].stationMessages[?(@.message.id==1)]").exists());
        result.andExpect(jsonPath("$.data.stations[?(@.shortCode=='TPE')].stationMessages[?(@.message.id==1)]").exists());
        result.andExpect(jsonPath("$.data.stations[?(@.shortCode=='TPE')].stationMessages[?(@.message.id==2)]").exists());
    }
}
