package fi.digitraffic.graphql.rail.webmvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.factory.TrainFactory;

/**
 * Integration tests for Train → Operator and Train → TrainType links.
 */
public class TrainToOperatorAndTrainTypeLinkTest extends BaseWebMVCTest {

    @Autowired
    private TrainFactory trainFactory;

    @Test
    public void operatorLinkReturnsCorrectFields() throws Exception {
        trainFactory.createBaseTrain(new TrainId(1L, LocalDate.of(2024, 1, 1)));

        final ResultActions result = this.query("""
                {
                    trainsByDepartureDate(departureDate: "2024-01-01") {
                        trainNumber
                        operator {
                            name
                            shortCode
                            uicCode
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(1));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].operator.name").value("test"));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].operator.shortCode").value("test"));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].operator.uicCode").isNumber());
    }

    @Test
    public void trainTypeLinkReturnsCorrectFields() throws Exception {
        trainFactory.createBaseTrain(new TrainId(1L, LocalDate.of(2024, 1, 1)));

        final ResultActions result = this.query("""
                {
                    trainsByDepartureDate(departureDate: "2024-01-01") {
                        trainNumber
                        trainType {
                            name
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(1));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].trainType.name").value("Test TrainType"));
    }

    @Test
    public void multipleTrainsShareSameOperator() throws Exception {
        trainFactory.createBaseTrain(new TrainId(1L, LocalDate.of(2024, 1, 1)));
        trainFactory.createBaseTrain(new TrainId(2L, LocalDate.of(2024, 1, 1)));

        final ResultActions result = this.query("""
                {
                    trainsByDepartureDate(departureDate: "2024-01-01") {
                        trainNumber
                        operator {
                            shortCode
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(2));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].operator.shortCode").value("test"));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[1].operator.shortCode").value("test"));
    }

    @Test
    public void multipleTrainsShareSameTrainType() throws Exception {
        trainFactory.createBaseTrain(new TrainId(1L, LocalDate.of(2024, 1, 1)));
        trainFactory.createBaseTrain(new TrainId(2L, LocalDate.of(2024, 1, 1)));

        final ResultActions result = this.query("""
                {
                    trainsByDepartureDate(departureDate: "2024-01-01") {
                        trainNumber
                        trainType {
                            name
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(2));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].trainType.name").value("Test TrainType"));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[1].trainType.name").value("Test TrainType"));
    }

    @Test
    public void operatorAndTrainTypeTogetherInSameQuery() throws Exception {
        trainFactory.createBaseTrain(new TrainId(1L, LocalDate.of(2024, 1, 1)));

        final ResultActions result = this.query("""
                {
                    trainsByDepartureDate(departureDate: "2024-01-01") {
                        trainNumber
                        operator {
                            name
                            shortCode
                        }
                        trainType {
                            name
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.data.trainsByDepartureDate.length()").value(1));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].operator.name").value("test"));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].trainType.name").value("Test TrainType"));
    }

    @Test
    public void operatorLinkWorksWithTrainQuery() throws Exception {
        trainFactory.createBaseTrain(new TrainId(66L, LocalDate.of(2024, 3, 15)));

        final ResultActions result = this.query("""
                {
                    train(trainNumber: 66, departureDate: "2024-03-15") {
                        trainNumber
                        operator {
                            name
                            shortCode
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.data.train.length()").value(1));
        result.andExpect(jsonPath("$.data.train[0].operator.shortCode").value("test"));
    }

    @Test
    public void trainTypeLinkWorksWithTrainQuery() throws Exception {
        trainFactory.createBaseTrain(new TrainId(66L, LocalDate.of(2024, 3, 15)));

        final ResultActions result = this.query("""
                {
                    train(trainNumber: 66, departureDate: "2024-03-15") {
                        trainNumber
                        trainType {
                            name
                        }
                    }
                }""");

        result.andExpect(jsonPath("$.data.train.length()").value(1));
        result.andExpect(jsonPath("$.data.train[0].trainType.name").value("Test TrainType"));
    }
}

