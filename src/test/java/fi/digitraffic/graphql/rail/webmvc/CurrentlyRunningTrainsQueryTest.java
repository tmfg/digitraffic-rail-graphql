package fi.digitraffic.graphql.rail.webmvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import fi.digitraffic.graphql.rail.entities.Train;
import fi.digitraffic.graphql.rail.repositories.TrainRepository;

/**
 * Integration tests for the currentlyRunningTrains query.
 */
public class CurrentlyRunningTrainsQueryTest extends BaseWebMVCTest {

    private static final ZoneId HELSINKI = ZoneId.of("Europe/Helsinki");

    @Autowired
    private TrainRepository trainRepository;

    @Test
    public void returnsRunningTrains() throws Exception {
        final LocalDate today = LocalDate.now(HELSINKI);

        final Train train1 = factoryService.getTrainFactory().createBaseTrain(1, today).getFirst();
        final Train train2 = factoryService.getTrainFactory().createBaseTrain(2, today).getFirst();
        final Train train3 = factoryService.getTrainFactory().createBaseTrain(3, today).getFirst();

        train1.runningCurrently = true;
        train2.runningCurrently = false;
        train3.runningCurrently = true;
        trainRepository.saveAll(List.of(train1, train2, train3));

        final ResultActions result = this.query("""
                {
                    currentlyRunningTrains {
                        trainNumber
                        runningCurrently
                    }
                }""");

        result.andExpect(jsonPath("$.data.currentlyRunningTrains.length()").value(2));
    }

    @Test
    public void includesYesterdayTrains() throws Exception {
        final LocalDate today = LocalDate.now(HELSINKI);
        final LocalDate yesterday = today.minusDays(1);

        final Train todayTrain = factoryService.getTrainFactory().createBaseTrain(1, today).getFirst();
        final Train yesterdayTrain = factoryService.getTrainFactory().createBaseTrain(2, yesterday).getFirst();

        todayTrain.runningCurrently = true;
        yesterdayTrain.runningCurrently = true;
        trainRepository.saveAll(List.of(todayTrain, yesterdayTrain));

        final ResultActions result = this.query("""
                {
                    currentlyRunningTrains {
                        trainNumber
                        departureDate
                    }
                }""");

        result.andExpect(jsonPath("$.data.currentlyRunningTrains.length()").value(2));
    }

    @Test
    public void excludesOldDates() throws Exception {
        final LocalDate today = LocalDate.now(HELSINKI);
        final LocalDate twoDaysAgo = today.minusDays(2);

        final Train todayTrain = factoryService.getTrainFactory().createBaseTrain(1, today).getFirst();
        final Train oldTrain = factoryService.getTrainFactory().createBaseTrain(2, twoDaysAgo).getFirst();

        todayTrain.runningCurrently = true;
        oldTrain.runningCurrently = true;
        trainRepository.saveAll(List.of(todayTrain, oldTrain));

        final ResultActions result = this.query("""
                {
                    currentlyRunningTrains {
                        trainNumber
                    }
                }""");

        result.andExpect(jsonPath("$.data.currentlyRunningTrains.length()").value(1));
        result.andExpect(jsonPath("$.data.currentlyRunningTrains[0].trainNumber").value(1));
    }

    @Test
    public void returnsEmptyWhenNoneRunning() throws Exception {
        final LocalDate today = LocalDate.now(HELSINKI);

        final Train train = factoryService.getTrainFactory().createBaseTrain(1, today).getFirst();
        train.runningCurrently = false;
        trainRepository.save(train);

        final ResultActions result = this.query("""
                {
                    currentlyRunningTrains {
                        trainNumber
                    }
                }""");

        result.andExpect(jsonPath("$.data.currentlyRunningTrains").isEmpty());
    }

    @Test
    public void supportsWhereFilter() throws Exception {
        final LocalDate today = LocalDate.now(HELSINKI);

        final Train train1 = factoryService.getTrainFactory().createBaseTrain(1, today).getFirst();
        final Train train2 = factoryService.getTrainFactory().createBaseTrain(2, today).getFirst();

        train1.runningCurrently = true;
        train1.timetableType = Train.TimetableType.ADHOC;
        train2.runningCurrently = true;
        train2.timetableType = Train.TimetableType.REGULAR;
        trainRepository.saveAll(List.of(train1, train2));

        final ResultActions result = this.query("""
                {
                    currentlyRunningTrains(where: { timetableType: { equals: "ADHOC" } }) {
                        trainNumber
                        timetableType
                    }
                }""");

        result.andExpect(jsonPath("$.data.currentlyRunningTrains.length()").value(1));
        result.andExpect(jsonPath("$.data.currentlyRunningTrains[0].trainNumber").value(1));
    }
}

