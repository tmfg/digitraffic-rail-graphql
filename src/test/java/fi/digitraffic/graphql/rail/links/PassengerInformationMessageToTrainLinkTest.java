package fi.digitraffic.graphql.rail.links;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.webmvc.BaseWebMVCTest;

public class PassengerInformationMessageToTrainLinkTest extends BaseWebMVCTest {

    @Test
    public void linkShouldWork() throws Exception {

        factoryService.getTrainFactory().createBaseTrain(1, LocalDate.of(2024, 1, 1));

        factoryService.getPassengerInformationMessageFactory().create("1", 1, ZonedDateTime.now().minusDays(1),
                ZonedDateTime.now().plusDays(1),
                LocalDate.of(2024, 1, 1), 1);

        final ResultActions result = this.query("""
                {
                  passengerInformationMessages {
                    train {
                      trainNumber
                    }
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.passengerInformationMessages.length()").value(1));
        result.andExpect(jsonPath("$.data.passengerInformationMessages[0].train.trainNumber").value(1));
    }
}
