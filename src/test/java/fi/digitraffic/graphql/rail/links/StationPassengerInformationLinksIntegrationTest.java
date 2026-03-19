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

public class StationPassengerInformationLinksIntegrationTest extends BaseWebMVCTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    @Transactional
    public void setUp() {
        // Create stations
        factoryService.getStationFactory().create(HKI, 1, "FI");
        factoryService.getStationFactory().create(TPE, 2, "FI");

        // Create active messages with station associations
        insertRamiMessage(jdbcTemplate, "1", 1, "2023-01-01 00:00:00",
                ZonedDateTime.now().minusDays(1).format(dateFormat),
                ZonedDateTime.now().plusDays(1).format(dateFormat), null, null,
                PassengerInformationMessage.MessageType.SCHEDULED_MESSAGE.name());
        insertRamiMessage(jdbcTemplate, "2", 1, "2024-01-01 00:00:00",
                ZonedDateTime.now().minusDays(1).format(dateFormat),
                ZonedDateTime.now().plusDays(1).format(dateFormat), null, null,
                PassengerInformationMessage.MessageType.SCHEDULED_MESSAGE.name());
        // Inactive message — should NOT appear
        insertRamiMessage(jdbcTemplate, "3", 1, "2024-01-01 00:00:00",
                ZonedDateTime.now().plusDays(1).format(dateFormat),
                ZonedDateTime.now().plusDays(2).format(dateFormat), null, null,
                PassengerInformationMessage.MessageType.SCHEDULED_MESSAGE.name());
        insertRamiMessageStation(jdbcTemplate, "1", 1, HKI);
        insertRamiMessageStation(jdbcTemplate, "1", 1, TPE);
        insertRamiMessageStation(jdbcTemplate, "2", 1, TPE);
        insertRamiMessageStation(jdbcTemplate, "3", 1, TPE);
    }

    @Test
    public void stationToStationMessages_linkShouldWork() throws Exception {
        final ResultActions result = query("""
                {
                  stations {
                    shortCode
                    stationMessages {
                      stationShortCode
                      messageId
                    }
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.stations[?(@.shortCode=='HKI')].stationMessages.length()").value(1));
        result.andExpect(jsonPath("$.data.stations[?(@.shortCode=='TPE')].stationMessages.length()").value(2));
    }

    @Test
    public void stationToStationMessages_messageLink() throws Exception {
        final ResultActions result = query("""
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

        result.andExpect(jsonPath("$.data.stations[?(@.shortCode=='HKI')].stationMessages[0].message.id").value("1"));
    }

    @Test
    public void stationToStationMessages_stationLink() throws Exception {
        final ResultActions result = query("""
                {
                  stations {
                    shortCode
                    stationMessages {
                      station {
                        shortCode
                        name
                      }
                    }
                  }
                }
                """);

        // Each stationMessage should have a station link back
        result.andExpect(jsonPath("$.data.stations[?(@.shortCode=='HKI')].stationMessages[0].station.shortCode").value("HKI"));
    }
}

