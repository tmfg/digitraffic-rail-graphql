package fi.digitraffic.graphql.rail.links.jpql;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.webmvc.BaseWebMVCTest;

public class JourneySectionToWagonLinkTest extends BaseWebMVCTest {

    private static final LocalDate DATE = LocalDate.of(2024, 6, 1);

    @Test
    public void linkShouldWork() throws Exception {
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
                            }
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.data.compositionsGreaterThanVersion[0].journeySections[0].wagons.length()").value(2));
        result.andExpect(jsonPath("$.data.compositionsGreaterThanVersion[0].journeySections[0].wagons[0].length").value(10));
    }
}

