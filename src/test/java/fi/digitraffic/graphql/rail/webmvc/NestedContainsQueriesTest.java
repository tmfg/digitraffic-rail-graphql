package fi.digitraffic.graphql.rail.webmvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.factory.CompositionFactory;
import fi.digitraffic.graphql.rail.factory.JourneySectionFactory;
import fi.digitraffic.graphql.rail.factory.TrainFactory;

/**
 * Integration tests for nested 'contains' queries:
 * Train → compositions (contains) → journeySections (contains) → field condition.
 *
 * This exercises the recursive contains handling in JpqlWhereBuilder, which generates:
 *   EXISTS (SELECT sub0 FROM e.compositions sub0 WHERE
 *     EXISTS (SELECT sub1 FROM sub0.journeySections sub1 WHERE sub1.maximumSpeed > :p0))
 */
public class NestedContainsQueriesTest extends BaseWebMVCTest {

    private static final LocalDate DATE = LocalDate.of(2024, 9, 1);

    @Autowired
    private TrainFactory trainFactory;

    @Autowired
    private CompositionFactory compositionFactory;

    @Autowired
    private JourneySectionFactory journeySectionFactory;

    /**
     * Train has a composition with a fast journey section (speed 200).
     * Nested contains should find it.
     */
    @Test
    public void nestedContainsMatchesTrainWithFastJourneySection() throws Exception {
        final Train train = trainFactory.createBaseTrain(1, DATE).getFirst();
        compositionFactory.create(train);
        journeySectionFactory.create(train, 200, 500, 1L, 2L);

        final ResultActions result = this.query("""
                {
                    trainsByDepartureDate(departureDate: "2024-09-01",
                        where: {
                            compositions: {
                                contains: {
                                    journeySections: {
                                        contains: {
                                            maximumSpeed: { greaterThan: 150 }
                                        }
                                    }
                                }
                            }
                        }) {
                        trainNumber
                    }
                }""");

        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(1));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].trainNumber").value(1));
    }

    /**
     * Train has a composition but the journey section is slow.
     * Nested contains should not find it.
     */
    @Test
    public void nestedContainsDoesNotMatchWhenConditionFails() throws Exception {
        final Train train = trainFactory.createBaseTrain(2, DATE).getFirst();
        compositionFactory.create(train);
        journeySectionFactory.create(train, 80, 300, 1L, 2L);

        final ResultActions result = this.query("""
                {
                    trainsByDepartureDate(departureDate: "2024-09-01",
                        where: {
                            compositions: {
                                contains: {
                                    journeySections: {
                                        contains: {
                                            maximumSpeed: { greaterThan: 150 }
                                        }
                                    }
                                }
                            }
                        }) {
                        trainNumber
                    }
                }""");

        result.andExpect(jsonPath("$.data.trainsByDepartureDate").isEmpty());
    }

    /**
     * Two trains — only one has a fast enough journey section.
     * Nested contains should selectively return the matching train.
     */
    @Test
    public void nestedContainsSelectivelyFilters() throws Exception {
        final Train fastTrain = trainFactory.createBaseTrain(3, DATE).getFirst();
        final Train slowTrain = trainFactory.createBaseTrain(4, DATE).getFirst();
        compositionFactory.create(fastTrain);
        compositionFactory.create(slowTrain);
        journeySectionFactory.create(fastTrain, 250, 600, 1L, 2L);
        journeySectionFactory.create(slowTrain, 60, 200, 3L, 4L);

        final ResultActions result = this.query("""
                {
                    trainsByDepartureDate(departureDate: "2024-09-01",
                        where: {
                            compositions: {
                                contains: {
                                    journeySections: {
                                        contains: {
                                            maximumSpeed: { greaterThan: 200 }
                                        }
                                    }
                                }
                            }
                        }) {
                        trainNumber
                    }
                }""");

        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(1));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].trainNumber").value(3));
    }

    /**
     * Nested contains combined with a top-level where condition on the train.
     */
    @Test
    public void nestedContainsCombinedWithTopLevelWhere() throws Exception {
        final Train cancelledTrain = trainFactory.createBaseTrain(5, DATE).getFirst();
        final Train runningTrain = trainFactory.createBaseTrain(6, DATE).getFirst();
        compositionFactory.create(cancelledTrain);
        compositionFactory.create(runningTrain);
        journeySectionFactory.create(cancelledTrain, 180, 400, 1L, 2L);
        journeySectionFactory.create(runningTrain, 180, 400, 3L, 4L);

        // Both have fast journey sections, but only filter running ones
        final ResultActions result = this.query("""
                {
                    trainsByDepartureDate(departureDate: "2024-09-01",
                        where: {
                            runningCurrently: { equals: true },
                            compositions: {
                                contains: {
                                    journeySections: {
                                        contains: {
                                            maximumSpeed: { greaterThan: 150 }
                                        }
                                    }
                                }
                            }
                        }) {
                        trainNumber
                    }
                }""");

        // runningCurrently is true for all base trains — both should match
        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(2));
    }

    /**
     * Nested contains using totalLength instead of maximumSpeed.
     */
    @Test
    public void nestedContainsOnTotalLength() throws Exception {
        final Train longTrain = trainFactory.createBaseTrain(7, DATE).getFirst();
        final Train shortTrain = trainFactory.createBaseTrain(8, DATE).getFirst();
        compositionFactory.create(longTrain);
        compositionFactory.create(shortTrain);
        journeySectionFactory.create(longTrain, 160, 800, 1L, 2L);
        journeySectionFactory.create(shortTrain, 160, 100, 3L, 4L);

        final ResultActions result = this.query("""
                {
                    trainsByDepartureDate(departureDate: "2024-09-01",
                        where: {
                            compositions: {
                                contains: {
                                    journeySections: {
                                        contains: {
                                            totalLength: { greaterThan: 500 }
                                        }
                                    }
                                }
                            }
                        }) {
                        trainNumber
                    }
                }""");

        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(1));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].trainNumber").value(7));
    }

    /**
     * Train has no compositions — nested contains should not match.
     */
    @Test
    public void nestedContainsDoesNotMatchTrainWithNoCompositions() throws Exception {
        trainFactory.createBaseTrain(9, DATE);

        final ResultActions result = this.query("""
                {
                    trainsByDepartureDate(departureDate: "2024-09-01",
                        where: {
                            compositions: {
                                contains: {
                                    journeySections: {
                                        contains: {
                                            maximumSpeed: { greaterThan: 0 }
                                        }
                                    }
                                }
                            }
                        }) {
                        trainNumber
                    }
                }""");

        result.andExpect(jsonPath("$.data.trainsByDepartureDate").isEmpty());
    }

    /**
     * Train has composition but no journey sections — nested contains should not match.
     */
    @Test
    public void nestedContainsDoesNotMatchCompositionWithNoJourneySections() throws Exception {
        final Train train = trainFactory.createBaseTrain(10, DATE).getFirst();
        compositionFactory.create(train);
        // No journey sections created

        final ResultActions result = this.query("""
                {
                    trainsByDepartureDate(departureDate: "2024-09-01",
                        where: {
                            compositions: {
                                contains: {
                                    journeySections: {
                                        contains: {
                                            maximumSpeed: { greaterThan: 0 }
                                        }
                                    }
                                }
                            }
                        }) {
                        trainNumber
                    }
                }""");

        result.andExpect(jsonPath("$.data.trainsByDepartureDate").isEmpty());
    }
}

