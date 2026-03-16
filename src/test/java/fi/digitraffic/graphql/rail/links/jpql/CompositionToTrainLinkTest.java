package fi.digitraffic.graphql.rail.links.jpql;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.webmvc.BaseWebMVCTest;

public class CompositionToTrainLinkTest extends BaseWebMVCTest {

    private static final LocalDate DATE = LocalDate.of(2024, 6, 1);

    @Test
    public void linkShouldWork() throws Exception {
        final Train train = factoryService.getTrainFactory().createBaseTrain(1, DATE).getFirst();
        factoryService.getCompositionFactory().create(train);

        final ResultActions result = this.query("""
                {
                    compositionsGreaterThanVersion(version: "0") {
                        version
                        train {
                            trainNumber
                            departureDate
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.data.compositionsGreaterThanVersion.length()").value(1));
        result.andExpect(jsonPath("$.data.compositionsGreaterThanVersion[0].train.trainNumber").value(1));
        result.andExpect(jsonPath("$.data.compositionsGreaterThanVersion[0].train.departureDate").value(DATE.toString()));
    }
}

