package fi.digitraffic.graphql.rail.webmvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

public class TrainTrackingMessagesByVersionGreaterThanQueryTest extends BaseWebMVCTest {

    private static final LocalDate DATE = LocalDate.of(2024, 6, 1);

    @Test
    public void returnsMatchingMessages() throws Exception {
        final var train = factoryService.getTrainFactory().createBaseTrain(1, DATE).getFirst();
        factoryService.getTrainTrackingMessageFactory().create(train);

        final ResultActions result = this.query("""
                {
                    trainTrackingMessagesByVersionGreaterThan(version: "0") {
                        id
                        version
                    }
                }""");

        result.andExpect(jsonPath("$.data.trainTrackingMessagesByVersionGreaterThan.length()").value(1));
    }

    @Test
    public void returnsEmptyWhenNoneMatch() throws Exception {
        final var train = factoryService.getTrainFactory().createBaseTrain(1, DATE).getFirst();
        factoryService.getTrainTrackingMessageFactory().create(train);

        final ResultActions result = this.query("""
                {
                    trainTrackingMessagesByVersionGreaterThan(version: "9999") {
                        id
                    }
                }""");

        result.andExpect(jsonPath("$.data.trainTrackingMessagesByVersionGreaterThan.length()").value(0));
    }
}

