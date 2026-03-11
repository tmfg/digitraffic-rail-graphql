package fi.digitraffic.graphql.rail.webmvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.repositories.TrainRepository;

/**
 * Integration tests for the trainsByVersionGreaterThan query.
 */
public class TrainsByVersionGreaterThanQueryTest extends BaseWebMVCTest {

    private static final LocalDate DATE = LocalDate.of(2024, 6, 1);

    @Autowired
    private TrainRepository trainRepository;

    @Test
    public void returnsMatchingTrains() throws Exception {
        factoryService.getTrainFactory().createBaseTrain(1, DATE, 10L);
        factoryService.getTrainFactory().createBaseTrain(2, DATE, 20L);
        factoryService.getTrainFactory().createBaseTrain(3, DATE, 30L);

        final ResultActions result = this.query("""
                {
                    trainsByVersionGreaterThan(version: "15") {
                        trainNumber
                        version
                    }
                }""");

        result.andExpect(jsonPath("$.data.trainsByVersionGreaterThan.length()").value(2));
    }

    @Test
    public void returnsEmptyWhenNoneMatch() throws Exception {
        factoryService.getTrainFactory().createBaseTrain(1, DATE, 5L);
        factoryService.getTrainFactory().createBaseTrain(2, DATE, 10L);

        final ResultActions result = this.query("""
                {
                    trainsByVersionGreaterThan(version: "100") {
                        trainNumber
                    }
                }""");

        result.andExpect(jsonPath("$.data.trainsByVersionGreaterThan").isEmpty());
    }

    @Test
    public void ordersByVersionAsc() throws Exception {
        factoryService.getTrainFactory().createBaseTrain(1, DATE, 30L);
        factoryService.getTrainFactory().createBaseTrain(2, DATE, 10L);
        factoryService.getTrainFactory().createBaseTrain(3, DATE, 20L);

        final ResultActions result = this.query("""
                {
                    trainsByVersionGreaterThan(version: "5") {
                        trainNumber
                        version
                    }
                }""");

        result.andExpect(jsonPath("$.data.trainsByVersionGreaterThan.length()").value(3));
        result.andExpect(jsonPath("$.data.trainsByVersionGreaterThan[0].version").value("10"));
        result.andExpect(jsonPath("$.data.trainsByVersionGreaterThan[1].version").value("20"));
        result.andExpect(jsonPath("$.data.trainsByVersionGreaterThan[2].version").value("30"));
    }

    @Test
    public void supportsWhereFilter() throws Exception {
        final Train train1 = factoryService.getTrainFactory().createBaseTrain(1, DATE, 10L).getFirst();
        final Train train2 = factoryService.getTrainFactory().createBaseTrain(2, DATE, 20L).getFirst();

        train1.cancelled = true;
        train2.cancelled = false;
        trainRepository.saveAll(List.of(train1, train2));

        final ResultActions result = this.query("""
                {
                    trainsByVersionGreaterThan(version: "5", where: { cancelled: { equals: false } }) {
                        trainNumber
                    }
                }""");

        result.andExpect(jsonPath("$.data.trainsByVersionGreaterThan.length()").value(1));
        result.andExpect(jsonPath("$.data.trainsByVersionGreaterThan[0].trainNumber").value(2));
    }
}

