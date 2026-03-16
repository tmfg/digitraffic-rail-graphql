package fi.digitraffic.graphql.rail.webmvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.ResultActions;

public class CompositionsGreaterThanVersionQueryTest extends BaseWebMVCTest {

    private static final LocalDate DATE = LocalDate.of(2024, 6, 1);

    @Test
    public void returnsMatchingCompositions() throws Exception {
        final var train1 = factoryService.getTrainFactory().createBaseTrain(1, DATE, 10L).getFirst();
        final var train2 = factoryService.getTrainFactory().createBaseTrain(2, DATE, 20L).getFirst();
        final var train3 = factoryService.getTrainFactory().createBaseTrain(3, DATE, 30L).getFirst();

        factoryService.getCompositionFactory().create(train1);
        factoryService.getCompositionFactory().create(train2);
        factoryService.getCompositionFactory().create(train3);

        final ResultActions result = this.query("""
                {
                    compositionsGreaterThanVersion(version: "15") {
                        version
                    }
                }""");

        result.andExpect(jsonPath("$.data.compositionsGreaterThanVersion.length()").value(2));
    }

    @Test
    public void returnsEmptyWhenNoneMatch() throws Exception {
        final var train = factoryService.getTrainFactory().createBaseTrain(1, DATE, 5L).getFirst();
        factoryService.getCompositionFactory().create(train);

        final ResultActions result = this.query("""
                {
                    compositionsGreaterThanVersion(version: "100") {
                        version
                    }
                }""");

        result.andExpect(jsonPath("$.data.compositionsGreaterThanVersion.length()").value(0));
    }

    @Test
    public void returnsInVersionOrder() throws Exception {
        final var train1 = factoryService.getTrainFactory().createBaseTrain(1, DATE, 30L).getFirst();
        final var train2 = factoryService.getTrainFactory().createBaseTrain(2, DATE, 20L).getFirst();

        factoryService.getCompositionFactory().create(train1);
        factoryService.getCompositionFactory().create(train2);

        final ResultActions result = this.query("""
                {
                    compositionsGreaterThanVersion(version: "0") {
                        version
                    }
                }""");

        result.andExpect(jsonPath("$.data.compositionsGreaterThanVersion.length()").value(2));
        result.andExpect(jsonPath("$.data.compositionsGreaterThanVersion[0].version").value("20"));
        result.andExpect(jsonPath("$.data.compositionsGreaterThanVersion[1].version").value("30"));
    }
}

