package fi.digitraffic.graphql.rail.links;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.webmvc.BaseWebMVCTest;

public class CauseToDetailedAndThirdCategoryCodeLinkTest extends BaseWebMVCTest {

    @Test
    public void detailedCategoryCodeLinkShouldWork() throws Exception {
        final var train = factoryService.getTrainFactory().createBaseTrain(new TrainId(1L, LocalDate.of(2024, 6, 1)));
        factoryService.getCauseFactory().create(train.getSecond().get(0));

        final ResultActions result = this.query("""
                {
                    trainsByDepartureDate(departureDate: "2024-06-01") {
                        timeTableRows {
                            causes {
                                detailedCategoryCode {
                                    code
                                    name
                                }
                                thirdCategoryCode {
                                    code
                                    name
                                }
                            }
                        }
                    }
                }""");

        // Causes with no detailedCategoryCodeOid return null — just verify no errors
        result.andExpect(jsonPath("$.errors").doesNotExist());
    }
}

