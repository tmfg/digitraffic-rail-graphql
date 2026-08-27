package fi.digitraffic.graphql.rail.links;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.webmvc.BaseWebMVCTest;

public class JourneySectionToLocomotiveLinkTest extends BaseWebMVCTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final LocalDate DATE = LocalDate.of(2024, 6, 1);

    @Test
    public void locomotiveFieldsAndCountShouldBeCorrect() throws Exception {
        // id and journeysectionId are hidden fields (stripped from schema), so we query
        // all non-hidden locomotive fields to exercise the full Hibernate mapping.
        final var train = factoryService.getTrainFactory().createBaseTrain(1, DATE).getFirst();
        factoryService.getCompositionFactory().create(train);
        final long journeySectionId = factoryService.getJourneySectionFactory().create(train, 120, 500, 1L, 2L);
        factoryService.getLocomotiveFactory().create(journeySectionId);
        factoryService.getLocomotiveFactory().create(journeySectionId);

        final ResultActions result = this.query("""
                {
                    compositionsGreaterThanVersion(version: "0") {
                        journeySections {
                            maximumSpeed
                            locomotives {
                                location
                                locomotiveType
                                powerTypeAbbreviation
                                vehicleNumber
                            }
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.errors").doesNotExist());
        result.andExpect(jsonPath("$.data.compositionsGreaterThanVersion[0].journeySections[0].locomotives.length()").value(2));
        result.andExpect(jsonPath("$.data.compositionsGreaterThanVersion[0].journeySections[0].locomotives[0].locomotiveType").value("Sr2"));
        result.andExpect(jsonPath("$.data.compositionsGreaterThanVersion[0].journeySections[0].locomotives[0].powerTypeAbbreviation").value("S"));
    }

    @Test
    public void locomotivesWhereOrderByAndTakeShouldWork() throws Exception {
        final var train = factoryService.getTrainFactory().createBaseTrain(1, DATE).getFirst();
        factoryService.getCompositionFactory().create(train);
        final long journeySectionId = factoryService.getJourneySectionFactory().create(train, 120, 500, 1L, 2L);
        final long firstId = factoryService.getLocomotiveFactory().create(journeySectionId);
        final long secondId = factoryService.getLocomotiveFactory().create(journeySectionId);
        jdbcTemplate.update("UPDATE locomotive SET location = ?, locomotive_type = ? WHERE id = ?", 1, "Sr1", firstId);
        jdbcTemplate.update("UPDATE locomotive SET location = ?, locomotive_type = ? WHERE id = ?", 2, "Sr2", secondId);

        final ResultActions result = this.query("""
                {
                    compositionsGreaterThanVersion(version: "0") {
                        journeySections {
                            locomotives(
                                where: { location: { greaterThan: 1 } }
                                orderBy: [{ location: DESCENDING }]
                                take: 1
                            ) {
                                location
                                locomotiveType
                            }
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.data.compositionsGreaterThanVersion[0].journeySections[0].locomotives.length()").value(1));
        result.andExpect(jsonPath("$.data.compositionsGreaterThanVersion[0].journeySections[0].locomotives[0].location").value(2));
        result.andExpect(jsonPath("$.data.compositionsGreaterThanVersion[0].journeySections[0].locomotives[0].locomotiveType").value("Sr2"));
    }

    @Test
    public void locomotivesSkipShouldWork() throws Exception {
        final var train = factoryService.getTrainFactory().createBaseTrain(1, DATE).getFirst();
        factoryService.getCompositionFactory().create(train);
        final long journeySectionId = factoryService.getJourneySectionFactory().create(train, 120, 500, 1L, 2L);
        final long firstId = factoryService.getLocomotiveFactory().create(journeySectionId);
        final long secondId = factoryService.getLocomotiveFactory().create(journeySectionId);
        jdbcTemplate.update("UPDATE locomotive SET location = ? WHERE id = ?", 1, firstId);
        jdbcTemplate.update("UPDATE locomotive SET location = ? WHERE id = ?", 2, secondId);

        final ResultActions result = this.query("""
                {
                    compositionsGreaterThanVersion(version: "0") {
                        journeySections {
                            locomotives(orderBy: [{ location: ASCENDING }], skip: 1, take: 1) {
                                location
                            }
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.data.compositionsGreaterThanVersion[0].journeySections[0].locomotives.length()").value(1));
        result.andExpect(jsonPath("$.data.compositionsGreaterThanVersion[0].journeySections[0].locomotives[0].location").value(2));
    }
}
