package fi.digitraffic.graphql.rail.links;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.webmvc.BaseWebMVCTest;

public class CompositionToJourneySectionsLinkTest extends BaseWebMVCTest {

    private static final LocalDate DATE = LocalDate.of(2024, 6, 1);

    @Test
    public void linkShouldWork() throws Exception {
        final Train train = factoryService.getTrainFactory().createBaseTrain(1, DATE).getFirst();
        factoryService.getCompositionFactory().create(train);
        factoryService.getJourneySectionFactory().create(train, 120, 500, 1L, 2L);
        factoryService.getJourneySectionFactory().create(train, 140, 600, 3L, 4L);

        final ResultActions result = this.query("""
                {
                    compositionsGreaterThanVersion(version: "0") {
                        version
                        journeySections {
                            maximumSpeed
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.data.compositionsGreaterThanVersion.length()").value(1));
        result.andExpect(jsonPath("$.data.compositionsGreaterThanVersion[0].journeySections.length()").value(2));
    }
}

