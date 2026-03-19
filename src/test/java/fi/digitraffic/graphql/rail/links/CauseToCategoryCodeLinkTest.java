package fi.digitraffic.graphql.rail.links;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.webmvc.BaseWebMVCTest;

public class CauseToCategoryCodeLinkTest extends BaseWebMVCTest {

    @Test
    public void linkShouldWork() throws Exception {
        final var train = factoryService.getTrainFactory().createBaseTrain(new TrainId(1L, LocalDate.of(2024, 6, 1)));
        factoryService.getCauseFactory().create(train.getSecond().get(0));

        final ResultActions result = this.query("""
                {
                    trainsByDepartureDate(departureDate: "2024-06-01") {
                        timeTableRows {
                            causes {
                                categoryCode {
                                    code
                                    name
                                }
                            }
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].timeTableRows[0].causes[0].categoryCode.code").value("A"));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].timeTableRows[0].causes[0].categoryCode.name").value("C"));
    }
}

