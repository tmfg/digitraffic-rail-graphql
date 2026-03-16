package fi.digitraffic.graphql.rail.links;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.webmvc.BaseWebMVCTest;

public class TrackSectionToTrackRangesLinkTest extends BaseWebMVCTest {

    private static final LocalDate DATE = LocalDate.of(2024, 6, 1);

    @Test
    public void linkShouldWork() throws Exception {
        final Train train = factoryService.getTrainFactory().createBaseTrain(1, DATE).getFirst();
        factoryService.getTrainTrackingMessageFactory().create(train);
        final long trackSectionId = factoryService.getTrackSectionFactory().create("TEST99", "TEST_UNIQUE");
        factoryService.getTrackSectionFactory().createRange(trackSectionId);
        factoryService.getTrackSectionFactory().createRange(trackSectionId);

        final ResultActions result = this.query("""
                {
                    trainTrackingMessagesByVersionGreaterThan(version: "0") {
                        trackSectionCode
                        trackSection {
                            trackSectionCode
                            ranges {
                                startTrack
                                endTrack
                                startKilometres
                            }
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.data.trainTrackingMessagesByVersionGreaterThan[0].trackSection.trackSectionCode").value("TEST_UNIQUE"));
        result.andExpect(jsonPath("$.data.trainTrackingMessagesByVersionGreaterThan[0].trackSection.ranges.length()").value(2));
        result.andExpect(jsonPath("$.data.trainTrackingMessagesByVersionGreaterThan[0].trackSection.ranges[0].startTrack").value("001"));
    }
}

