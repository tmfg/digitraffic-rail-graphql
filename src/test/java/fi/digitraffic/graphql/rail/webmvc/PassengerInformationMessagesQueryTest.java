package fi.digitraffic.graphql.rail.webmvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.factory.PassengerInformationMessageFactory;

public class PassengerInformationMessagesQueryTest extends BaseWebMVCTest {

    @Test
    public void testPassengerInformationMessagesQuery() throws Exception {
        final PassengerInformationMessageFactory factory = factoryService.getPassengerInformationMessageFactory();

        factory.create("1", 1, ZonedDateTime.now().minusDays(2),
                ZonedDateTime.now().minusDays(1),
                LocalDate.of(2024, 1, 1), 1);
        factory.create("2", 1, ZonedDateTime.now().minusDays(1),
                ZonedDateTime.now().plusDays(1),
                LocalDate.of(2024, 1, 1), 1);
        factory.create("2", 2, ZonedDateTime.now().minusDays(1),
                ZonedDateTime.now().plusDays(1),
                LocalDate.of(2024, 1, 1), 1);
        factory.create("3", 1, ZonedDateTime.now().minusDays(1),
                ZonedDateTime.now().plusDays(1),
                LocalDate.of(2024, 1, 1), 1);

        final ResultActions result = this.query("{ passengerInformationMessages { id, version }}");

        // only currently active (startValidity < now < endValidity) messages should be returned
        result.andExpect(jsonPath("$.data.passengerInformationMessages.length()").value(2));

        // only the latest version of a message id should be returned
        result.andExpect(jsonPath("$.data.passengerInformationMessages[?(@.id==2 && @.version==2)]").exists());
        result.andExpect(jsonPath("$.data.passengerInformationMessages[?(@.id==2 && @.version==1)]").doesNotExist());
    }
}
