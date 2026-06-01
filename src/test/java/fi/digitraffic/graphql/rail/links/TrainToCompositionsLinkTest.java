package fi.digitraffic.graphql.rail.links;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.webmvc.BaseWebMVCTest;

public class TrainToCompositionsLinkTest extends BaseWebMVCTest {

    private static final LocalDate DATE = LocalDate.of(2024, 6, 1);

    @Test
    public void linkShouldWork() throws Exception {
        final var train = factoryService.getTrainFactory().createBaseTrain(1, DATE).getFirst();
        factoryService.getCompositionFactory().create(train);

        final ResultActions result = this.query("""
                {
                    trainsByDepartureDate(departureDate: "2024-06-01") {
                        trainNumber
                        compositions {
                            version
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].compositions.length()").value(1));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].compositions[0].version").isNotEmpty());
    }

    @Test
    public void whereFilterShouldWork() throws Exception {
        final var train1 = factoryService.getTrainFactory().createBaseTrain(1, DATE).getFirst();
        final var train2 = factoryService.getTrainFactory().createBaseTrain(2, DATE).getFirst();

        // this sets the composition version also to 2 in CompositionFactory
        train2.version = 2L;

        factoryService.getCompositionFactory().create(train1);
        factoryService.getCompositionFactory().create(train2);

        final ResultActions result = this.query("""
                {
                    trainsByDepartureDate(departureDate: "2024-06-01") {
                        trainNumber
                        compositions(where: { version: { greaterThan: "1" } }) {
                            version
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.data.trainsByDepartureDate[?(@.trainNumber==1)].compositions.length()").value(0));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[?(@.trainNumber==2)].compositions.length()").value(1));
    }

    @Test
    public void skipShouldWork() throws Exception {
        final var train = factoryService.getTrainFactory().createBaseTrain(1, DATE).getFirst();
        factoryService.getCompositionFactory().create(train);

        final ResultActions result = this.query("""
                {
                    trainsByDepartureDate(departureDate: "2024-06-01") {
                        compositions(skip: 1, take: 1) {
                            version
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].compositions").isEmpty());
    }
}

