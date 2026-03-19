package fi.digitraffic.graphql.rail.links;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.webmvc.BaseWebMVCTest;

public class TrainTrackingMessageToStationLinksTest extends BaseWebMVCTest {

    private static final LocalDate DATE = LocalDate.of(2024, 6, 1);

    @Test
    public void stationLinkShouldWork() throws Exception {
        final var train = factoryService.getTrainFactory().createBaseTrain(new TrainId(1L, DATE)).getFirst();
        // TrainTrackingMessageFactory sets stationShortCode = "TEST99"
        factoryService.getStationFactory().create("TEST99", 99, "FI");
        factoryService.getTrainTrackingMessageFactory().create(train);

        final ResultActions result = this.query("""
                {
                    trainTrackingMessagesByVersionGreaterThan(version: "0") {
                        station { shortCode name }
                        nextStation { shortCode }
                        previousStation { shortCode }
                    }
                }""");

        result.andExpect(jsonPath("$.errors").doesNotExist());
        result.andExpect(jsonPath("$.data.trainTrackingMessagesByVersionGreaterThan[0].station.shortCode").value("TEST99"));
        // next/previous are null in factory — links must handle null keys gracefully
        result.andExpect(jsonPath("$.data.trainTrackingMessagesByVersionGreaterThan[0].nextStation").isEmpty());
        result.andExpect(jsonPath("$.data.trainTrackingMessagesByVersionGreaterThan[0].previousStation").isEmpty());
    }

    @Test
    public void trackSectionLinkShouldWork() throws Exception {
        final var train = factoryService.getTrainFactory().createBaseTrain(new TrainId(2L, DATE)).getFirst();
        factoryService.getStationFactory().create("TEST99", 99, "FI");
        factoryService.getTrainTrackingMessageFactory().create(train);
        // TrainTrackingMessageFactory sets track_section = "TEST_UNIQUE"
        factoryService.getTrackSectionFactory().create("TEST99", "TEST_UNIQUE");

        final ResultActions result = this.query("""
                {
                    trainTrackingMessagesByVersionGreaterThan(version: "0") {
                        trackSection { trackSectionCode }
                    }
                }""");

        result.andExpect(jsonPath("$.errors").doesNotExist());
        result.andExpect(jsonPath("$.data.trainTrackingMessagesByVersionGreaterThan[0].trackSection.trackSectionCode").value("TEST_UNIQUE"));
    }

    @Test
    public void stationLinkWithMissingStationShouldNotCauseInternalError() throws Exception {
        // TrainTrackingMessageFactory sets stationShortCode = "TEST99" but we intentionally
        // do NOT create a Station row for it
        final var train = factoryService.getTrainFactory().createBaseTrain(new TrainId(4L, DATE)).getFirst();
        factoryService.getTrainTrackingMessageFactory().create(train);

        final ResultActions result = this.query("""
                {
                    trainTrackingMessagesByVersionGreaterThan(version: "0") {
                        station { shortCode }
                    }
                }""");

        result.andExpect(jsonPath("$.errors").doesNotExist());
    }

    @Test
    public void duplicateTrackSectionCodeShouldNotCauseInternalError() throws Exception {
        // Reproduces: HibernateException: More than one row with the given identifier was found
        // In production, multiple TrackSection rows can share the same trackSectionCode.
        // The @ManyToOne on TrainTrackingMessage uses referencedColumnName = "trackSectionCode"
        // which triggers Hibernate's unique-key loader and throws when it finds > 1 row.
        final var train = factoryService.getTrainFactory().createBaseTrain(new TrainId(5L, DATE)).getFirst();
        factoryService.getStationFactory().create("TEST99", 99, "FI");
        // Create two TrackSection rows with the same trackSectionCode (mirrors production data)
        factoryService.getTrackSectionFactory().create("TEST99", "DUPLICATE_CODE");
        factoryService.getTrackSectionFactory().create("TEST99", "DUPLICATE_CODE");
        // TrainTrackingMessageFactory sets track_section = "TEST_UNIQUE", so override via a
        // dedicated message that references the duplicate code
        factoryService.getTrainTrackingMessageFactory().createWithTrackSection(train, "DUPLICATE_CODE");

        final ResultActions result = this.query("""
                {
                    trainTrackingMessagesByVersionGreaterThan(version: "0") {
                        trackSection { trackSectionCode }
                    }
                }""");

        result.andExpect(jsonPath("$.errors").doesNotExist());
    }

    @Test
    public void nonNumericTrainNumberShouldReturnNullTrain() throws Exception {
        // Reproduces the pattern where trainNumber is e.g. "F29657" (non-numeric).
        // createKeyFromParent returns null, BaseLink.createFetcher short-circuits to null
        // without hitting the DataLoader, so no NumberFormatException is thrown.
        final var train = factoryService.getTrainFactory().createBaseTrain(new TrainId(6L, DATE)).getFirst();
        factoryService.getStationFactory().create("TEST99", 99, "FI");
        factoryService.getTrainTrackingMessageFactory().createWithNonNumericTrainNumber(train, "F29657");

        final ResultActions result = this.query("""
                {
                    trainTrackingMessagesByVersionGreaterThan(version: "0") {
                        train { trainNumber }
                    }
                }""");

        result.andExpect(jsonPath("$.errors").doesNotExist());
        result.andExpect(jsonPath("$.data.trainTrackingMessagesByVersionGreaterThan[0].train").isEmpty());
    }

    @Test
    public void trackSectionLinkReturnsNullWhenNoMatchingTrackSection() throws Exception {
        final var train = factoryService.getTrainFactory().createBaseTrain(new TrainId(3L, DATE)).getFirst();
        factoryService.getStationFactory().create("TEST99", 99, "FI");
        factoryService.getTrainTrackingMessageFactory().create(train);
        // No TrackSection created — link must return null gracefully

        final ResultActions result = this.query("""
                {
                    trainTrackingMessagesByVersionGreaterThan(version: "0") {
                        trackSection { trackSectionCode }
                    }
                }""");

        result.andExpect(jsonPath("$.errors").doesNotExist());
        result.andExpect(jsonPath("$.data.trainTrackingMessagesByVersionGreaterThan[0].trackSection").isEmpty());
    }
}

