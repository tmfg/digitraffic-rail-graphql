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
        // but TrainFactory creates HKI, PSL, TPE, JY, OL stations — use one of those
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
}

