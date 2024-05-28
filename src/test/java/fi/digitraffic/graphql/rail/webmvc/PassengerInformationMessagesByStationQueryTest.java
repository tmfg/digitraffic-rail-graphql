package fi.digitraffic.graphql.rail.webmvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.entities.PassengerInformationMessage;
import fi.digitraffic.graphql.rail.factory.PassengerInformationMessageFactory;

public class PassengerInformationMessagesByStationQueryTest extends BaseWebMVCTest {

    @Test
    public void testPassengerInformationMessagesByStationQuery() throws Exception {
        final PassengerInformationMessageFactory factory = factoryService.getPassengerInformationMessageFactory();

        final String helsinki = "HKI";
        final String tampere = "TPE";
        final List<String> stations = List.of(helsinki, tampere);

        factory.create("1", 1, ZonedDateTime.now().minusDays(1),
                ZonedDateTime.now().plusDays(1), stations, PassengerInformationMessage.MessageType.SCHEDULED_MESSAGE);
        factory.create("2", 1, ZonedDateTime.now().minusDays(1),
                ZonedDateTime.now().plusDays(1),
                LocalDate.of(2024, 1, 1), 1, stations);

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
