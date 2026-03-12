package fi.digitraffic.graphql.rail.links.jpql;

import static fi.digitraffic.graphql.rail.util.TestDataUtils.HKI;
import static fi.digitraffic.graphql.rail.util.TestDataUtils.TPE;
import static fi.digitraffic.graphql.rail.util.TestDataUtils.dateFormat;
import static fi.digitraffic.graphql.rail.util.TestDataUtils.insertRamiMessage;
import static fi.digitraffic.graphql.rail.util.TestDataUtils.insertRamiMessageStation;
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
 * Integration tests for links from PassengerInformationMessage
 * to messageStations, audio, video, and train.
 * Tests via full GraphQL HTTP requests through the JPQL query endpoints.
 */
public class PassengerInformationMessageLinksIntegrationTest extends BaseWebMVCTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void messageToMessageStations_linkShouldWork() throws Exception {
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
                    }
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.passengerInformationMessages.length()").value(1));
        result.andExpect(jsonPath("$.data.passengerInformationMessages[0].messageStations.length()").value(2));
    }

    @Test
    public void messageToAudio_linkShouldWork() throws Exception {
        insertRamiMessage(jdbcTemplate, "1", 1, ZonedDateTime.now().minusHours(1).format(dateFormat),
                ZonedDateTime.now().minusDays(1).format(dateFormat),
                ZonedDateTime.now().plusDays(1).format(dateFormat), null, null,
                PassengerInformationMessage.MessageType.SCHEDULED_MESSAGE.name());
        // Insert audio data
        jdbcTemplate.update(
                "INSERT INTO rami_message_audio (rami_message_id, rami_message_version, text_fi, text_sv, text_en, days_of_week) VALUES (?, ?, ?, ?, ?, ?)",
                "1", 1, "Huomio", "Observera", "Attention", 127);

        final ResultActions result = query("""
                {
                  passengerInformationMessages {
                    id
                    audio {
                      text {
                        fi
                        sv
                        en
                      }
                      messageId
                      messageVersion
                    }
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.passengerInformationMessages.length()").value(1));
        result.andExpect(jsonPath("$.data.passengerInformationMessages[0].audio.text.fi").value("Huomio"));
        result.andExpect(jsonPath("$.data.passengerInformationMessages[0].audio.text.sv").value("Observera"));
        result.andExpect(jsonPath("$.data.passengerInformationMessages[0].audio.text.en").value("Attention"));
        result.andExpect(jsonPath("$.data.passengerInformationMessages[0].audio.messageId").value("1"));
        result.andExpect(jsonPath("$.data.passengerInformationMessages[0].audio.messageVersion").value(1));
    }

    @Test
    public void messageToVideo_linkShouldWork() throws Exception {
        insertRamiMessage(jdbcTemplate, "1", 1, ZonedDateTime.now().minusHours(1).format(dateFormat),
                ZonedDateTime.now().minusDays(1).format(dateFormat),
                ZonedDateTime.now().plusDays(1).format(dateFormat), null, null,
                PassengerInformationMessage.MessageType.SCHEDULED_MESSAGE.name());
        // Insert video data
        jdbcTemplate.update(
                "INSERT INTO rami_message_video (rami_message_id, rami_message_version, text_fi, text_sv, text_en, days_of_week) VALUES (?, ?, ?, ?, ?, ?)",
                "1", 1, "VideoFi", "VideoSv", "VideoEn", 127);

        final ResultActions result = query("""
                {
                  passengerInformationMessages {
                    id
                    video {
                      text {
                        fi
                        sv
                        en
                      }
                      messageId
                      messageVersion
                    }
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.passengerInformationMessages.length()").value(1));
        result.andExpect(jsonPath("$.data.passengerInformationMessages[0].video.text.fi").value("VideoFi"));
        result.andExpect(jsonPath("$.data.passengerInformationMessages[0].video.text.en").value("VideoEn"));
        result.andExpect(jsonPath("$.data.passengerInformationMessages[0].video.messageId").value("1"));
    }

    @Test
    public void messageToTrain_linkShouldWork() throws Exception {
        factoryService.getTrainFactory().createBaseTrain(1, LocalDate.of(2024, 1, 1));

        factoryService.getPassengerInformationMessageFactory().create("1", 1,
                ZonedDateTime.now().minusDays(1), ZonedDateTime.now().plusDays(1),
                LocalDate.of(2024, 1, 1), 1L);

        final ResultActions result = query("""
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
        result.andExpect(jsonPath("$.data.passengerInformationMessages[0].train.trainNumber").value(1));
    }

    @Test
    public void messageWithNoAudioOrVideo_returnsNull() throws Exception {
        factoryService.getPassengerInformationMessageFactory().create("1", 1,
                ZonedDateTime.now().minusDays(1), ZonedDateTime.now().plusDays(1),
                LocalDate.of(2024, 1, 1), 1L);

        final ResultActions result = query("""
                {
                  passengerInformationMessages {
                    id
                    audio {
                      text { fi }
                    }
                    video {
                      text { fi }
                    }
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.passengerInformationMessages.length()").value(1));
        result.andExpect(jsonPath("$.data.passengerInformationMessages[0].audio").isEmpty());
        result.andExpect(jsonPath("$.data.passengerInformationMessages[0].video").isEmpty());
    }
}

