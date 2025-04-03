package fi.digitraffic.graphql.rail.webmvc;

import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.factory.PassengerInformationMessageFactory;
import fi.digitraffic.graphql.rail.factory.TrainFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class FailingQueriesTest extends BaseWebMVCTest {
    @Autowired
    private TrainFactory trainFactory;

    @Test
    public void failingTest() throws Exception {
        trainFactory.createBaseTrain(new TrainId(2257L, LocalDate.of(2025, 3, 25)));
        final PassengerInformationMessageFactory factory = factoryService.getPassengerInformationMessageFactory();

        factory.create("1", 1, ZonedDateTime.now().minusDays(2),
                ZonedDateTime.now().plusDays(1),
                LocalDate.of(2025, 3, 25), 2257);

        final ResultActions result = this.query("""
                {passengerInformationMessages(where: {trainDepartureDate: {equals: "2025-03-25"}}
                ) {
                  version
                  id
                  train {
                  trainNumber
                  timeTableRows {
                  scheduledTime
                }
                }
                }
                }"""
        );

        result.andExpect(jsonPath("$.data.passengerInformationMessages.length()").value(1));
    }
}
