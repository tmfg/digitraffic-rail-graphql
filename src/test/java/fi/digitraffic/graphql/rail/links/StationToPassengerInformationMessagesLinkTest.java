package fi.digitraffic.graphql.rail.links;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.webmvc.BaseWebMVCTest;

public class StationToPassengerInformationMessagesLinkTest extends BaseWebMVCTest {

    @Test
    public void linkShouldWork() throws Exception {

        final String helsinki = "HKI";
        final String tampere = "TPE";

        factoryService.getStationFactory().create(helsinki, 1, "FI");
        factoryService.getStationFactory().create(tampere, 2, "FI");

        factoryService.getPassengerInformationMessageFactory().create("1", 1, ZonedDateTime.now().minusDays(1),
                ZonedDateTime.now().plusDays(1), List.of(helsinki, tampere));
        factoryService.getPassengerInformationMessageFactory().create("2", 1, ZonedDateTime.now().minusDays(1),
                ZonedDateTime.now().plusDays(1), List.of(tampere));

        final ResultActions result = this.query("""
                {
                  stations {
                    shortCode             
                    passengerInformationMessages {
                      id
                    }
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.stations[?(@.shortCode=='HKI')].passengerInformationMessages.length()").value(1));
        result.andExpect(jsonPath("$.data.stations[?(@.shortCode=='TPE')].passengerInformationMessages.length()").value(2));
        result.andExpect(jsonPath("$.data.stations[?(@.shortCode=='HKI')].passengerInformationMessages[?(@.id==1)]").exists());
        result.andExpect(jsonPath("$.data.stations[?(@.shortCode=='TPE')].passengerInformationMessages[?(@.id==1)]").exists());
        result.andExpect(jsonPath("$.data.stations[?(@.shortCode=='TPE')].passengerInformationMessages[?(@.id==2)]").exists());
    }
}
