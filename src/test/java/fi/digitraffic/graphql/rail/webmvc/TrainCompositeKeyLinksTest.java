package fi.digitraffic.graphql.rail.webmvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.factory.CompositionFactory;
import fi.digitraffic.graphql.rail.factory.TrainFactory;
import fi.digitraffic.graphql.rail.factory.TrainLocationFactory;

/**
 * Integration tests for composite-key links: Train → TimeTableRows, TrainLocations, and Compositions.
 */
public class TrainCompositeKeyLinksTest extends BaseWebMVCTest {

    @Autowired
    private TrainFactory trainFactory;

    @Autowired
    private CompositionFactory compositionFactory;

    @Autowired
    private TrainLocationFactory trainLocationFactory;

    // ─── TrainToTimeTableRowLink ───────────────────────────────────────────────

    @Test
    public void timeTableRowsLinkReturnsRowsForTrain() throws Exception {
        trainFactory.createBaseTrain(new TrainId(100L, LocalDate.of(2024, 6, 1)));

        final ResultActions result = this.query("""
                {
                    trainsByDepartureDate(departureDate: "2024-06-01") {
                        trainNumber
                        timeTableRows {
                            type
                            scheduledTime
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(1));
        // TrainFactory creates 8 time table rows per train
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].timeTableRows.length()").value(8));
    }

    @Test
    public void timeTableRowsLinkReturnsRowsForMultipleTrains() throws Exception {
        trainFactory.createBaseTrain(new TrainId(101L, LocalDate.of(2024, 6, 1)));
        trainFactory.createBaseTrain(new TrainId(102L, LocalDate.of(2024, 6, 1)));

        final ResultActions result = this.query("""
                {
                    trainsByDepartureDate(departureDate: "2024-06-01") {
                        trainNumber
                        timeTableRows {
                            scheduledTime
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(2));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].timeTableRows.length()").value(8));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[1].timeTableRows.length()").value(8));
    }

    @Test
    public void timeTableRowsLinkFiltersByWhere() throws Exception {
        trainFactory.createBaseTrain(new TrainId(103L, LocalDate.of(2024, 6, 2)));

        // Filter by type (DEPARTURE) — type is not hidden, enum value passed as string
        final ResultActions result = this.query("""
                {
                    trainsByDepartureDate(departureDate: "2024-06-02") {
                        trainNumber
                        timeTableRows(where: { type: { equals: "DEPARTURE" } }) {
                            type
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(1));
        // TrainFactory creates 4 DEPARTURE rows (HKI, PSL, TPE, JY)
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].timeTableRows.length()").value(4));
    }

    @Test
    public void timeTableRowsLinkWithMultipleDepartureDates() throws Exception {
        trainFactory.createBaseTrain(new TrainId(104L, LocalDate.of(2024, 6, 3)));
        trainFactory.createBaseTrain(new TrainId(105L, LocalDate.of(2024, 6, 4)));

        // Query train 104
        final ResultActions result1 = this.query("""
                {
                    trainsByDepartureDate(departureDate: "2024-06-03") {
                        trainNumber
                        timeTableRows { scheduledTime }
                    }
                }""");
        result1.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(1));
        result1.andExpect(jsonPath("$.data.trainsByDepartureDate[0].timeTableRows.length()").value(8));

        // Query train 105
        final ResultActions result2 = this.query("""
                {
                    trainsByDepartureDate(departureDate: "2024-06-04") {
                        trainNumber
                        timeTableRows { scheduledTime }
                    }
                }""");
        result2.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(1));
        result2.andExpect(jsonPath("$.data.trainsByDepartureDate[0].timeTableRows.length()").value(8));
    }

    // ─── TrainToCompositionsLink ───────────────────────────────────────────────

    @Test
    public void compositionsLinkReturnsCompositionForTrain() throws Exception {
        final Train train = trainFactory.createBaseTrain(new TrainId(200L, LocalDate.of(2024, 7, 1))).getFirst();
        compositionFactory.create(train);

        final ResultActions result = this.query("""
                {
                    trainsByDepartureDate(departureDate: "2024-07-01") {
                        trainNumber
                        compositions {
                            version
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(1));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].compositions.length()").value(1));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].compositions[0].version").value("1"));
    }

