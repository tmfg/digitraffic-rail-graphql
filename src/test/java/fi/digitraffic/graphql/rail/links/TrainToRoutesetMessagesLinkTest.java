package fi.digitraffic.graphql.rail.links;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.entities.TrainId;
import fi.digitraffic.graphql.rail.webmvc.BaseWebMVCTest;

class TrainToRoutesetMessagesLinkTest extends BaseWebMVCTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void linkReturnsRoutesetMessagesForTrain() throws Exception {
        final var train = factoryService.getTrainFactory().createBaseTrain(new TrainId(66L, LocalDate.of(2020, 9, 17)));
        factoryService.getRoutesetMessageFactory().create(train.getFirst());

        final ResultActions result = query("""
                {
                  trainsByDepartureDate(departureDate: "2020-09-17") {
                    trainNumber
                    routesetMessages {
                      version
                      routeType
                    }
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].routesetMessages.length()").value(1));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].routesetMessages[0].version").value("1"));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].routesetMessages[0].routeType").value("A"));
    }

    @Test
    void linkReturnsEmptyListWhenNoRoutesetMessages() throws Exception {
        factoryService.getTrainFactory().createBaseTrain(new TrainId(66L, LocalDate.of(2020, 9, 17)));

        final ResultActions result = query("""
                {
                  trainsByDepartureDate(departureDate: "2020-09-17") {
                    trainNumber
                    routesetMessages {
                      version
                    }
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].routesetMessages.length()").value(0));
    }

    @Test
    void linkOnlyReturnsMessagesForCorrectTrain() throws Exception {
        final var train1 = factoryService.getTrainFactory().createBaseTrain(new TrainId(66L, LocalDate.of(2020, 9, 17)));
        factoryService.getTrainFactory().createBaseTrain(new TrainId(77L, LocalDate.of(2020, 9, 17)));
        factoryService.getRoutesetMessageFactory().create(train1.getFirst());

        final ResultActions result = query("""
                {
                  trainsByDepartureDate(departureDate: "2020-09-17") {
                    trainNumber
                    routesetMessages {
                      version
                    }
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.trainsByDepartureDate[?(@.trainNumber==66)].routesetMessages.length()").value(1));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[?(@.trainNumber==77)].routesetMessages.length()").value(0));
    }

    @Test
    void routesetMessagesWhereOrderByAndTakeShouldWork() throws Exception {
        final var train = factoryService.getTrainFactory().createBaseTrain(new TrainId(66L, LocalDate.of(2020, 9, 17)));
        final var first = factoryService.getRoutesetMessageFactory().create(train.getFirst());
        final var second = factoryService.getRoutesetMessageFactory().create(train.getFirst());
        jdbcTemplate.update("UPDATE routeset SET route_type = ?, version = ? WHERE id = ?", "A", 1L, first.id);
        jdbcTemplate.update("UPDATE routeset SET route_type = ?, version = ? WHERE id = ?", "B", 2L, second.id);

        final ResultActions result = query("""
                {
                  trainsByDepartureDate(departureDate: "2020-09-17") {
                    routesetMessages(
                      where: { routeType: { equals: "B" } }
                      orderBy: [{ version: DESCENDING }]
                      take: 1
                    ) {
                      routeType
                      version
                    }
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].routesetMessages.length()").value(1));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].routesetMessages[0].routeType").value("B"));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].routesetMessages[0].version").value("2"));
    }

    @Test
    void routesetMessagesSkipShouldWork() throws Exception {
        final var train = factoryService.getTrainFactory().createBaseTrain(new TrainId(66L, LocalDate.of(2020, 9, 17)));
        final var first = factoryService.getRoutesetMessageFactory().create(train.getFirst());
        final var second = factoryService.getRoutesetMessageFactory().create(train.getFirst());
        jdbcTemplate.update("UPDATE routeset SET version = ? WHERE id = ?", 1L, first.id);
        jdbcTemplate.update("UPDATE routeset SET version = ? WHERE id = ?", 2L, second.id);

        final ResultActions result = query("""
                {
                  trainsByDepartureDate(departureDate: "2020-09-17") {
                    routesetMessages(orderBy: [{ version: ASCENDING }], skip: 1, take: 1) {
                      version
                    }
                  }
                }
                """);

        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].routesetMessages.length()").value(1));
        result.andExpect(jsonPath("$.data.trainsByDepartureDate[0].routesetMessages[0].version").value("2"));
    }
}

