package fi.digitraffic.graphql.rail.links;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.webmvc.BaseWebMVCTest;

public class JourneySectionToWagonLinkTest extends BaseWebMVCTest {

    private static final LocalDate DATE = LocalDate.of(2024, 6, 1);

    @Test
    public void wagonFieldsAndCountShouldBeCorrect() throws Exception {
        // id and journeysectionId are hidden fields (stripped from schema), so we query
        // all non-hidden wagon fields to exercise the full Hibernate mapping.
        final Train train = factoryService.getTrainFactory().createBaseTrain(1, DATE).getFirst();
        factoryService.getCompositionFactory().create(train);
        final long journeySectionId = factoryService.getJourneySectionFactory().create(train, 120, 500, 1L, 2L);
        factoryService.getWagonFactory().create(journeySectionId);
        factoryService.getWagonFactory().create(journeySectionId);

        final ResultActions result = this.query("""
                {
                    compositionsGreaterThanVersion(version: "0") {
                        journeySections {
                            maximumSpeed
                            wagons {
                                length
                                location
                                salesNumber
                                catering
                                disabled
                                luggage
                                pet
                                playground
                                smoking
                                video
                                wagonType
                                vehicleNumber
                            }
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.errors").doesNotExist());
        result.andExpect(jsonPath("$.data.compositionsGreaterThanVersion[0].journeySections[0].wagons.length()").value(2));
        result.andExpect(jsonPath("$.data.compositionsGreaterThanVersion[0].journeySections[0].wagons[0].length").value(10));
        result.andExpect(jsonPath("$.data.compositionsGreaterThanVersion[0].journeySections[0].wagons[0].location").value(1));
    }
}
