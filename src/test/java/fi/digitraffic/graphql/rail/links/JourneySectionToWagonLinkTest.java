package fi.digitraffic.graphql.rail.links;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.webmvc.BaseWebMVCTest;

public class JourneySectionToWagonLinkTest extends BaseWebMVCTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final LocalDate DATE = LocalDate.of(2024, 6, 1);

    @Test
    public void wagonFieldsAndCountShouldBeCorrect() throws Exception {
        // id and journeysectionId are hidden fields (stripped from schema), so we query
        // all non-hidden wagon fields to exercise the full Hibernate mapping.
        final Train train = factoryService.getTrainFactory().createBaseTrain(1, DATE).getFirst();
        factoryService.getCompositionFactory().create(train);
        final long journeySectionId = factoryService.getJourneySectionFactory().create(train, 120, 500, 1L, 2L);
        factoryService.getWagonFactory().create(journeySectionId);
        factoryService.getWagonFactory().create(journeySectionId);

        final ResultActions result = this.query("""
                {
                    compositionsGreaterThanVersion(version: "0") {
                        journeySections {
                            maximumSpeed
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
        result.andExpect(jsonPath("$.data.compositionsGreaterThanVersion[0].journeySections[0].wagons.length()").value(2));
        result.andExpect(jsonPath("$.data.compositionsGreaterThanVersion[0].journeySections[0].wagons[0].length").value(10));
        result.andExpect(jsonPath("$.data.compositionsGreaterThanVersion[0].journeySections[0].wagons[0].location").value(1));
    }

    @Test
    public void wagonsWhereOrderByAndTakeShouldWork() throws Exception {
        final Train train = factoryService.getTrainFactory().createBaseTrain(1, DATE).getFirst();
        factoryService.getCompositionFactory().create(train);
        final long journeySectionId = factoryService.getJourneySectionFactory().create(train, 120, 500, 1L, 2L);
        final long firstId = factoryService.getWagonFactory().create(journeySectionId);
        final long secondId = factoryService.getWagonFactory().create(journeySectionId);
        jdbcTemplate.update("UPDATE wagon SET length = ?, location = ? WHERE id = ?", 10, 1, firstId);
        jdbcTemplate.update("UPDATE wagon SET length = ?, location = ? WHERE id = ?", 20, 2, secondId);

        final ResultActions result = this.query("""
                {
                    compositionsGreaterThanVersion(version: "0") {
                        journeySections {
                            wagons(
                                where: { length: { greaterThan: 10 } }
                                orderBy: [{ length: DESCENDING }]
                                take: 1
                            ) {
                                length
                            }
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.data.compositionsGreaterThanVersion[0].journeySections[0].wagons.length()").value(1));
        result.andExpect(jsonPath("$.data.compositionsGreaterThanVersion[0].journeySections[0].wagons[0].length").value(20));
    }

    @Test
    public void wagonsSkipShouldWork() throws Exception {
        final Train train = factoryService.getTrainFactory().createBaseTrain(1, DATE).getFirst();
        factoryService.getCompositionFactory().create(train);
        final long journeySectionId = factoryService.getJourneySectionFactory().create(train, 120, 500, 1L, 2L);
        final long firstId = factoryService.getWagonFactory().create(journeySectionId);
        final long secondId = factoryService.getWagonFactory().create(journeySectionId);
        jdbcTemplate.update("UPDATE wagon SET length = ? WHERE id = ?", 10, firstId);
        jdbcTemplate.update("UPDATE wagon SET length = ? WHERE id = ?", 20, secondId);

        final ResultActions result = this.query("""
                {
                    compositionsGreaterThanVersion(version: "0") {
                        journeySections {
                            wagons(orderBy: [{ length: ASCENDING }], skip: 1, take: 1) {
                                length
                            }
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.data.compositionsGreaterThanVersion[0].journeySections[0].wagons.length()").value(1));
        result.andExpect(jsonPath("$.data.compositionsGreaterThanVersion[0].journeySections[0].wagons[0].length").value(20));
    }
}