    @Test
    public void compositionsLinkReturnsEmptyListWhenNoComposition() throws Exception {
        trainFactory.createBaseTrain(new TrainId(201L, LocalDate.of(2024, 7, 2)));

        final ResultActions result = this.query("""
                {
                    trainsByDepartureDate(departureDate: "2024-07-02") {
                        trainNumber
                        compositions {
                            version
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(1));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].compositions.length()").value(0));
    }

    @Test
    public void compositionsLinkWorksForMultipleTrains() throws Exception {
        final Train train1 = trainFactory.createBaseTrain(new TrainId(202L, LocalDate.of(2024, 7, 3))).getFirst();
        final Train train2 = trainFactory.createBaseTrain(new TrainId(203L, LocalDate.of(2024, 7, 3))).getFirst();
        compositionFactory.create(train1);
        compositionFactory.create(train2);

        final ResultActions result = this.query("""
                {
                    trainsByDepartureDate(departureDate: "2024-07-03") {
                        trainNumber
                        compositions {
                            version
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(2));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].compositions.length()").value(1));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[1].compositions.length()").value(1));
    }

    // ─── TrainToTrainLocationsLink ─────────────────────────────────────────────

    @Test
    public void trainLocationsLinkReturnsLocationsForTrain() throws Exception {
        final Train train = trainFactory.createBaseTrain(new TrainId(300L, LocalDate.of(2024, 8, 1))).getFirst();
        trainLocationFactory.create(25.0, 60.0, 120, train);
        trainLocationFactory.create(25.1, 60.1, 130, train);

        final ResultActions result = this.query("""
                {
                    trainsByDepartureDate(departureDate: "2024-08-01") {
                        trainNumber
                        trainLocations {
                            speed
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(1));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].trainLocations.length()").value(2));
    }

    @Test
    public void trainLocationsLinkReturnsEmptyListWhenNoLocations() throws Exception {
        trainFactory.createBaseTrain(new TrainId(301L, LocalDate.of(2024, 8, 2)));

        final ResultActions result = this.query("""
                {
                    trainsByDepartureDate(departureDate: "2024-08-02") {
                        trainNumber
                        trainLocations {
                            speed
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(1));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].trainLocations.length()").value(0));
    }

    @Test
    public void trainLocationsLinkFiltersBySpeed() throws Exception {
        final Train train = trainFactory.createBaseTrain(new TrainId(302L, LocalDate.of(2024, 8, 3))).getFirst();
        trainLocationFactory.create(25.0, 60.0, 50, train);
        trainLocationFactory.create(25.1, 60.1, 150, train);

        final ResultActions result = this.query("""
                {
                    trainsByDepartureDate(departureDate: "2024-08-03") {
                        trainNumber
                        trainLocations(where: { speed: { greaterThan: 100 } }) {
                            speed
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(1));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].trainLocations.length()").value(1));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].trainLocations[0].speed").value(150));
    }

    @Test
    public void trainLocationsLinkWorksForMultipleTrains() throws Exception {
        final Train train1 = trainFactory.createBaseTrain(new TrainId(303L, LocalDate.of(2024, 8, 4))).getFirst();
        final Train train2 = trainFactory.createBaseTrain(new TrainId(304L, LocalDate.of(2024, 8, 4))).getFirst();
        trainLocationFactory.create(25.0, 60.0, 100, train1);
        trainLocationFactory.create(26.0, 61.0, 200, train2);

        final ResultActions result = this.query("""
                {
                    trainsByDepartureDate(departureDate: "2024-08-04") {
                        trainNumber
                        trainLocations {
                            speed
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(2));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].trainLocations.length()").value(1));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[1].trainLocations.length()").value(1));
    }
}


