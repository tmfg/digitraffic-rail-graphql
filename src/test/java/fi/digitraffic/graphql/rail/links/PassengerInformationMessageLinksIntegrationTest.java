package fi.digitraffic.graphql.rail.links;

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

    @Test
    public void fullMessageStructureIsReadable() throws Exception {
        // Exercises all fields in a single query: message → audio (all fields incl. deliveryRules)
        // + video (all fields incl. deliveryRules) + messageStations → station + train.
        factoryService.getTrainFactory().createBaseTrain(1, LocalDate.of(2024, 1, 1));
        factoryService.getStationFactory().create(HKI, 1, "FI");

        insertRamiMessage(jdbcTemplate, "1", 1, ZonedDateTime.now().minusHours(1).format(dateFormat),
                ZonedDateTime.now().minusDays(1).format(dateFormat),
                ZonedDateTime.now().plusDays(1).format(dateFormat), 1, LocalDate.of(2024, 1, 1).toString(),
                PassengerInformationMessage.MessageType.MONITORED_JOURNEY_SCHEDULED_MESSAGE.name());
        insertRamiMessageStation(jdbcTemplate, "1", 1, HKI);

        jdbcTemplate.update(
                "INSERT INTO rami_message_audio (rami_message_id, rami_message_version, text_fi, text_sv, text_en, " +
                "delivery_type, event_type, start_date_time, end_date_time, start_time, end_time, days_of_week, " +
                "delivery_at, repetitions, repeat_every) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                "1", 1, "Huomio", "Observera", "Attention",
                "ON_SCHEDULE", "ARRIVING",
                ZonedDateTime.now().minusDays(1).format(dateFormat),
                ZonedDateTime.now().plusDays(1).format(dateFormat),
                "08:00", "20:00", 127,
                ZonedDateTime.now().plusHours(1).format(dateFormat),
                3, 10);

        jdbcTemplate.update(
                "INSERT INTO rami_message_video (rami_message_id, rami_message_version, text_fi, text_sv, text_en, " +
                "delivery_type, start_date_time, end_date_time, start_time, end_time, days_of_week) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                "1", 1, "VideoFi", "VideoSv", "VideoEn",
                "WHEN",
                ZonedDateTime.now().minusDays(1).format(dateFormat),
                ZonedDateTime.now().plusDays(1).format(dateFormat),
                "08:00", "20:00", 127);

        final ResultActions result = query("""
                {
                  passengerInformationMessages {
                    id
                    version
                    creationDateTime
                    startValidity
                    endValidity
                    trainDepartureDate
                    trainNumber
                    train { trainNumber cancelled }
                    messageStations {
                      stationShortCode
                      messageId
                      messageVersion
                      station { shortCode name countryCode }
                    }
                    audio {
                      messageId
                      messageVersion
                      text { fi sv en }
                      deliveryRules {
                        deliveryType
                        eventType
                        startDateTime
                        endDateTime
                        startTime
                        endTime
                        weekDays
                        deliveryAt
                        repetitions
                        repeatEvery
                      }
                    }
                    video {
                      messageId
                      messageVersion
                      text { fi sv en }
                      deliveryRules {
                        deliveryType
                        startDateTime
                        endDateTime
                        startTime
                        endTime
                        weekDays
                      }
                    }
                  }
                }
                """);

        result.andExpect(jsonPath("$.errors").doesNotExist());
        result.andExpect(jsonPath("$.data.passengerInformationMessages.length()").value(1));
        result.andExpect(jsonPath("$.data.passengerInformationMessages[0].train.trainNumber").value(1));
        result.andExpect(jsonPath("$.data.passengerInformationMessages[0].messageStations[0].stationShortCode").value(HKI));
        result.andExpect(jsonPath("$.data.passengerInformationMessages[0].messageStations[0].station.shortCode").value(HKI));
        result.andExpect(jsonPath("$.data.passengerInformationMessages[0].audio.text.fi").value("Huomio"));
        result.andExpect(jsonPath("$.data.passengerInformationMessages[0].audio.deliveryRules.deliveryType").value("ON_SCHEDULE"));
        result.andExpect(jsonPath("$.data.passengerInformationMessages[0].audio.deliveryRules.eventType").value("ARRIVING"));
        result.andExpect(jsonPath("$.data.passengerInformationMessages[0].audio.deliveryRules.startTime").value("08:00:00"));
        result.andExpect(jsonPath("$.data.passengerInformationMessages[0].audio.deliveryRules.repetitions").value(3));
        result.andExpect(jsonPath("$.data.passengerInformationMessages[0].audio.deliveryRules.repeatEvery").value(10));
        result.andExpect(jsonPath("$.data.passengerInformationMessages[0].video.text.fi").value("VideoFi"));
        result.andExpect(jsonPath("$.data.passengerInformationMessages[0].video.deliveryRules.deliveryType").value("WHEN"));
        result.andExpect(jsonPath("$.data.passengerInformationMessages[0].video.deliveryRules.startTime").value("08:00:00"));
    }
}
