package fi.digitraffic.graphql.rail.links;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.webmvc.BaseWebMVCTest;

public class TrainToTrainLocationsLinkTest extends BaseWebMVCTest {

    private static final LocalDate DATE = LocalDate.of(2024, 6, 1);

    @Test
    public void linkShouldWork() throws Exception {
        final var train = factoryService.getTrainFactory().createBaseTrain(1, DATE).getFirst();
        factoryService.getTrainLocationFactory().create(25.0, 60.0, 100, train);
        factoryService.getTrainLocationFactory().create(25.1, 60.1, 110, train);

        final ResultActions result = this.query("""
                {
                    trainsByDepartureDate(departureDate: "2024-06-01") {
                        trainNumber
                        trainLocations {
                            speed
                            location
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].trainLocations.length()").value(2));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].trainLocations[0].speed").value(100));
    }
}

