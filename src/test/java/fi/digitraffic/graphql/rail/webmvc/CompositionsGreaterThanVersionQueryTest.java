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

    @Test
    public void fullCompositionStructureIsReadable() throws Exception {
        // Exercises the full chain in a single query: composition → train, journeySections →
        // wagons (all non-hidden fields), locomotives (all non-hidden fields), startTimeTableRow, endTimeTableRow.
        // Regression: this exact query pattern triggered "Unknown column 'w1_0.journeysection_id'"
        // in production due to a duplicate @Column mapping on Wagon.journeysectionId.
        final var pair = factoryService.getTrainFactory().createBaseTrain(1, DATE, 10L);
        final var train = pair.getFirst();
        final var rows = pair.getSecond();
        factoryService.getCompositionFactory().create(train);
        final long sectionId = factoryService.getJourneySectionFactory().create(
                train, 120, 500, rows.get(0).id.attapId, rows.get(1).id.attapId);
        factoryService.getWagonFactory().create(sectionId);
        factoryService.getLocomotiveFactory().create(sectionId);

        final ResultActions result = this.query("""
                {
                    compositionsGreaterThanVersion(version: "0") {
                        version
                        train { trainNumber cancelled }
                        journeySections {
                            maximumSpeed
                            totalLength
                            startTimeTableRow { scheduledTime type }
                            endTimeTableRow   { scheduledTime type }
                            locomotives {
                                location
                                locomotiveType
                                powerTypeAbbreviation
                                vehicleNumber
                            }
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
        result.andExpect(jsonPath("$.data.compositionsGreaterThanVersion.length()").value(1));
        result.andExpect(jsonPath("$.data.compositionsGreaterThanVersion[0].train.trainNumber").value(1));
        result.andExpect(jsonPath("$.data.compositionsGreaterThanVersion[0].journeySections[0].wagons.length()").value(1));
        result.andExpect(jsonPath("$.data.compositionsGreaterThanVersion[0].journeySections[0].wagons[0].length").value(10));
        result.andExpect(jsonPath("$.data.compositionsGreaterThanVersion[0].journeySections[0].locomotives.length()").value(1));
        result.andExpect(jsonPath("$.data.compositionsGreaterThanVersion[0].journeySections[0].locomotives[0].locomotiveType").value("Sr2"));
        result.andExpect(jsonPath("$.data.compositionsGreaterThanVersion[0].journeySections[0].startTimeTableRow").exists());
        result.andExpect(jsonPath("$.data.compositionsGreaterThanVersion[0].journeySections[0].endTimeTableRow").exists());
    }
}
